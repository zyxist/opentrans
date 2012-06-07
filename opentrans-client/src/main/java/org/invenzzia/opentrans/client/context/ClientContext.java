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

import com.google.common.eventbus.EventBus;
import javax.swing.SwingUtilities;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.annotation.Tasks;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.SplashEvent;
import org.invenzzia.helium.gui.ui.card.CardView;
import org.invenzzia.helium.gui.ui.menu.IMenuElementStorage;
import org.invenzzia.helium.gui.ui.menu.MenuController;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.element.Menu;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.helium.gui.ui.menu.element.Separator;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;
import org.invenzzia.opentrans.client.MenuActions;
import org.invenzzia.opentrans.client.MyWelcomeView;
import org.picocontainer.Characteristics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context for the OpenTrans client. Represents a case, where no project
 * is open.
 * 
 * @author Tomasz Jędrzejewski
 */
@Tasks(weight = 2)
public class ClientContext extends AbstractContext {
	private final Logger logger = LoggerFactory.getLogger(ClientContext.class);
	
	public ClientContext(Application application) {
		super(application);
		this.container.as(Characteristics.NO_CACHE).addComponent(MyWelcomeView.class);
	}

	@Override
	protected boolean startup() {
		this.logger.info("OpenTrans client is being opened.");
		this.container.start();
		
		EventBus eventBus = this.application.getEventBus();
		eventBus.post(new SplashEvent(2, "Initializing OpenTrans environment..."));
		this.initClientMenu(this.container.getComponent(MenuController.class).getModel());
		this.initActions();
		this.initWelcomeScreen();
		
		return true;
	}

	@Override
	protected boolean shutdown() {
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
		} finally {
			model.stopBatchUpdate();
		}
	}
	
	public void initActions() {
		this.application.getActionManager().registerActions(new MenuActions(this.application));
	}
	
	public void initWelcomeScreen() {
		final CardView cardView = this.container.getComponent(CardView.class);
		WelcomeController controller = this.container.getComponent(WelcomeController.class);
		controller.loadDefinition("Welcome");
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				WelcomeView welcomeView = ClientContext.this.container.getComponent(MyWelcomeView.class);
				cardView.createCard(welcomeView);
			}
		});
	}
}
