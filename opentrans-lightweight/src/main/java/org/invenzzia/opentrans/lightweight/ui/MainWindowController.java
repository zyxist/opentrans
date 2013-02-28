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

package org.invenzzia.opentrans.lightweight.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.events.ProjectEvent;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialogController;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class MainWindowController {
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private EventBus eventBus;
	@Inject
	private BrandingModel brandingModel;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Provider<ResizeDialogController> resizeDialogControllerProvider;
	/**
	 * Main window view managed by this controller.
	 */
	private MainWindow mainWindow;

	
	/**
	 * Sets the new main window for the application.
	 * 
	 * @param mainWindow The new main window for the application.
	 */
	public void setMainWindow(MainWindow mainWindow) {
		if(null != this.mainWindow) {
			this.eventBus.unregister(this);
		}
		this.mainWindow = mainWindow;
		if(null != this.mainWindow) {
			// Here we do not have to pay attention to the concurrency (I hope so).
			ProjectRecord record = new ProjectRecord();
			record.importData(this.projectHolder.getCurrentProject());
			this.createWindowTitle(record);
			this.eventBus.register(this);
		}
	}
	
	/**
	 * Returns the currently operated main window.
	 * 
	 * @return Current main window.
	 */
	public MainWindow getMainWindow() {
		return this.mainWindow;
	}
	
	/**
	 * Builds a title of the window, using the branding information and project
	 * information.
	 * 
	 * @param project Visitons project
	 */
	public void createWindowTitle(ProjectRecord project) {
		if(null != this.mainWindow) {
			StringBuilder title = new StringBuilder();
			title.append(project.getName()).append(" - ").append(this.brandingModel.getApplicationName())
				.append(" ").append(this.brandingModel.getApplicationVersion());
			this.mainWindow.setTitle(title.toString());
		}
	}
	
	@Subscribe
	public void notifyAboutProjectEvents(ProjectEvent event) {
		this.createWindowTitle(event.getProject());
	}
}
