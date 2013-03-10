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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog;

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

	/**
	 * The view managed by this controller.
	 */
	private VehicleTab view;

	public void setView(VehicleTab view) {
		this.view = view;
		this.actionScanner.discoverActions(VehicleTabController.class, this);
		this.actionScanner.bindComponents(VehicleTab.class, this.view);
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
}
