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
import java.awt.geom.Arc2D;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * Draws a curved track.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CurvedTrackPainter implements ITrackPainter {
	/**
	 * ID of the painted track: for the mouse hovering.
	 */
	private final long id;
	/**
	 * Data coordinates.
	 */
	private double coordinates[];
	/**
	 * Committed track metadata are represented in relative values to the segment boundaries
	 * in order to survive world size change. The delta is passed separately then.
	 */
	private final double dx, dy;
	/**
	 * Precomputed arc object.
	 */
	private Arc2D.Double arc;
	
	public CurvedTrackPainter(long id, double metadata[], double dx, double dy) {
		this.id = id;
		this.coordinates = metadata;
		this.dx = dx;
		this.dy = dy;
	}
	
	public CurvedTrackPainter(long id, double metadata[]) {
		this.id = id;
		this.coordinates = metadata;
		this.dx = 0.0;
		this.dy = 0.0;
	}
	
	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void draw(CameraModelSnapshot camera, Graphics2D graphics, boolean editable) {
		if(null != this.arc) {
			graphics.draw(this.arc);
		}
	}

	@Override
	public void refreshData(CameraModelSnapshot camera) {
		this.arc = new Arc2D.Double(
			(double) camera.world2pixX(this.coordinates[0] + this.dx),
			(double) camera.world2pixY(this.coordinates[1] + this.dy),
			(double) camera.world2pix(this.coordinates[2]),
			(double) camera.world2pix(this.coordinates[3]),
			Math.toDegrees(this.coordinates[4]),
			Math.toDegrees(this.coordinates[5]),
			Arc2D.OPEN
		);
	}

	@Override
	public boolean hits(Graphics2D graphics, Rectangle rect) {
		if(null != rect && null != this.arc) {
			return graphics.hit(rect, this.arc, true);
		}
		return false;
	}
}
