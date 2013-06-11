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
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.model.selectors.VehicleTypeSelectionModel;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;

/**
 * The controller manages the form for editing or adding a new vehicle,
 * handling the actions from the form and the buttons. Once the work
 * is finished, we can check whether the user accepted the changes by
 * clicking OK.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleEditorController {
	/**
	 * The managed view.
	 */
	private VehicleEditorDialog view;
	/**
	 * The edited model.
	 */
	private VehicleRecord model;
	/**
	 * The model for selecting the vehicle types.
	 */
	private VehicleTypeSelectionModel vehicleTypeSelectionModel;
	/**
	 * Has the edition been accepted?
	 */
	private boolean accepted = false;
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private IFormScanner formScanner;
	
	/**
	 * Sets the model that will be edited by this dialog.
	 * 
	 * @param model The edited model.
	 */
	public void setModel(VehicleRecord model) {
		this.model = model;
		this.installModel();
	}
	
	public void setVehicleTypeSelectionModel(VehicleTypeSelectionModel model) {
		this.vehicleTypeSelectionModel = model;
	}
	
	/**
	 * Returns the managed record.
	 * 
	 * @return Managed record.
	 */
	public VehicleRecord getModel() {
		return this.model;
	}
	
	/**
	 * Is the edition accepted? (equal to clicking 'OK').
	 * 
	 * @return True, if the user clicked OK.
	 */
	public boolean isAccepted() {
		return this.accepted;
	}
	
	
	/**
	 * Installs the managed view in the controller.
	 * 
	 * @param view 
	 */
	public void setView(VehicleEditorDialog view) {
		this.view = view;
		this.actionScanner.discoverActions(VehicleEditorController.class, this);
		this.actionScanner.bindComponents(VehicleEditorDialog.class, view);
		this.formScanner.discoverValidators(VehicleEditorController.class, this);
		this.formScanner.bindFields(VehicleEditorDialog.class, view);
		
		this.installModel();
	}
	
	@Action("okAction")
	public void okAction() {
		this.accepted = true;
		this.view.setVisible(false);
	}
	
	@Action("cancelAction")
	public void cancelAction() {
		this.accepted = false;
		this.view.setVisible(false);
	}
	
	@FormField(name="name")
	public void onNameChanged() {
		if(null != this.model) {
			this.model.setName(this.formScanner.getString("name"));
		}
	}

	@FormField(name="vehicleType")
	public void onVehicleTypeSelected() {
		if(null != this.model) {
			this.model.setVehicleTypeId(this.formScanner.getObject("vehicleType", VehicleTypeRecord.class).getId());
		}
	}
	
	/**
	 * If the model is set, the method updates the form fields with the model
	 * data.
	 */
	public void installModel() {
		if(null != this.model && this.formScanner.isDiscovered()) {
			this.formScanner.setString("name", this.model.getName());
			if(this.model.getVehicleTypeId() != IIdentifiable.NEUTRAL_ID) {
				this.formScanner.setObject("vehicleType", this.vehicleTypeSelectionModel.findById(this.model.getVehicleTypeId()));
			}
		}
	}
}
