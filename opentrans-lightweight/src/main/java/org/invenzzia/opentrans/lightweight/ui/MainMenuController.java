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

package org.invenzzia.opentrans.lightweight.ui;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialogController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Implementation for all actions found in the main application menu.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MainMenuController {
	/**
	 * For managing the state of 'undo' and 'redo' buttons.
	 */
	@Inject
	private History<ICommand> history;
	/**
	 * Used for binding actions to menu items.
	 */
	@Inject
	private IActionScanner actionScanner;
	/**
	 * We need to open dialogs somehow.
	 */
	@Inject
	private IDialogBuilder dialogBuilder;
	
	@Inject
	private Provider<ResizeDialogController> resizeDialogControllerProvider;
	@Inject
	private Provider<MeanOfTransportController> meanOfTransportControllerProvider;
	@Inject
	private Provider<VehicleTypeController> vehicleTypeControllerProvider;
	/**
	 * The view scanned for menu items.
	 */
	private MainWindow view;
	
	/**
	 * Assigns the view to the controller and binds the actions.
	 * 
	 * @param mainWindow The view.
	 */
	public void setView(MainWindow mainWindow) {
		if(null != this.view) {
			this.actionScanner.clear(MainWindow.class, this.view);
		}
		this.view = mainWindow;
		if(null != this.view) {
			this.actionScanner.discoverActions(MainMenuController.class, this);
			this.actionScanner.bindComponents(MainWindow.class, this.view);
			this.updateButtonStates();
		}
	}

	@Action("quit")
	public void quitAction() {
		this.view.setVisible(false);
		WindowEvent wev = new WindowEvent(this.view, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	@Action("undo")
	public void undoAction() {
		try {
			this.history.undo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot undo", exception);
		}
	}
	
	@Action("redo")
	public void redoAction() {
		try {
			this.history.redo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot redo", exception);
		}
	}
	
	/**
	 * Shows the 'resize world' dialog.
	 */
	@Action("resizeWorld")
	public void resizeWorldAction() {
		ResizeDialog theDialog = this.dialogBuilder.createModalDialog(ResizeDialog.class);
		ResizeDialogController controller = this.resizeDialogControllerProvider.get();
		controller.setView(theDialog);
		theDialog.setVisible(true);
	}
	
	/**
	 * Shows the dialog for managing means of transport.
	 */
	@Action("meansOfTransport")
	public void meansOfTransportAction() {
		MeanOfTransportDialog dialog = this.dialogBuilder.createModalDialog(MeanOfTransportDialog.class);
		MeanOfTransportController controller = this.meanOfTransportControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}
	
	@Action("vehicleTypes")
	public void vehicleTypesAction() {
		VehicleTypeDialog dialog = this.dialogBuilder.createModalDialog(VehicleTypeDialog.class);
		VehicleTypeController controller = this.vehicleTypeControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}
	
	@Subscribe
	public void notifyHistoryChanges(HistoryChangedEvent<ICommand> event) {
		this.updateButtonStates();
	}
	
	/**
	 * Manages the 'undo' and 'redo' button states.
	 * 
	 * @param selectedIdx 
	 */
	private void updateButtonStates() {
		int selectedIdx = this.history.getPastOperationNum();
		if(0 == selectedIdx) {
			this.view.setUndoEnabled(false);
			if(this.history.getPastOperationNum() + this.history.getFutureOperationNum() == 0) {
				this.view.setRedoEnabled(false);
			} else {
				this.view.setRedoEnabled(true);
			}
		} else if(this.history.getPastOperationNum() + this.history.getFutureOperationNum() == selectedIdx) {
			this.view.setUndoEnabled(true);
			this.view.setRedoEnabled(false);
		} else {
			this.view.setUndoEnabled(true);
			this.view.setRedoEnabled(true);
		}
	}
}
