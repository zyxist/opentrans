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
package org.invenzzia.opentrans.client.ui.netview;

import org.invenzzia.helium.gui.ui.menu.MenuModel;

/**
 * Description here.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public interface IOperationMode extends IOperation {
	public static final short CLICK_LEFT = 0;
	public static final short CLICK_RIGHT = 1;
	
	public void modeActivated();
	public void modeDeactivated();
	public String getHelpText();

	/**
	 * @return Menu model used to display a pop-up menu.
	 */
	public MenuModel getContextMenuModel();
	/**
	 * @return Class implementing the pop-up menu actions.
	 */
	public Class<?> getMenuActions();
	/**
	 * This method is called by the command translator when we click with a mouse
	 * on a network map. We receive information about the neighbourhood (position+segment
	 * in world units, nearby vertices and tracks).
	 * 
	 * @param element Where we have clicked?
	 * @param button Button used for clicking.
	 */
	public void mouseClicked(ClickedElement element, short button);
	/**
	 * This method is called by the command translator when we move a mouse
	 * on a network map. We receive information about the neighbourhood (position+segment
	 * in world units, nearby vertices and tracks).
	 * 
	 * @param element Where we have clicked?
	 * @param button Button used for clicking.
	 */	
	public void mouseMoved(ClickedElement element);
	/**
	 * This method is called by the command translator when we drag a mouse on
	 * a network map. We receive information about the neighbourhood (position+segment
	 * in world units, nearby vertices and tracks).
	 * 
	 * @param element Where we have clicked?
	 * @param button Button used for clicking.
	 */
	public void mouseDragged(ClickedElement element, short button);
}
