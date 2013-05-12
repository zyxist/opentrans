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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.invenzzia.opentrans.lightweight.Application;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.events.ApplicationCloseEvent;
import org.invenzzia.opentrans.lightweight.events.StatusEvent;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialogController;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.events.ProjectEvent;

/**
 * The basic controller that handles the main application window, and deals with
 * such details as updating its title and closing.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class MainWindowController {
	private static final String EXIT_QUESTION_MESSAGE = "Do you really want to exit OpenTrans? Unsaved changes will be lost.";
	
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private EventBus eventBus;
	@Inject
	private BrandingModel brandingModel;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Application application;
	@Inject
	private Provider<ResizeDialogController> resizeDialogControllerProvider;
	/**
	 * Main window view managed by this controller.
	 */
	private MainWindow mainWindow;
	/**
	 * Handles main window closing process.
	 */
	private MainWindowListener mainWindowListener = new MainWindowListener();

	
	/**
	 * Sets the new main window for the application.
	 * 
	 * @param mainWindow The new main window for the application.
	 */
	public void setMainWindow(MainWindow mainWindow) {
		if(null != this.mainWindow) {
			this.mainWindow.removeWindowListener(this.mainWindowListener);
			this.eventBus.unregister(this);
		}
		this.mainWindow = mainWindow;
		if(null != this.mainWindow) {
			this.mainWindow.addWindowListener(this.mainWindowListener);
			
			// Here we do not have to pay attention to the concurrency (I hope so).
			ProjectRecord record = new ProjectRecord();
			Project currentProject = this.projectHolder.getCurrentProject();
			record.importData(currentProject, currentProject);
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
	
	/**
	 * API method that allows closing OpenTrans.
	 */
	public void handleClosing() {
		if(this.dialogBuilder.showConfirmDialog("Exit OpenTrans", EXIT_QUESTION_MESSAGE)) {
			this.eventBus.post(new ApplicationCloseEvent());
			this.mainWindow.dispose();
			this.application.close();
		}
	}
	
	@Subscribe
	public void notifyAboutStatusEvents(StatusEvent event) {
		this.mainWindow.setStatusMessage(event.getStatusMessage());
	}
	
	@Subscribe
	public void notifyAboutProjectEvents(ProjectEvent event) {
		this.createWindowTitle(event.getProject());
	}
	
	class MainWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			handleClosing();
		}
	}
}
