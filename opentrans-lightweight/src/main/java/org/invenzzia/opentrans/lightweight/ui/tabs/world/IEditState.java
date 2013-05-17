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
	/**
	 * If this method returns true, the controller starts forwarding mouse motion events
	 * to the edit mode.
	 * 
	 * @return 
	 */
	public boolean captureMotionEvents();
	/**
	 * If this method returns true, the controller starts forwarding dragging events
	 * to the edit mode.
	 * 
	 * @return 
	 */
	public boolean captureDragEvents();
	/**
	 * The method is called, when we are moving the mouse around the camera view.
	 * However, {@link #captureMotionEvents() ()} must return true to enable movement
	 * event forwarding.
	 * 
	 * @param worldX Current mouse position in world coordinates.
	 * @param worldY Current mouse position in world coordinates.
	 * @param altDown Is ALT button pressed?
	 * @param ctrlDown Is CTRL button pressed?
	 */
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	
	/**
	 * The method is called at the beginning of the dragging process. It immediately follows
	 * the first call to {@link #mouseDrags(double, double, double, double, boolean, boolean)}.
	 * 
	 * @param worldX Current mouse position in world coordinates.
	 * @param worldY Current mouse position in world coordinates.
	 * @param altDown Is ALT button pressed?
	 * @param ctrlDown Is CTRL button pressed?
	 */
	public void mouseStartsDragging(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	/**
	 * The method is called, when the controller detects that we are dragging something.
	 * However, {@link #captureDragEvents()} must return true to enable drag event forwarding.
	 * 
	 * @param worldX Current mouse position in world coordinates.
	 * @param worldY Current mouse position in world coordinates.
	 * @param deltaX Movement delta
	 * @param deltaY Movement delta
	 * @param altDown Is ALT button pressed?
	 * @param ctrlDown Is CTRL button pressed?
	 */
	public void mouseDrags(double worldX, double worldY, double deltaX, double deltaY, boolean altDown, boolean ctrlDown);
	/**
	 * This method is called, when we stop dragging. It is guaranteed that previously, 
	 * {@link #mouseDrags(double, double, double, double, boolean, boolean)} has been called
	 * at least once.
	 * 
	 * @param worldX Current mouse position in world coordinates.
	 * @param worldY Current mouse position in world coordinates.
	 * @param altDown Is ALT button pressed?
	 * @param ctrlDown Is CTRL button pressed?
	 */
	public void mouseStopsDragging(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	/**
	 * The method is called, when the user clicks with the left mouse button somewhere.
	 * 
	 * @param worldX Place of click in the world coordinates.
	 * @param worldY Place of click in the world coordinates.
	 * @param altDown Whether the ALT button is pressed?
	 * @param ctrlDown Whether the CTRL button is pressed?
	 */
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	/**
	 * The method is called, when the user clicks with the right mouse button somewhere.
	 * 
	 * @param worldX Place of click in the world coordinates.
	 * @param worldY Place of click in the world coordinates.
	 * @param altDown Whether the ALT button is pressed?
	 * @param ctrlDown Whether the CTRL button is pressed?
	 */
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown);
	/**
	 * The method is called, if the user hits 'DELETE' button on the keyboard. The method is prepared
	 * for the forwarding the position of the cursor at the moment of hitting, but currently this functionality
	 * won't be implemented.
	 * 
	 * @param worldX Currently always 0
	 * @param worldY Currently always 0
	 */
	public void deletePressed(double worldX, double worldY);
}
