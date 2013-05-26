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

package org.invenzzia.opentrans.lightweight.ui.tabs.world.popups;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.annotations.PopupAction;
import org.invenzzia.opentrans.lightweight.events.CameraUpdatedEvent;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * This action allows centering the screen from the popup menu.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Center here")
@Singleton
public class CenterAction implements IPopupAction {
	@Inject
	private CameraModel cameraModel;
	@Inject
	private EventBus eventBus;
	
	@Override
	public void execute(IEditModeAPI api, double x, double y) {
		if(!api.getWorldRecord().isWithinWorld(x, y)) {
			api.setStatusMessage("Cannot center the camera: outside the world.");
		} else {
			this.cameraModel.centerAt(x, y);
			eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(cameraModel)));
		}
	}

}
