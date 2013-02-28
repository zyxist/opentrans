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

import com.google.inject.Inject;
import javax.swing.JTextField;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.forms.IFormAccessor;
import org.invenzzia.opentrans.lightweight.ui.forms.IFormHandler;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.AddStopCmd;
import org.invenzzia.opentrans.visitons.editing.operations.EditStopCmd;

/**
 * Handles the logic of creating and updating the stops.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopFormHandler implements IFormHandler {
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private History<ICommand> history;
	/**
	 * Data model, if we are editing an existing record.
	 */
	private StopRecord model;
	
	/**
	 * Sets the data model. By calling this method, we are activating
	 * the record editing mode.
	 * 
	 * @param record 
	 */
	public void setModel(StopRecord record) {
		this.model = record;
	}

	@Override
	public String getSuccessMessage() {
		return "The stop has been successfully created.";
	}

	@Override
	public String getFailureMessage() {
		return "Cannot create a stop with the given name.";
	}

	@Override
	public void loadModel(IFormAccessor form) {
		if(null != this.model) {
			form.getField("name", JTextField.class).setText(this.model.getName());
		}
	}

	@Override
	public boolean validateModel(IFormAccessor form) {
		String name = form.getField("name", JTextField.class).getText();
		if(name.length() < 3 || name.length() >= 100) {
			form.setInvalid("name", "Stop name must contain 3 to 100 characters.");
			return false;
		}
		boolean notUnique = true;
		if(null != this.model) {
			notUnique = !name.equals(this.model.getName()) && this.nameExists(name);
		} else {
			notUnique = this.nameExists(name);
		}
		if(notUnique) {
			form.setInvalid("name", "The stop name must be unique.");
			return false;
		}
		return true;
	}

	@Override
	public void saveModel(IFormAccessor form) {
		if(null != this.model) {
			this.model.setName(form.getField("name", JTextField.class).getText());
			try {
				this.history.execute(new EditStopCmd(this.model));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot update the stop", exception);
			}
		} else {
			StopRecord stop = new StopRecord();
			stop.setName(form.getField("name", JTextField.class).getText());

			try {
				this.history.execute(new AddStopCmd(stop));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot create the stop", exception);
			}
		}
	}
	
	/**
	 * Executed in the model thread - it verifies whether the stop name is
	 * already in use.
	 * 
	 * @param name Stop name.
	 * @return True, if such a name does not exist so far.
	 */
	@InModelThread(asynchronous=false)
	private boolean nameExists(String name) {
		Project project = this.projectHolder.getCurrentProject();
		return project.getStopManager().nameExists(name);
	}
}
