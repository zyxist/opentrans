/*
 * Copyright (C) 2013 zyxist
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.ui.tabs.timetable;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopItem;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;

/**
 *
 * @author zyxist
 */
public class TimetableTabFactory implements IDesktopPaneFactory<TimetableTab> {
	@Inject
	private Provider<TimetableTabController> controllerProvider;
	@Inject
	private EventBus eventBus;

	@Override
	public String getDesktopItemName() {
		return "Timetable";
	}

	@Override
	public Class<TimetableTab> getContentType() {
		return TimetableTab.class;
	}

	@Override
	public DesktopItem createDesktopItem() {
		TimetableTab view = new TimetableTab();
		TimetableTabController controller = this.controllerProvider.get();
		controller.setView(view);
		
		this.eventBus.register(controller);
		return new DesktopItem(this.getDesktopItemName(), view, controller);
	}

	@Override
	public void destroyDesktopItem(DesktopItem desktopItem) {
		this.eventBus.unregister(desktopItem.getMetadata(TimetableTabController.class));
	}
}
