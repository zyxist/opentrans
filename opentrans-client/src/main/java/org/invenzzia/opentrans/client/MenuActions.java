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
import javax.swing.JOptionPane;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.ContextManagerService;
import org.invenzzia.helium.gui.annotation.Action;
import org.invenzzia.helium.gui.ui.appframe.AppframeView;
import org.invenzzia.opentrans.client.context.ProjectContext;
import org.invenzzia.opentrans.visitons.VisitonsProject;

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
		ProjectContext projectCtx = this.application.getCurrentContainer().getComponent(ProjectContext.class);
		projectCtx.setVisitonsProject(new VisitonsProject());
		
		ContextManagerService cm = this.application.getCurrentContainer().getComponent(ContextManagerService.class);
		cm.pushContext(projectCtx);
	}
	
	@Action(id="closeProject")
	public void actionCloseProject() {
		ContextManagerService cm = this.application.getCurrentContainer().getComponent(ContextManagerService.class);
		AppframeView appView = this.application.getCurrentContainer().getComponent(AppframeView.class);
		int n = JOptionPane.showConfirmDialog(appView, "Do you really want to close the project?", "Closing project", JOptionPane.YES_NO_OPTION);
		if(JOptionPane.YES_OPTION == n) {
			while(cm.popContext().getClass() != ProjectContext.class) {
			}
		}
	}
} // end MenuActions;
