/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import org.invenzzia.helium.events.HistoryCommandReplayedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.FormDialogCloser;
import org.invenzzia.opentrans.lightweight.ui.forms.FormController;
import org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure.InfrastructureTab.IInfrastructureTabListener;
import org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure.InfrastructureTab.InfrastructureTabEvent;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.data.manager.StopManager;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.RemoveStopCmd;

/**
 * Manages the logic of the infrastructure tab.
 * 
 * @author Tomasz Jędrzejewski
 */
public class InfrastructureTabController implements IInfrastructureTabListener {
	private InfrastructureTab view;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Provider<StopFormHandler> stopFormHandlerProvider;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private History<ICommand> history;
	
	public void setView(InfrastructureTab view) {
		this.view = view;
		this.view.addInfrastructureTabListener(this);
		this.refreshStopList();
	}
	
	/**
	 * Finds all the stops and passes them back to the GUI.
	 */
	@InModelThread(asynchronous = true)
	public void refreshStopList() {
		Project project = this.projectHolder.getCurrentProject();
		StopManager stopManager = project.getStopManager();
		List<StopRecord> records = new ArrayList<>(stopManager.size());
		for(Stop stop: stopManager) {
			StopRecord record = new StopRecord();
			record.importData(stop, project);
			records.add(record);
		}
		this.populateStopListTable(records);
	}
	
	/**
	 * Passes the list generated in the model thread to GUI.
	 * @param stopList 
	 */
	@InSwingThread(asynchronous = true)
	public void populateStopListTable(List<StopRecord> stopList) {
		this.view.populateStopListTable(ImmutableList.copyOf(stopList));
	}


	@Override
	public void addStopButtonClicked() {
		StopFormHandler formHandler = this.stopFormHandlerProvider.get();
		FormController formController = new FormController(formHandler);
		StopEditorDialog dialog = this.dialogBuilder.createModalDialog(StopEditorDialog.class);
		formController.addFormStatusListener(new FormDialogCloser(dialog));
		formController.setManagedPanel(dialog);
		dialog.setVisible(true);
		this.refreshStopList();
	}

	@Override
	public void editStopButtonClicked(InfrastructureTabEvent event) {
		StopFormHandler formHandler = this.stopFormHandlerProvider.get();
		formHandler.setModel(event.getRecord());
		FormController formController = new FormController(formHandler);
		StopEditorDialog dialog = this.dialogBuilder.createModalDialog(StopEditorDialog.class);
		formController.addFormStatusListener(new FormDialogCloser(dialog));
		formController.setManagedPanel(dialog);
		dialog.setVisible(true);
		this.refreshStopList();
	}

	@Override
	public void removeStopButtonClicked(InfrastructureTabEvent event) {
		if(this.dialogBuilder
			.showConfirmDialog("Question", "Do you really want to remove the stop '"+event.getRecord().getName()+"'?")
		) {
			try {
				this.history.execute(new RemoveStopCmd(event.getRecord()));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot remove the stop", exception);
			}
			this.refreshStopList();
		}
	}

	@Override
	public void stopSelected(InfrastructureTabEvent event) {
		StopRecord record = event.getRecord();
		if(null == record) {
			this.view.setDetailedStopName("");
		} else {
			this.view.setDetailedStopName(record.getName());
		}
	}
	
	/**
	 * This might indicate that some stops no longer exist, we must refresh the list.
	 * 
	 * @param event 
	 */
	@Subscribe
	public void notifyHistoryChanged(HistoryCommandReplayedEvent<ICommand> event) {
		this.refreshStopList();
	}
}
