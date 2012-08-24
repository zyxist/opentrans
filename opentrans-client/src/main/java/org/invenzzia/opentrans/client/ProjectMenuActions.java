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
import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.annotation.Action;
import org.invenzzia.helium.gui.exception.ViewConfigurationException;
import org.invenzzia.helium.gui.mvc.ViewService;
import org.invenzzia.helium.gui.ui.appframe.AppframeView;
import org.invenzzia.helium.gui.ui.dialog.DefaultDialogController;
import org.invenzzia.opentrans.client.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.client.ui.worldresize.WorldResizeController;
import org.invenzzia.opentrans.client.ui.worldresize.WorldResizeView;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.picocontainer.MutablePicoContainer;

/**
 * Project-specific menu actions.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectMenuActions {
	private Application application;
	
	public ProjectMenuActions(Application app) {
		this.application = Preconditions.checkNotNull(app);
	}
	
	@Action(id="showWorldSizeDialog")
	public void actionWorldResize() throws ViewConfigurationException {
		AppframeView appView = this.application.get(ViewService.class).get(AppframeView.class);

		WorldResizeView view = new WorldResizeView();
		WorldResizeController controller = this.application.get(WorldResizeController.class);
		view.setController(controller);
		
		appView.displayDialog(view, this.application.get(DefaultDialogController.class));
		
		// Once the dialog is closed, we must refresh some data.
		VisitonsProject project = this.application.get(VisitonsProject.class);
		this.application.get(EventBus.class).post(new WorldSizeChangedEvent(project.getWorld()));
	}
}
