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

package org.invenzzia.opentrans.lightweight.ui.tabs;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopItem;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class ProjectTabFactory implements IDesktopPaneFactory<ProjectTab> {
	@Inject
	private Provider<ProjectTabFormHandler> formHandlerProvider;
	@Inject
	private Provider<ProjectTabController> controllerProvider;
	@Inject
	private EventBus eventBus;
	/**
	 * We must remember the controller in order to unregister it from event bus,
	 * when the panel is discarded.
	 */
	private ProjectTabController controllerMemento;
	
	@Override
	public String getDesktopItemName() {
		return "Project";
	}

	@Override
	public Class getContentType() {
		return ProjectTab.class;
	}

	@Override
	public DesktopItem createDesktopItem() {
		ProjectTab projectTab = new ProjectTab();
		this.controllerMemento = this.controllerProvider.get();
		this.controllerMemento.setView(projectTab);
		this.eventBus.register(this.controllerMemento);
		
		DesktopItem di = new DesktopItem(this.getDesktopItemName(), projectTab);
		return di;
	}

	@Override
	public void destroyDesktopItem(DesktopItem desktopItem) {
		this.eventBus.unregister(this.controllerMemento);
	}
}
