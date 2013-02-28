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

package org.invenzzia.opentrans.lightweight.ui.tabs;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.events.ProjectEvent;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.forms.IFormAccessor;
import org.invenzzia.opentrans.lightweight.ui.forms.IFormHandler;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.UpdateProjectInfoCmd;

/**
 * Handles the project information editing form. Nothing special: just
 * some operations on a model + sending the notification about changing
 * the status, because some GUI components rely on the project name
 * and might be interested in updating their state.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectTabFormHandler implements IFormHandler {
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private History<ICommand> history;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private EventBus eventBus;

	@Override
	public void loadModel(final IFormAccessor form) {
		final ProjectRecord record = new ProjectRecord();
		final Project project = this.projectHolder.getCurrentProject();
		form.getField("name", JTextField.class).setEnabled(false);
		form.getField("author", JTextField.class).setEnabled(false);
		form.getField("description", JTextPane.class).setEnabled(false);
		this.collectData(project, record, form);
	}
	
	@InModelThread(asynchronous = true)
	public void collectData(Project project, ProjectRecord record, IFormAccessor accessor) {
		record.importData(project);
		this.updateGUI(record, accessor);
	}

	@InSwingThread(asynchronous = true)
	public void updateGUI(final ProjectRecord record, final IFormAccessor form) {
		form.getField("name", JTextField.class).setText(record.getName());
		form.getField("author", JTextField.class).setText(record.getAuthor());
		form.getField("description", JTextPane.class).setText(record.getDescription());
		form.getField("name", JTextField.class).setEnabled(true);
		form.getField("author", JTextField.class).setEnabled(true);
		form.getField("description", JTextPane.class).setEnabled(true);
	}

	@Override
	public boolean validateModel(IFormAccessor form) {
		return true;
	}

	@Override
	public void saveModel(IFormAccessor form) {
		final ProjectRecord record = new ProjectRecord();
		final Project project = this.projectHolder.getCurrentProject();
		record.setName(form.getField("name", JTextField.class).getText());
		record.setAuthor(form.getField("author", JTextField.class).getText());
		record.setDescription(form.getField("description", JTextPane.class).getText());
		try {
			this.history.execute(new UpdateProjectInfoCmd(record));
			this.eventBus.post(new ProjectEvent(this.getValidRecord(project)));
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot update project", exception);
		}
	}

	@Override
	public String getSuccessMessage() {
		return "The project information has been updated.";
	}

	@Override
	public String getFailureMessage() {
		return "Validation errors occurred. Please correct the data.";
	}
	
	@InModelThread(asynchronous = false)
	public ProjectRecord getValidRecord(Project project) {
		ProjectRecord record = new ProjectRecord();
		record.importData(project);
		return record;
	}

}
