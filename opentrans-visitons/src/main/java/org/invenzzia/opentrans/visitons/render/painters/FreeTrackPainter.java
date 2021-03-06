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
import org.invenzzia.opentrans.visitons.geometry.ArcOps;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;

/**
 * Draws a free track (two curved tracks).
 * 
 * @author Tomasz Jędrzejewski
 */
public class FreeTrackPainter implements ITrackPainter {
	/**
	 * ID of the painted track: for the mouse hovering.
	 */
	private final long id;
	private double coordinates[];
	/**
	 * Committed track metadata are represented in relative values to the segment boundaries
	 * in order to survive world size change. The delta is passed separately then.
	 */
	private double dx, dy;
	private Arc2D.Double firstArc;
	private Arc2D.Double secondArc;
	/**
	 * If we make a mouse hit, we must know, which arc we have hit to, to calculate the
	 * proper position. This is a bit ugly, because now our calculatePosition() will work
	 * only if we have checked the hit earlier.
	 */
	private boolean hitSecond;
	
	public FreeTrackPainter(long id, double metadata[], double dx, double dy) {
		this.id = id;
		this.coordinates = metadata;
		this.dx = dx;
		this.dy = dy;
	}
	
	public FreeTrackPainter(long id, double metadata[]) {
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
		if(null != this.firstArc) {
			graphics.draw(this.firstArc);
			graphics.draw(this.secondArc);
		}
	}

	@Override
	public void refreshData(CameraModelSnapshot camera) {
		double wh = (double) camera.world2pix(this.coordinates[2]);
		this.firstArc = new Arc2D.Double(
			(double) camera.world2pixX(this.coordinates[0] + this.dx),
			(double) camera.world2pixY(this.coordinates[1] + this.dy),
			wh, wh,
			Math.toDegrees(this.coordinates[4]),
			Math.toDegrees(this.coordinates[5]),
			Arc2D.OPEN
		);
		wh = (double) camera.world2pix(this.coordinates[10]);
		this.secondArc = new Arc2D.Double(
			(double) camera.world2pixX(this.coordinates[8] + this.dx),
			(double) camera.world2pixY(this.coordinates[9] + this.dy),
			wh, wh,
			Math.toDegrees(this.coordinates[12]),
			Math.toDegrees(this.coordinates[13]),
			Arc2D.OPEN
		);
	}
	
	@Override
	public boolean hits(Graphics2D graphics, Rectangle rect) {
		if(null != rect && null != this.firstArc) {
			return graphics.hit(rect, this.firstArc, true) || (this.hitSecond = graphics.hit(rect, this.secondArc, true));
		}
		return false;
	}

	@Override
	public double computePosition(MouseSnapshot snapshot, CameraModelSnapshot camera) {
		double px = camera.pix2worldX(snapshot.x());
		double py = camera.pix2worldY(snapshot.y());
		if(this.hitSecond) {
			double cx = this.coordinates[14] + this.dx;
			double cy = this.coordinates[15] + this.dy;
			double t = ArcOps.coord2Param(cx, cy,
				this.coordinates[18] + this.dx, this.coordinates[19] + this.dy,
				this.coordinates[11], px, py
			);
			t = (t / this.coordinates[13]);
			if(t < 0.0) {
				return 0.5;
			} else if(t > 1.0) {
				return 1.0;
			}
			return (t / 2.0) + 0.5;
		} else {
			double cx = this.coordinates[6] + this.dx;
			double cy = this.coordinates[7] + this.dy;
			double t = ArcOps.coord2Param(cx, cy,
				this.coordinates[16] + this.dx, this.coordinates[17] + this.dy,
				this.coordinates[3], px, py
			);
			t = (t / this.coordinates[5]);
			if(t < 0.0) {
				return 0.0;
			} else if(t > 1.0) {
				return 0.5;
			}
			return t / 2.0;
		}
	}
}
