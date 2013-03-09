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

package org.invenzzia.opentrans.lightweight.ui.dialogs.means;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportDialog.IItemListener;
import org.invenzzia.opentrans.lightweight.validator.Validators;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.UpdateMeansOfTransportCmd;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MeanOfTransportController implements IItemListener {
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
	private MeanOfTransportModel model;
	@Inject
	private IProjectHolder projectHolder;
	/**
	 * The managed view.
	 */
	private MeanOfTransportDialog view;
	
	/**
	 * Sets the view and binds the controller actions.
	 * 
	 * @param dialog 
	 */
	public void setView(MeanOfTransportDialog dialog) {
		this.view = Preconditions.checkNotNull(dialog);
		this.actionScanner.discoverActions(MeanOfTransportController.class, this);
		this.actionScanner.bindComponents(MeanOfTransportDialog.class, dialog);
		
		this.formScanner.discoverValidators(MeanOfTransportController.class, this);
		this.formScanner.bindFields(MeanOfTransportDialog.class, dialog);
		
		this.model.loadData(this.projectHolder.getCurrentProject());
		this.view.setModel(this.model);
		this.view.addItemListener(this);
	}
	
	@Action("okAction")
	public void okAction() {
		this.view.setVisible(false);
		UnitOfWork<MeanOfTransportRecord> unitOfWork = this.model.getUnitOfWork();
		try {
			if(!unitOfWork.isEmpty()) {
				this.history.execute(new UpdateMeansOfTransportCmd(unitOfWork));
			}
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot save means of transport", exception);
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
		NewMeanDialog subDialog = this.dialogBuilder.createModalDialog(NewMeanDialog.class);
		subDialog.setVisible(true);
		if(subDialog.isConfirmed()) {
			String enteredName = subDialog.getEnteredName();
			if(enteredName.isEmpty() || enteredName.length() > 30) {
				this.dialogBuilder.showError("Invalid name", "Invalid name of the new mean of transport.");
			} else {
				MeanOfTransportRecord record = new MeanOfTransportRecord();
				record.setName(subDialog.getEnteredName());
				record.setMaxSafeSpeedRadiusCoefficient(1.0);
				record.setOvertakingAllowed(true);
				record.setRollingFrictionCoefficient(0.005);
				record.setOvertakingPunishment(1.0);
				
				this.model.insertRecord(record);
			}
		}
	}

	@Action("removeAction")
	public void removeAction() {
		MeanOfTransportRecord record = this.view.getSelectedRecord();
		if(record.hasVehicleTypes()) {
			this.dialogBuilder.showInformation("Cannot remove", "This mean of transport has vehicle types assigned. Remove the vehicle types first.");
		} else {
			this.model.removeRecord(record);
		}
	}
	
	@FormField(name="name")
	public void onNameChanged() {
		if(this.formScanner.validate("name", Validators.lengthBetween(3, 30))) {
			MeanOfTransportRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setName(this.formScanner.getString("name"));
				this.model.updateRecord(record);
				this.model.fireContentChanged();
			}
		}
	}
	
	@FormField(name="rollingFrictionCoefficient")
	public void onRollingFrictionCoefficientChanged() {
		if(this.formScanner.validate("rollingFrictionCoefficient", Validators.isDouble())) {
			MeanOfTransportRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setRollingFrictionCoefficient(this.formScanner.getDouble("rollingFrictionCoefficient"));
				this.model.updateRecord(record);
			}
		}		
	}
	
	@FormField(name="maxSafeSpeedCoefficient")
	public void onMaxSafeSpeedCoefficientChanged() {
		if(this.formScanner.validate("maxSafeSpeedCoefficient", Validators.isDouble())) {
			MeanOfTransportRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setMaxSafeSpeedRadiusCoefficient(this.formScanner.getDouble("maxSafeSpeedCoefficient"));
				this.model.updateRecord(record);
			}
		}
	}
	
	@FormField(name="overtakingSpeedPunishment")
	public void onOvertakingSpeedPunishmentChanged() {
		if(this.formScanner.validate("overtakingSpeedPunishment", Validators.isDouble())) {
			MeanOfTransportRecord record = this.view.getSelectedRecord();
			if(null != record) {
				record.setOvertakingPunishment(this.formScanner.getDouble("overtakingSpeedPunishment"));
				this.model.updateRecord(record);
			}
		}
	}
	
	@FormField(name="allowVehicleOvertaking")
	public void onAllowVehicleOvertakingChanged() {
		boolean value = this.formScanner.getBoolean("allowVehicleOvertaking");
		if(!value) {
			this.view.setOvertakingSpeedPunishmentEnabled(false);
		} else {
			this.view.setOvertakingSpeedPunishmentEnabled(true);
		}
		MeanOfTransportRecord record = this.view.getSelectedRecord();
		if(null != record) {
			record.setOvertakingAllowed(value);
			this.model.updateRecord(record);
		}
	}

	@Override
	public void onItemSelected(MeanOfTransportDialog.ItemEvent event) {
		if(event.hasRecord()) {
			MeanOfTransportRecord record = event.getRecord();
			this.view.enableForm();
			this.formScanner.setString("name", record.getName());
			this.formScanner.setDouble("rollingFrictionCoefficient", record.getRollingFrictionCoefficient());
			this.formScanner.setDouble("maxSafeSpeedCoefficient", record.getMaxSafeSpeedRadiusCoefficient());
			this.formScanner.setDouble("overtakingSpeedPunishment", record.getOvertakingPunishment());
			this.formScanner.setBoolean("allowVehicleOvertaking", record.isOvertakingAllowed());
			if(!record.isOvertakingAllowed()) {
				this.view.setOvertakingSpeedPunishmentEnabled(false);
			}
			this.view.setHasVehicleTypes(record.hasVehicleTypes());
		} else {
			this.view.disableForm();
		}
	}
}
