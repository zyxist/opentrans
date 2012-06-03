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
package org.invenzzia.opentrans.client;

import javax.swing.SwingUtilities;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.ui.card.CardView;
import org.invenzzia.helium.gui.ui.welcome.WelcomeController;
import org.invenzzia.helium.gui.ui.welcome.WelcomeView;
import org.invenzzia.helium.tasks.annotations.Task;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;

/**
 *
 * @author zyxist
 */
public class StartupTasks {
	private Application app;

	public StartupTasks(Application app) {
		this.app = app;
	} // end StartupTasks();
	
	@Task(order = 1, weight = 5, description = "Initializing dependency graph.")
	public void initDependencies() {
		MutablePicoContainer container = this.app.getContainer();
		container.as(Characteristics.NO_CACHE).addComponent(MyWelcomeView.class);
	}
	
	/**
	 * OpenTrans-specific GUI initialization code.
	 * 
	 * @throws PresenterConfigurationException 
	 */
	@Task(order = 900, weight = 5, description = "Initializing GUI")
	public void initializeActions() {
		this.app.getActionManager().registerActions(new MenuActions(this.app));
	}
	
	@Task(order = 2000, weight = 1, description = "Opening cards")
	public void createWelcomeScreen() {
		final CardView cardView = this.app.getContainer().getComponent(CardView.class);
		WelcomeController controller = this.app.getContainer().getComponent(WelcomeController.class);
		controller.loadDefinition("Welcome");
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				WelcomeView welcomeView = StartupTasks.this.app.getContainer().getComponent(MyWelcomeView.class);
				cardView.createCard(welcomeView);
			}
		});
	} // end createWelcomeScreen();
} // end StartupTasks();
