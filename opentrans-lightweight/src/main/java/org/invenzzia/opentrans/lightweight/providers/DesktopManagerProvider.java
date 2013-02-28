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

package org.invenzzia.opentrans.lightweight.providers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.Set;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 * Initializes the desktop manager and registers all the tab factories
 * registered in the dependency injection container. This effectively allows
 * us to add new tab types by just binding them to the dependency injection
 * container.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DesktopManagerProvider implements Provider<DesktopManager> {
	@Inject
	private Injector injector;
	
	@Inject
	private Set<IDesktopPaneFactory> factories;
	
	@Override
	public DesktopManager get() {
		DesktopManager deskMan = new DesktopManager();		
		for(IDesktopPaneFactory factory: this.factories) {
			deskMan.registerFactory(factory.getContentType(), factory);
		}
		
		return deskMan;
	}

}
