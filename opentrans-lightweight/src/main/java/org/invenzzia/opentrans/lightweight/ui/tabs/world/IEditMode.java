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

/**
 * Common interface for writing edit modes.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IEditMode {
	
	public boolean captureMotionEvents();
	
	public boolean captureDragEvents();
	
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	/**
	 * The method is called, when the mode is becoming enabled and starts capturing
	 * the input events.
	 */
	public void modeEnabled();
	/**
	 * The method is called, when the mode is becoming disabled and stops capturing
	 * the input events.
	 */
	public void modeDisabled();
}
