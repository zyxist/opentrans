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

package org.invenzzia.opentrans.lightweight.app;

import com.google.inject.Inject;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.helium.history.IHistoryStrategy;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * This strategy executes all the history commands within the proper model
 * thread and in the context of the currently loaded project.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisitonsHistoryStrategy implements IHistoryStrategy<ICommand> {
	@Inject
	private IProjectHolder projectHolder;

	@Override
	@InModelThread(asynchronous = false)
	public void execute(ICommand command) throws Exception {
		command.execute(this.projectHolder.getCurrentProject());
	}

	@Override
	@InModelThread(asynchronous = true)
	public void undo(ICommand command) {
		command.undo(this.projectHolder.getCurrentProject());
	}

	@Override
	@InModelThread(asynchronous = true)
	public void redo(ICommand command) {
		command.redo(this.projectHolder.getCurrentProject());
	}

	@Override
	public ICommand getBaseCommand() {
		return new BaseCommand();
	}
}

@CommandDetails(name = "Initial state")
class BaseCommand implements ICommand {
	@Override
	public void execute(Project project) throws Exception {
		throw new UnsupportedOperationException("Not applicable for this command.");
	}

	@Override
	public void undo(Project project) {
		throw new UnsupportedOperationException("Not applicable for this command.");
	}

	@Override
	public void redo(Project project) {
		throw new UnsupportedOperationException("Not applicable for this command.");
	}
}