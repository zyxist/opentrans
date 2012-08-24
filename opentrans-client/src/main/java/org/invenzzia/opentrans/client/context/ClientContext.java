/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.exceptions.ParseException;
import org.invenzzia.helium.gui.IconManagerService;
import org.invenzzia.helium.gui.actions.ActionManagerService;
import org.invenzzia.helium.gui.annotation.Tasks;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.SplashEvent;
import org.invenzzia.helium.gui.mvc.ModelService;
import org.invenzzia.helium.gui.ui.appframe.AppframeView;
import org.invenzzia.helium.gui.ui.dock.*;
import org.invenzzia.helium.gui.ui.dock.components.DockStatusMenu;
import org.invenzzia.helium.gui.ui.menu.IMenuElementStorage;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.element.Menu;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.helium.gui.ui.menu.element.Separator;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeModel;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;
import org.invenzzia.helium.gui.ui.workspace.WorkspaceDockModel;
import org.invenzzia.opentrans.client.MenuActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context for the OpenTrans client. Represents a case, where no project
 * is open.
 * 
 * @author Tomasz Jędrzejewski
 */
@Tasks(weight = 10)
public class ClientContext extends AbstractContext {
	private final Logger logger = LoggerFactory.getLogger(ClientContext.class);
	
	private DockStatusMenu dockStatusMenu;
	/**
	 * For registrations within the docking system.
	 */
	private KnownPositions knownPositions;
	/**
	 * Access global models.
	 */
	private ModelService modelService;
	
	public ClientContext(Application application) {
		super(application);
		this.registerManagedClasses(new Object[][] {
			{ ProjectContext.class },
		});
	}
	
	@Inject
	public void setKnownPositions(KnownPositions kp) {
		this.knownPositions = kp;
	}
	
	@Inject
	public void setModelService(ModelService ms) {
		this.modelService = ms;
	}

	@Override
	protected boolean startup() {
		this.logger.info("OpenTrans client is being opened.");
		this.container.start();
		
		this.eventBus.post(new SplashEvent(8, "Loading icons..."));
		this.logger.info("Loading icons.");
		this.loadIcons();
		
		this.eventBus.post(new SplashEvent(2, "Initializing OpenTrans environment..."));
		this.logger.info("Initializing client menu.");
		this.initClientMenu(this.modelService.get(MenuModel.class));
		this.logger.info("Initializing actions.");
		this.initActions();
		this.logger.info("Initializing welcome screen.");
		this.initWelcomeScreen();
		
		this.logger.info("OpenTrans client opened.");
		
		return true;
	}

	@Override
	protected boolean shutdown() {
		this.eventBus.unregister(this.dockStatusMenu);
		this.container.stop();
		return true;
	}
	
	/**
	 * Initializes the basic client menu structure. Note that we do not have to clean it manually
	 * at the end of the context, because the menu model automatically does that for us.
	 * 
	 * @param model 
	 */
	private void initClientMenu(MenuModel model) {
		model.startBatchUpdate();
		try {
			IMenuElementStorage fileElement = model.getElement("file", IMenuElementStorage.class);
			fileElement.prependElement(new Position("newProject", "New project", "newProject"));
			fileElement.addElementAfter(new Separator("newProjectSeparator"), "newProject");
			fileElement.addElementAfter(new Position("openProject", "Open project"), "newProjectSeparator");
			fileElement.addElementAfter(new Position("saveProject", "Save project"), "openProject");
			fileElement.addElementAfter(new Position("saveProjectAs", "Save project as..."), "saveProject");
			fileElement.addElementAfter(new Separator("quitSeparator"), "saveProjectAs");
		
			Menu editMenu = new Menu("edit", "Edit");
			editMenu.appendElement(new Position("undo", "Undo"));
			editMenu.appendElement(new Position("redo", "Redo"));
			
			Menu viewMenu = new Menu("view", "View");
			
			model.addElementAfter(editMenu, "file");
			model.addElementAfter(viewMenu, "edit");
			
			model.addElementBefore(this.dockStatusMenu = new DockStatusMenu("dockStatus", "Window", this.container.getComponent(WorkspaceDockModel.class)), "help");
			this.eventBus.register(this.dockStatusMenu);
		} finally {
			model.stopBatchUpdate();
		}
	}
	
	public void initActions() {
		this.application.get(ActionManagerService.class).registerActions(new MenuActions(this.application));
	}
	
	public void initWelcomeScreen() {
		final WelcomeController controller = this.container.getComponent(WelcomeController.class);
		final WelcomeModel model = this.container.getComponent(WelcomeModel.class);
		try {
			model.loadPage(ClientContext.class.getClassLoader().getResourceAsStream("welcome.xml"), "welcome.xml");

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					WelcomeView welcomeView = new WelcomeView();
					welcomeView.setModel(model);
					welcomeView.setController(controller);

					Dockable welcomeDockable = new Dockable(welcomeView, "Welcome");
					DockModel dockModel = modelService.get(WorkspaceDockModel.class);
					dockModel.resolvePath(knownPositions.selectPath(welcomeDockable, "editor"), welcomeDockable);
				}
			});
		} catch(IOException | ParseException exception) {
			this.container.getComponent(AppframeView.class).displayError("Error loading welcome.xml", exception);
		}
	}
	
	/**
	 * Loads the icons.
	 */
	public void loadIcons() {
		try {
			final IconManagerService iconManager = this.container.getComponent(IconManagerService.class);
			final ClassLoader currentLoader = this.getClass().getClassLoader();
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					iconManager.setUnknownIcon(currentLoader.getResource("icons/unknown.png"));
					
					iconManager.preloadIcon("ui-close", currentLoader.getResource("icons/ui-close.png"));
					iconManager.preloadIcon("ui-maximize", currentLoader.getResource("icons/ui-maximize.png"));
					iconManager.preloadIcon("ui-restore", currentLoader.getResource("icons/ui-restore.png"));
					iconManager.preloadIcon("ui-cursor", currentLoader.getResource("icons/ui-cursor.png"));
					
					iconManager.preloadIcon("visitons-netedit", currentLoader.getResource("icons/visitons-netedit.png"));
					iconManager.addIcon("pencil", currentLoader.getResource("icons/pencil.png"));
					iconManager.addIcon("vector", currentLoader.getResource("icons/vector.png"));
				}
			});
		} catch(InterruptedException | InvocationTargetException ex) {
			this.logger.warn("Cannot load icons.", ex);
		}
	}
}
