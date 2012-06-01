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

import com.google.common.base.Preconditions;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.annotation.Action;

/**
 * Menu actions for OpenTrans.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MenuActions {
	private Application application;
	
	public MenuActions(Application app) {
		this.application = Preconditions.checkNotNull(app);
	}
	
	@Action(id="newProject")
	public void actionNewProject() {
		/*
		LifecycleManager lcm = this.application.getLifecycleManager();
		DialogUtils.runDialog(lcm, lcm.getPresenter(ProjectPresenter.class));
		*/
	} // end actionNewProject();
} // end MenuActions;
