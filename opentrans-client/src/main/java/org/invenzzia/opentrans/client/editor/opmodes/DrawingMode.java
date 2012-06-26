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
package org.invenzzia.opentrans.client.editor.opmodes;

import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.opentrans.client.ui.netview.IOperationMode;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DrawingMode implements IOperationMode {
	@Override
	public void modeActivated() {
	}

	@Override
	public void modeDeactivated() {
	}

	@Override
	public String getName() {
		return "Draw";
	}

	@Override
	public String getIcon() {
		return "pencil";
	}
	
	@Override
	public String getHelpText() {
		return "Click on the map to start drawing a track.";
	}
	
	@Override
	public MenuModel getContextMenuModel() {
		return null;
	}
}
