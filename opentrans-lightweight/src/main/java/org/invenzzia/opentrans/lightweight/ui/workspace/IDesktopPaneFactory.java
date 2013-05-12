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

package org.invenzzia.opentrans.lightweight.ui.workspace;

import javax.swing.JPanel;

/**
 * Specifies, how to create a particular tab type. It is guaranteed
 * that the desktop manager won't ask for the new desktop item before
 * destroying the previous one, so we can cache some information in
 * the factory.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IDesktopPaneFactory<T extends JPanel> {
	/**
	 * We must know the name of the desktop item without constructing the
	 * actual desktop item in order to draw a menu.
	 * 
	 * @return Name of the constructed desktop items.
	 */
	public String getDesktopItemName();
	/**
	 * @return Type of the constructed desktop item class.
	 */
	public Class<T> getContentType();
	/**
	 * Creates the desktop item and returns it.
	 * 
	 * @return New desktop item.
	 */
	public DesktopItem createDesktopItem();
	/**
	 * Destroys the desktop item.
	 * 
	 * @param desktopItem 
	 */
	public void destroyDesktopItem(DesktopItem desktopItem);
}
