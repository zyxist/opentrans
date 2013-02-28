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

package org.invenzzia.opentrans.lightweight.ui.toolbars;

import java.awt.Dimension;
import javax.swing.JToolBar;

/**
 * Base class for all the toolbars in the program.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractToolbar extends JToolBar {
	/**
	 * Information for the toolbar manager whether this toolbar is visible.
	 */
	private boolean active;
	
	/**
	 * The method shall return the name of the toolbar.
	 * 
	 * @return Toolbar name.
	 */
	abstract public String getToolbarName();
	
	/**
	 * Initializes the toolbar properties.
	 * 
	 * @param numberOfButtons The number of buttons in the toolbar.
	 */
	protected void initProperties(int numberOfButtons) {
		this.setFloatable(false);
	//	this.setBorder(BorderFactory.createSoftBevelBorder(EtchedBorder.RAISED));
		this.setPreferredSize(new Dimension(numberOfButtons * 42, 42));
	}
	
	/**
	 * Sets the new value of the 'active' flag used for controlling the displaying
	 * of this toolbar.
	 * 
	 * @param active 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * Is this toolbar active?
	 * 
	 * @return True, if this toolbar is active and visible.
	 */
	public boolean isActive() {
		return this.active;
	}
}
