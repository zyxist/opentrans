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
 * Various types of events that can be captured by the world tab controller while
 * moving the cursor around. This interface is used for both creating the complete
 * edit modes, and the state machines within them.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IEditState {
	public boolean captureMotionEvents();
	
	public boolean captureDragEvents();
	
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
}
