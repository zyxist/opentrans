/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.editing.operations;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * The command allows updating the project details.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Update project information")
public class UpdateProjectInfoCmd implements ICommand {
	private final ProjectRecord record;
	/**
	 * Memento data created during 'undo' operation.
	 */
	private Object memento;

	public UpdateProjectInfoCmd(ProjectRecord record) {
		this.record = Preconditions.checkNotNull(record, "The project record cannot be empty.");
	}

	@Override
	public void execute(Project project) throws Exception {
		this.memento = project.getMemento(project);
		this.record.exportData(project, project);
	}

	@Override
	public void undo(Project project) {
		project.restoreMemento(this.memento, project);
	}

	@Override
	public void redo(Project project) {
		this.record.exportData(project, project);
	}
}
