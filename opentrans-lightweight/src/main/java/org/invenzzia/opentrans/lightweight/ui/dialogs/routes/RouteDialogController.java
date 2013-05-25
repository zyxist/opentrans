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
package org.invenzzia.opentrans.lightweight.ui.dialogs.routes;

import org.invenzzia.opentrans.lightweight.model.lists.RouteModel;
import com.google.inject.Inject;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.ui.AbstractDialogController;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.routes.RouteDialog.IItemListener;
import org.invenzzia.opentrans.lightweight.validator.Validators;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.UpdateRoutesCmd;
import org.invenzzia.opentrans.visitons.types.RouteNumber;

/**
 * Controller for the route management dialog.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RouteDialogController extends AbstractDialogController<RouteDialog> implements IItemListener {
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
	private RouteModel model;
	@Inject
	private IProjectHolder projectHolder;
	
	/**
	 * Sets the view and binds the controller actions.
	 * 
	 * @param view The dialog view.
	 */
	@Override
	public void setView(RouteDialog dialog) {
		super.setView(dialog);
		this.actionScanner.discoverActions(RouteDialogController.class, this);
		this.actionScanner.bindComponents(RouteDialog.class, dialog);
		
		this.formScanner.discoverValidators(RouteDialogController.class, this);
		this.formScanner.bindFields(RouteDialog.class, dialog);
		
		this.model.loadData(this.projectHolder.getCurrentProject());
		this.dialog.setModel(this.model);
		this.dialog.addItemListener(this);
		this.dialog.disableForm();
	}
	
	@Action("okAction")
	public void okAction() {
		UnitOfWork<RouteRecord> unitOfWork = this.model.getUnitOfWork();
		try {
			if(!unitOfWork.isEmpty()) {
				this.history.execute(new UpdateRoutesCmd(unitOfWork));
			}
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot save means of transport", exception);
		}
		this.dialog.dispose();
	}
	
	@Action("cancelAction")
	public void cancelAction() {
		this.dialog.dispose();
	}
	
	@Action("helpAction")
	public void helpAction() {
		
	}

	@Action("addAction")
	public void addAction() {
		NewRouteDialog subDialog = this.dialogBuilder.createModalDialog(NewRouteDialog.class);
		subDialog.setVisible(true);
		if(subDialog.isConfirmed()) {
			String enteredName = subDialog.getEnteredNumber();
			if(enteredName.isEmpty() || enteredName.length() > 10) {
				this.dialogBuilder.showError("Invalid name", "Invalid route number.");
			} else {
				RouteRecord record = new RouteRecord();
				record.setNumber(RouteNumber.parseString(enteredName));
				record.setDescription("");
				this.model.insertRecord(record);
			}
		}
	}
	
	@Action("removeAction")
	public void removeAction() {
		RouteRecord record = this.dialog.getSelectedRecord();
		if(null != record) {
			this.model.removeRecord(record);
			this.dialog.disableForm();
			this.formScanner.clear();
		}
	}
	
	@FormField(name="number")
	public void onNumberChanged() {
		if(formScanner.validate("number", Validators.lengthBetween(1, 10))) {
			RouteRecord record = this.dialog.getSelectedRecord();
			if(null != record) {
				record.setNumber(RouteNumber.parseString(this.formScanner.getString("number")));
				this.model.updateRecord(record);
			}
		}
	}
	
	@FormField(name="description")
	public void onDescriptionChanged() {
		if(formScanner.validate("number", Validators.lengthBetween(0, 300))) {
			RouteRecord record = this.dialog.getSelectedRecord();
			if(null != record) {
				record.setDescription(this.formScanner.getString("description"));
				this.model.updateRecord(record);
			}
		}
	}

	@Override
	public void onItemSelected(RouteDialog.ItemEvent event) {
		if(event.hasRecord()) {
			RouteRecord record = event.getRecord();
			this.dialog.enableForm();
			
			this.formScanner.setString("number", record.getNumber().toString());
			this.formScanner.setString("description", record.getDescription());
		} else {
			this.dialog.disableForm();
			this.formScanner.clear();
		}
	}
}
