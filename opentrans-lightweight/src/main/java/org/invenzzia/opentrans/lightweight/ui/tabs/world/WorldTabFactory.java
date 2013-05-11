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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

import com.google.inject.Inject;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;
import org.invenzzia.opentrans.lightweight.ui.MainWindowController;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopItem;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldTabFactory implements IDesktopPaneFactory<WorldTab> {
	@Inject
	private WorldTabController controller;
	@Inject
	private MainWindowController mainWindowController;
	@Inject
	private IconService iconService;

	@Override
	public Class getContentType() {
		return WorldTab.class;
	}

	@Override
	public DesktopItem createDesktopItem() {
		WorldTab worldTab = new WorldTab();
		worldTab.importIcons(this.iconService);
		this.controller.setWorldTab(worldTab);
		return new DesktopItem("World", worldTab, this.controller);
	}

	@Override
	public void destroyDesktopItem(DesktopItem desktopItem) {
		this.controller.setWorldTab(null);
		this.mainWindowController.getMainWindow().clearLocationInfo();
	}
}
