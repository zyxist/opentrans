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

package org.invenzzia.opentrans.lightweight.ui.dialogs.servicetypes;

import com.google.inject.Inject;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.ui.AbstractDialogController;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Controller for the service type dialog.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ServiceTypeDialogController extends AbstractDialogController<ServiceTypeDialog> {
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
	
	@Override
	public void setView(ServiceTypeDialog dialog) {
		super.setView(dialog);
		
		this.actionScanner.discoverActions(ServiceTypeDialogController.class, this);
		this.actionScanner.bindComponents(ServiceTypeDialog.class, dialog);
		
		this.formScanner.discoverValidators(ServiceTypeDialogController.class, this);
		this.formScanner.bindFields(ServiceTypeDialog.class, dialog);
	}
	
	@Action("okAction")
	public void okAction() {
		this.dialog.dispose();
	}

	@Action("cancelAction")
	public void cancelAction() {
		this.dialog.dispose();
	}
	
}
