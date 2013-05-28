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
import java.awt.geom.Line2D;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;

/**
 * Draws a straight track.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StraightTrackPainter implements ITrackPainter {
	/**
	 * ID of the painted track: for the mouse hovering.
	 */
	private final long id;
	private final double coordinates[];
	/**
	 * Committed track metadata are represented in relative values to the segment boundaries
	 * in order to survive world size change. The delta is passed separately then.
	 */
	private double dx, dy;
	private Line2D.Double line;
		
	public StraightTrackPainter(long id, double metadata[], double dx, double dy) {
		this.coordinates = metadata;
		this.id = id;
		this.dx = dx;
		this.dy = dy;
	}
	
	public StraightTrackPainter(long id, double metadata[]) {
		this.coordinates = metadata;
		this.id = id;
		this.dx = 0.0;
		this.dy = 0.0;
	}
	
	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void draw(CameraModelSnapshot camera, Graphics2D graphics, boolean editable) {
		if(null != this.line) {
			graphics.draw(this.line);
		}
	}

	@Override
	public void refreshData(CameraModelSnapshot camera) {
		this.line = new Line2D.Double(
			camera.world2pixX(this.coordinates[0] + this.dx),
			camera.world2pixY(this.coordinates[1] + this.dy),
			camera.world2pixX(this.coordinates[2] + this.dx),
			camera.world2pixY(this.coordinates[3] + this.dy)
		);
	}
	
	@Override
	public boolean hits(Graphics2D graphics, Rectangle rect) {
		if(null != rect && null != this.line) {
			return graphics.hit(rect, this.line, true) || graphics.hit(rect, this.line, true);
		}
		return false;
	}

	@Override
	public double computePosition(MouseSnapshot snapshot) {
		double cursorDist = LineOps.distance(this.line.getX1(), this.line.getY1(), snapshot.x(), snapshot.y());
		double lineLength = LineOps.distance(this.line.getX1(), this.line.getY1(), this.line.getX2(), this.line.getY2());
		
		double result = cursorDist / lineLength;
		if(result > 1.0) {
			result = 1.0;
		} else if(result < 0.0) {
			result = 0.0;
		}
		return result;
	}
}
