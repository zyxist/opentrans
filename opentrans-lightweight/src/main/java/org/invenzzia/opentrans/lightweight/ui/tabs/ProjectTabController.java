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
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.events.HistoryCommandReplayedEvent;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.ui.forms.FormController;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.ProjectEvent;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectTabController {
	/**
	 * The managed view.
	 */
	private ProjectTab view;
	/**
	 * The instantiated form handler.
	 */
	private ProjectTabFormHandler formHandler;
	/**
	 * How to obtain the form handler?
	 */
	@Inject
	private Provider<ProjectTabFormHandler> formHandlerProvider;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private EventBus eventBus;
	/**
	 * Child form controller.
	 */
	private FormController formController;

	public void setView(ProjectTab view) {
		this.view = view;
		this.formHandler = this.formHandlerProvider.get();
		this.formController = new FormController(this.formHandler);
		this.formController.setManagedPanel(view);
	}
	
	@Subscribe
	public void notifyHistoryChanged(HistoryCommandReplayedEvent<ICommand> event) {
		this.formController.refresh();
		this.eventBus.post(new ProjectEvent(this.getUpdatedRecord(this.projectHolder.getCurrentProject())));		
	}
	
	@InModelThread(asynchronous = false)
	public ProjectRecord getUpdatedRecord(Project project) {
		ProjectRecord record = new ProjectRecord();
		record.importData(project, project);
		return record;
	}
}
