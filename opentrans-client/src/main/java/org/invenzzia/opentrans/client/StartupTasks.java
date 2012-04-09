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

import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.exception.PresenterConfigurationException;
import org.invenzzia.helium.gui.presenter.card.CardPresenter;
import org.invenzzia.helium.gui.presenter.welcome.WelcomePresenter;
import org.invenzzia.helium.tasks.annotations.Task;

/**
 *
 * @author zyxist
 */
public class StartupTasks {
	private Application app;

	public StartupTasks(Application app) {
		this.app = app;
	} // end StartupTasks();
	
	@Task(order = 2000, weight = 1, description = "Opening cards")
	public void createWelcomeScreen() throws PresenterConfigurationException {
		CardPresenter cardPresenter = this.app.getLifecycleManager().getPresenter(CardPresenter.class);
		
		for(int i = 0; i < 20; i++) {
			cardPresenter.createCard(this.app.getLifecycleManager().getPresenter(WelcomePresenter.class, "id"+Integer.toString(i)));
		}
	} // end createWelcomeScreen();
} // end StartupTasks();
