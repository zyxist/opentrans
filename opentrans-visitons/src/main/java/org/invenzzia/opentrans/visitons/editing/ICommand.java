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

package org.invenzzia.opentrans.visitons.editing;

import org.invenzzia.opentrans.visitons.Project;

/**
 * Implementation of the 'command' design pattern. Represents various commands available to
 * perform on a project data model. Commands support 'undo' operation. Each command shall be
 * atomic: it either fully succeeds or fully fails, leaving the system in the consistent
 * state. We assume that the commands are executed sequentially on the data model.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ICommand {
	/**
	 * Executes the command on the data model.
	 * 
	 * @param project Data model.
	 */
	public void execute(Project project) throws Exception;
	/**
	 * Cancels the changes introduced by this command.
	 * 
	 * @param project 
	 */
	public void undo(Project project);
	/**
	 * Replays the changes introduced by this command.
	 * 
	 * @param project 
	 */
	public void redo(Project project);
}
