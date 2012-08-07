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
package org.invenzzia.opentrans.client.editor.opmodes.selection;

import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.gui.ContextManagerService;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.opentrans.client.ui.netview.IOperationMode;

/**
 * In the selection mode, we can select the parts of the infrastructure
 * and move them around. Such actions, as delete work and these are the
 * only available modifications of the network graph content.
 * 
 * Right-clicking allows selecting i.e. the bitmap for the segment.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMode implements IOperationMode {
	private MenuModel contextMenuModel;
	
	public SelectionMode(EventBus eventBus, ContextManagerService contextManager) {
		this.contextMenuModel = new MenuModel(eventBus, contextManager);
		this.contextMenuModel.appendElement(new Position("set-segment-bitmap", "Set segment bitmap", "setSegmentBitmap"));
	}

	@Override
	public void modeActivated() {
	}

	@Override
	public void modeDeactivated() {
	}

	@Override
	public String getName() {
		return "Select";
	}

	@Override
	public String getIcon() {
		return "ui-cursor";
	}
	
	@Override
	public String getHelpText() {
		return "Click on the network infrastructure elements to select them.";
	}
	
	@Override
	public MenuModel getContextMenuModel() {
		return this.contextMenuModel;
	}
	
	@Override
	public Class<?> getMenuActions() {
		return SelectionMenuActions.class;
	}
}
