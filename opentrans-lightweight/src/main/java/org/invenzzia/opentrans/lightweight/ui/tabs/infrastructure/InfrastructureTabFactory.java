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

package org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopItem;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class InfrastructureTabFactory implements IDesktopPaneFactory<InfrastructureTab> {
	@Inject
	private Provider<InfrastructureTabController> controllerProvider;
	@Inject
	private EventBus eventBus;
	
	private InfrastructureTabController lastController;
	
	@Override
	public String getDesktopItemName() {
		return "Infrastructure";
	}

	@Override
	public Class<InfrastructureTab> getContentType() {
		return InfrastructureTab.class;
	}

	@Override
	public DesktopItem createDesktopItem() {
		InfrastructureTab tab = new InfrastructureTab();
		InfrastructureTabController ctrl =  this.controllerProvider.get();
		this.eventBus.register(ctrl);
		ctrl.setView(tab);
		this.lastController = ctrl;
		
		return new DesktopItem(this.getDesktopItemName(), tab);
	}

	@Override
	public void destroyDesktopItem(DesktopItem desktopItem) {
		this.eventBus.unregister(this.lastController);
	}
}
