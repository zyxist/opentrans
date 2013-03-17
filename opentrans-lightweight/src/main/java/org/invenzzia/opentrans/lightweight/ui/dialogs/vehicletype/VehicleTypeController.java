/*
 * Copyright (C) 2013 zyxist
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.model.visitons.MeanSelectionModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog.IItemListener;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog.ItemEvent;
import org.invenzzia.opentrans.lightweight.validator.Validators;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.UpdateVehicleTypesCmd;

/**
 *
 * @author zyxist
 */
public class VehicleTypeController implements IItemListener {
	/**
	 * Required to save the state at the end of work.
	 */
	@Inject
	private History<ICommand> history;
	/**
	 * Provides dialog creation.
	 */
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * Binds the view buttons to the controller actions.
	 */
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private IFormScanner formScanner;
	@Inject
	private VehicleTypeModel model;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private Provider<MeanSelectionModel> meanSelectionModelProvider;
	/**
	 * The managed view.
	 */
	private VehicleTypeDialog view;
	/**
	 * Available means of transport.
	 */
	private MeanSelectionModel meanSelectionModel;
	
	/**
	 * Sets the view and binds the controller actions.
	 * 
	 * @param dialog 
	 */
	public void setView(VehicleTypeDialog dialog) {
		this.view = Preconditions.checkNotNull(dialog);
		this.actionScanner.discoverActions(VehicleTypeController.class, this);
		this.actionScanner.bindComponents(VehicleTypeDialog.class, dialog);
		
		this.formScanner.discoverValidators(VehicleTypeController.class, this);
		this.formScanner.bindFields(VehicleTypeDialog.class, dialog);
		
		Project project = this.projectHolder.getCurrentProject();
		
		this.meanSelectionModel = this.meanSelectionModelProvider.get();
		this.meanSelectionModel.loadData(project);		
		this.model.loadData(this.projectHolder.getCurrentProject());
		
		this.view.setModel(this.model);
		this.view.setMeanSelectionModel(this.meanSelectionModel);
		this.view.addItemListener(this);
		this.view.disableForm();
	}
	
	@Action("okAction")
	public void okAction() {
		this.view.setVisible(false);
		UnitOfWork<VehicleTypeRecord> unitOfWork = this.model.getUnitOfWork();
		try {
			if(!unitOfWork.isEmpty()) {
				this.history.execute(new UpdateVehicleTypesCmd(unitOfWork));
			}
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot save vehicle types", exception);
		}
	}
	
	@Action("cancelAction")
	public void cancelAction() {
		this.view.setVisible(false);
	}

	@Action("helpAction")
	public void helpAction() {
		
	}
	
	@Action("addAction")
	public void addAction() {
		MeanSelectionModel msm = this.meanSelectionModelProvider.get();
		msm.loadData(this.projectHolder.getCurrentProject());
		if(msm.getSize() == 0) {
			this.dialogBuilder.showWarning("No means of transport", "Please add some means of transport first.");
		} else {
		
			NewVehicleTypeDialog subDialog = this.dialogBuilder.createModalDialog(NewVehicleTypeDialog.class);
			subDialog.setMeanOfTransportModel(msm);
			subDialog.setVisible(true);
			if(subDialog.isConfirmed()) {
				String enteredName = subDialog.getEnteredName();
				if(enteredName.isEmpty() || enteredName.length() > 30) {
					this.dialogBuilder.showError("Invalid name", "Invalid name of the new vehicleType.");
				} else {
					VehicleTypeRecord record = new VehicleTypeRecord();
					record.setName(subDialog.getEnteredName());
					record.setMeanOfTransport(subDialog.getEnteredMeanOfTransport().getId());
					record.setLength(12.0);
					record.setMass(10000);
					record.setEnginePower(300000);
					record.setMaximumCapacity(180);
					record.setNumberOfSegments(1);
					record.setPassengerExchangeRatio(110);

					this.model.insertRecord(record);
				}
			}
		}
	}

	@Action("removeAction")
	public void removeAction() {
		VehicleTypeRecord record = this.view.getSelectedRecord();
		if(record.hasVehicles()) {
			this.dialogBuilder.showInformation("Cannot remove", "This vehicle type has vehicles assigned. Remove the vehicles first.");
		} else {
			this.model.removeRecord(record);
		}
	}
	
	@FormField(name="name")
	public void onNameChanged() {
		if(this.formScanner.validate("name", Validators.lengthBetween(1, 30))) {
			VehicleTypeRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setName(this.formScanner.getString("name"));
				this.model.updateRecord(record);
				this.model.fireContentChanged();
			}
		}
	}
	
	@FormField(name="meanOfTransport")
	public void onMeanOfTransportChange() {
		VehicleTypeRecord record = this.view.getSelectedRecord();
		if(null != record) {
			record.setMeanOfTransport(this.formScanner.getObject("meanOfTransport", MeanOfTransportRecord.class).getId());
		}
	}
	
	@FormField(name="length")
	public void onLengthChange() {
		if(this.formScanner.validate("length", Validators.isDouble(), Validators.range(0.0, 100.0))) {
			VehicleTypeRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setLength(this.formScanner.getDouble("length"));
			}
		}
	}
	
	@FormField(name="mass")
	public void onMassChange() {
		if(this.formScanner.validate("mass", Validators.isInteger(), Validators.range(0, 200000))) {
			VehicleTypeRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setMass(this.formScanner.getInt("mass"));
			}
		}
	}
	
	@FormField(name="enginePower")
	public void onEnginePowerChange() {
		if(this.formScanner.validate("enginePower", Validators.isInteger(), Validators.range(0, 1000000))) {
			VehicleTypeRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setEnginePower(this.formScanner.getInt("enginePower"));
			}
		}
	}
	
	@FormField(name="maximumCapacity")
	public void onMaximumCapacityChanged() {
		if(this.formScanner.validate("maximumCapacity", Validators.isInteger(), Validators.range(0, 1000))) {
			VehicleTypeRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setMaximumCapacity(this.formScanner.getInt("maximumCapacity"));
			}
		}
	}
	
	@FormField(name="numberOfSegments")
	public void onNumberOfSegmentsChanged() {
		VehicleTypeRecord record = this.view.getSelectedRecord();
		if(null != record) {
			record.setNumberOfSegments(this.formScanner.getInt("numberOfSegments"));
		}
	}
	
	@FormField(name="exchangeRatio")
	public void onExchangeRatioChanged() {
		VehicleTypeRecord record = this.view.getSelectedRecord();
		if(null != record) {
			record.setPassengerExchangeRatio(this.formScanner.getInt("exchangeRatio"));
		}
	}
	
	@Override
	public void onItemSelected(ItemEvent event) {
		if(event.hasRecord()) {
			VehicleTypeRecord record = event.getRecord();
			this.view.enableForm();
			this.formScanner.setString("name", record.getName());
			this.formScanner.setObject("meanOfTransport", this.meanSelectionModel.findById(record.getMeanOfTransportId()));
			this.formScanner.setDouble("length", record.getLength());
			this.formScanner.setInt("mass", record.getMass());
			this.formScanner.setInt("enginePower", record.getEnginePower());
			this.formScanner.setInt("maximumCapacity", record.getMaximumCapacity());
			this.formScanner.setInt("numberOfSegments", record.getNumberOfSegments());
			this.formScanner.setInt("exchangeRatio", record.getPassengerExchangeRatio());
		} else {
			this.view.disableForm();
		}
	}
}
