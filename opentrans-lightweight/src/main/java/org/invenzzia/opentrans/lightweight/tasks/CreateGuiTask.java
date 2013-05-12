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
import com.google.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.exception.TaskException;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;
import org.invenzzia.opentrans.lightweight.ui.MainMenuController;
import org.invenzzia.opentrans.lightweight.ui.MainWindow;
import org.invenzzia.opentrans.lightweight.ui.MainWindowController;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.WorldTab;
import org.invenzzia.opentrans.lightweight.ui.toolbars.AbstractToolbar;
import org.invenzzia.opentrans.lightweight.ui.toolbars.HistoryToolbar;
import org.invenzzia.opentrans.lightweight.ui.toolbars.HistoryToolbarController;
import org.invenzzia.opentrans.lightweight.ui.toolbars.ProjectToolbar;
import org.invenzzia.opentrans.lightweight.ui.toolbars.ToolbarManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.HistoryController;
import org.invenzzia.opentrans.lightweight.ui.workspace.HistoryPanel;
import org.invenzzia.opentrans.lightweight.ui.workspace.WorkspaceController;
import org.invenzzia.opentrans.lightweight.ui.workspace.WorkspacePanel;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Creates a GUI for the application.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CreateGuiTask implements ITask {
	@Inject
	private Provider<MainWindow> mainWindowProvider;
	@Inject
	private Provider<WorkspacePanel> workspacePanelProvider;
	@Inject
	private Provider<ProjectToolbar> projectToolbarProvider;
	@Inject
	private ToolbarManager toolbarManager;
	@Inject
	private MainWindowController mainWindowController;
	@Inject
	private WorkspaceController workspaceController;
	@Inject
	private HistoryController historyController;
	@Inject
	private DesktopManager desktopManager;
	@Inject
	private IconService iconService;
	@Inject
	private EventBus eventBus;
	@Inject
	private History<ICommand> history;
	@Inject
	private HistoryToolbarController historyToolbarController;
	@Inject
	private MainMenuController mainMenuController;

	@Override
	public void startup() throws TaskException {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					doStartup();
				}
			});
		} catch(InterruptedException ex) {
			throw new TaskException("The GUI construction has been interrupted.");
		} catch(InvocationTargetException exception) {
			throw new TaskException("An exception occurred while starting the GUI.", exception.getCause());
		}
	}

	@Override
	public void shutdown() {
		this.mainWindowController.setMainWindow(null);
		this.mainMenuController.setView(null);
		this.eventBus.unregister(this.mainMenuController);
	}
	
	public final void doStartup() {
		this.initServices();
		
		// Create the toolbars - must go first, because the menu controller must see them!
		this.createToolbars();
		
		// Create the main GUI object
		MainWindow window = this.mainWindowProvider.get();
		this.mainWindowController.setMainWindow(window);
		this.mainMenuController.setView(window);
		this.eventBus.register(this.mainMenuController);
		
		WorkspacePanel workspacePanel = this.workspacePanelProvider.get();
		workspacePanel.getDesktopTab().setCloseButtonIcon(this.iconService.getIcon("ui-close-small"));
		window.getWorkspacePanel().add(workspacePanel);
		this.workspaceController.setWorkspace(workspacePanel);
		this.desktopManager.setManagedPane(workspacePanel.getDesktopTab());
		
		// Create the history tab
		HistoryPanel historyPanel = new HistoryPanel();
		workspacePanel.setHistoryPanel(historyPanel);
		
		this.historyController.setView(historyPanel);
		
		// Create toolbars
		this.toolbarManager.setToolbarPanel(window.getToolbarPanel());
		this.toolbarManager.update();
		this.desktopManager.setFocus(WorldTab.class);
		window.setVisible(true);
	}
	
	public final void initServices() {
		this.history.setMaximumCapacity(Preferences.userRoot().getInt("opentrans.historySize", 100));
	}
	
	/**
	 * Toolbar creation code goes here.
	 */
	public final void createToolbars() {
		ProjectToolbar projectToolbar = new ProjectToolbar();
		projectToolbar.importIcons(this.iconService);
		this.setToolbarActivity(projectToolbar);
		this.toolbarManager.addToolbar(projectToolbar);
		
		HistoryToolbar historyToolbar = new HistoryToolbar();
		historyToolbar.importIcons(this.iconService);
		this.historyToolbarController.setView(historyToolbar);
		this.eventBus.register(this.historyToolbarController);
		this.setToolbarActivity(historyToolbar);
		this.toolbarManager.addToolbar(historyToolbar);
		
	}
	
	/**
	 * Sets the activity status of toolbar, from the user preferences.
	 * 
	 * @param toolbar 
	 */
	private void setToolbarActivity(AbstractToolbar toolbar) {
		toolbar.setActive(Preferences.userRoot().getBoolean("opentrans.toolbars."+toolbar.getClass().getSimpleName(), true));
	}
}
