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
 * Specifies, how to create a particular tab type.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public interface IDesktopPaneFactory<T extends JPanel> {
	public Class<T> getContentType();
	public DesktopItem createDesktopItem();
	public void destroyDesktopItem(DesktopItem desktopItem);
}
