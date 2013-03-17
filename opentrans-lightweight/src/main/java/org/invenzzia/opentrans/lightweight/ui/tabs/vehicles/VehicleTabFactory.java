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

package org.invenzzia.opentrans.lightweight.ui.tabs.vehicles;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopItem;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 * Constructs the vehicle tab, when the user requests it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTabFactory implements IDesktopPaneFactory<VehicleTab> {
	@Inject
	private Provider<VehicleTabController> controllerProvider;
	@Inject
	private EventBus eventBus;

	@Override
	public Class<VehicleTab> getContentType() {
		return VehicleTab.class;
	}

	@Override
	public DesktopItem createDesktopItem() {
		VehicleTab tab = new VehicleTab();
		VehicleTabController ctrl =  this.controllerProvider.get();
		ctrl.setView(tab);
		this.eventBus.register(ctrl);
		
		return new DesktopItem("Vehicles", tab, ctrl);
	}

	@Override
	public void destroyDesktopItem(DesktopItem desktopItem) {
		this.eventBus.unregister(desktopItem.getMetadata(VehicleTabController.class));
	}
}
