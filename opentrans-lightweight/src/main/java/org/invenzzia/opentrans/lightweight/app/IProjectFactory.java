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

import org.invenzzia.opentrans.visitons.Project;

/**
 * These factories are responsible for delivering the transportation
 * simulation projects.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IProjectFactory {
	/**
	 * Creates a new project and returns it.
	 * 
	 * @return 
	 */
	public Project createProject();
}
