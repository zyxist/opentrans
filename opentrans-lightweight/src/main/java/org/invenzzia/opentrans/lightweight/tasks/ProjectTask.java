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

package org.invenzzia.opentrans.lightweight.tasks;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.invenzzia.opentrans.lightweight.Application;
import org.invenzzia.opentrans.lightweight.app.IProjectFactory;
import org.invenzzia.opentrans.lightweight.exception.TaskException;
import org.invenzzia.opentrans.lightweight.binding.NewProject;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectTask implements ITask {
	@Inject
	@NewProject
	private IProjectFactory projectFactory;
	@Inject
	private EventBus eventBus;
	@Inject
	private Application application;

	@Override
	public void startup() throws TaskException {
		if(this.application.getStartupMode() == Application.STARTUP_EMPTY_PROJECT) {
			application.setCurrentProject(this.projectFactory.createProject());
		} else {
			application.setCurrentProject(this.projectFactory.createProject());
		//	application.setCurrentProject(this.projectFactory.openExistingProject(application.getProjectPath()));
		}
	}

	@Override
	public void shutdown() throws TaskException {
	}
}
