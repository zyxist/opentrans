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

package org.invenzzia.opentrans.lightweight.ui.tabs.vehicles;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.model.visitons.MeanSelectionModel;
import org.invenzzia.opentrans.lightweight.model.visitons.VehicleTypeSelectionModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;
import org.invenzzia.opentrans.visitons.data.manager.VehicleManager;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.AddVehicleCmd;
import org.invenzzia.opentrans.visitons.editing.operations.EditVehicleCmd;
import org.invenzzia.opentrans.visitons.editing.operations.RemoveVehicleCmd;

/**
 * Responds for the high-level user events from the vehicle tab.
 * In the controller, we have an access to the dependency injection etc.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTabController {
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Provider<MeanOfTransportController> meanOfTransportControllerProvider;
	@Inject
	private Provider<VehicleTypeController> vehicleTypeControllerProvider;
	@Inject
	private Provider<VehicleEditorController> vehicleEditorControllerProvider;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private History<ICommand> history;
	@Inject
	private VehicleTabModel model;
	@Inject
	private Provider<MeanSelectionModel> meanSelectionModelProvider;
	@Inject
	private Provider<VehicleTypeSelectionModel> vehicleTypeSelectionModelProvider;

	/**
	 * The view managed by this controller.
	 */
	private VehicleTab view;
	/**
	 * The model of the table.
	 */
	private VehicleTableModel tableModel;
	/**
	 * The model for the selecting the means of transport.
	 */
	private MeanSelectionModel meanSelectionModel;

	public void setView(VehicleTab view) {
		this.view = view;
		this.actionScanner.discoverActions(VehicleTabController.class, this);
		this.actionScanner.bindComponents(VehicleTab.class, this.view);
		
		this.meanSelectionModel = this.meanSelectionModelProvider.get();
		this.model.addSelectionModel(this.meanSelectionModel);
		this.model.addBatchModelListener(this.view);
		this.view.setVehicleTableModel(this.tableModel = new VehicleTableModel());
		this.view.setMeanOfTransportModel(this.meanSelectionModel);
		this.view.setSelectedRecord(null);
		this.model.updateData();
	}
	
	/**
	 * We need to check if there are any vehicle types before trying
	 * to add a vehicle.
	 * 
	 * @return True, if there are any vehicle types.
	 */
	@InModelThread(asynchronous = false)
	public boolean hasVehicleTypes(final Project project) {
		return project.getVehicleTypeManager().size() > 0;
	}

	@Action("manageMeans")
	public void manageMeansAction() {
		MeanOfTransportDialog dialog = this.dialogBuilder.createModalDialog(MeanOfTransportDialog.class);
		MeanOfTransportController controller = this.meanOfTransportControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}

	@Action("manageVehicleTypes")
	public void manageVehicleTypesAction() {
		VehicleTypeDialog dialog = this.dialogBuilder.createModalDialog(VehicleTypeDialog.class);
		VehicleTypeController controller = this.vehicleTypeControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}
	
	@Action("addAction")
	public void addAction() {
		Project project = this.projectHolder.getCurrentProject();
		if(!this.hasVehicleTypes(project)) {
			this.dialogBuilder.showWarning("No vehicle types", "You must add some means of transport and vehicle types before adding a vehicle.");
			return;
		}
		VehicleEditorDialog dialog = this.dialogBuilder.createModalDialog(VehicleEditorDialog.class);
		
		VehicleTypeSelectionModel selectionModel = this.vehicleTypeSelectionModelProvider.get();
		selectionModel.installModel(selectionModel.updateModel(project));
		dialog.setVehicleTypeModel(selectionModel);
		VehicleEditorController controller = this.vehicleEditorControllerProvider.get();
		controller.setVehicleTypeSelectionModel(selectionModel);
		controller.setModel(new VehicleRecord());
		controller.setView(dialog);
		dialog.setVisible(true);
		if(controller.isAccepted()) {
			try {
				this.history.execute(new AddVehicleCmd(controller.getModel()));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot add a vehicle", exception);
			}
		}
	}
	
	@Action("editAction")
	public void editAction() {
		Project project = this.projectHolder.getCurrentProject();
		VehicleRecord record = this.view.getSelectedRecord();
		if(null == record) {
			this.dialogBuilder.showError("No vehicle selected", "Please select a vehicle.");
			return;
		}
		VehicleEditorDialog dialog = this.dialogBuilder.createModalDialog(VehicleEditorDialog.class);
		
		VehicleTypeSelectionModel selectionModel = this.vehicleTypeSelectionModelProvider.get();
		selectionModel.installModel(selectionModel.updateModel(project));
		dialog.setVehicleTypeModel(selectionModel);
		VehicleEditorController controller = this.vehicleEditorControllerProvider.get();
		controller.setVehicleTypeSelectionModel(selectionModel);
		controller.setModel(record);
		controller.setView(dialog);
		dialog.setVisible(true);
		if(controller.isAccepted()) {
			try {
				this.history.execute(new EditVehicleCmd(controller.getModel()));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot edit a vehicle", exception);
			}
		}
	}
	
	@Action("removeAction")
	public void removeAction() {
		VehicleRecord record = this.view.getSelectedRecord();
		if(null == record) {
			this.dialogBuilder.showError("No vehicle selected", "Please select a vehicle.");
			return;
		}
		if(this.dialogBuilder.showConfirmDialog("Question", "Do you really want to delete vehicle '"+record.getName()+"'?")) {
			try {
				this.history.execute(new RemoveVehicleCmd(record));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot remove a vehicle", exception);
			}
		}
	}
	
	@Action("locateAction")
	public void locateAction() {
		this.dialogBuilder.showInformation("Not supported", "This operation is not supported yet.");
	}
	
	/**
	 * When the history is changed, we must probably refresh the data in the tab
	 * in order to be up-to-date.
	 * 
	 * @param event History change event.
	 */
	@Subscribe
	public void notifyHistoryChanged(HistoryChangedEvent<ICommand> event) {
		this.model.updateData();
	}
}
