/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.render.painters;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;

/**
 * Painters tell the scene snapshots, how to paint certain types of tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ITrackPainter {
	/**
	 * @return ID of the painted track.
	 */
	public long getId();
	/**
	 * Checks whether the mouse hits this object.
	 * 
	 * @param rect Mouse mouse rectangle.
	 * @return True, if this object is hit.
	 */
	public boolean hits(Graphics2D graphics, Rectangle rect);
	
	/**
	 * To place something on tracks, we must know, which part of the line was
	 * clicked.
	 * 
	 * @param snapshot Information about the mouse position.
	 * @return Position above the line.
	 */
	public double computePosition(MouseSnapshot snapshot);
	/**
	 * Draws the given track on the screen.
	 * 
	 * @param camera The camera information snapshot.
	 * @param graphics The screen to draw on.
	 */
	public void draw(CameraModelSnapshot camera, Graphics2D graphics, boolean editable);
	/**
	 * Refreshes the painter data due to the changes in the camera model.
	 * 
	 * @param camera 
	 */
	public void refreshData(CameraModelSnapshot camera);
}
