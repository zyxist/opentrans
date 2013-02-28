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

package org.invenzzia.opentrans.lightweight.events;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;

/**
 * Events related to changing the project status in the application.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectEvent {
	private final ProjectRecord project;
	
	public ProjectEvent(ProjectRecord project) {
		this.project = Preconditions.checkNotNull(project, "Cannot emit a project event without a project record!");
	}
	
	/**
	 * Returns the project object this event is related to.
	 * 
	 * @return 
	 */
	public ProjectRecord getProject() {
		return this.project;
	}
}
