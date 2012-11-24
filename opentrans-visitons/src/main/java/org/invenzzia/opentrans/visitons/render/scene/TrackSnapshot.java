/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.render.scene;

import com.google.common.base.Preconditions;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.infrastructure.CurvedTrack;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;
import org.invenzzia.opentrans.visitons.infrastructure.StraightTrack;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * Keeps immutable information about the tracks to draw on the
 * screen.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackSnapshot {
	private final List<IDrawableTrack> tracks;
	
	public TrackSnapshot() {
		this.tracks = new LinkedList<>();
	}
	
	public void addDrawableTrack(IDrawableTrack dt) {
		this.tracks.add(Preconditions.checkNotNull(dt));
	}
	
	public List<IDrawableTrack> getDrawableTracks() {
		return this.tracks;
	}
	
	public static interface IDrawableTrack {
		public void draw(CameraModelSnapshot camera, Graphics2D graphics);
	}
	
	public static class DrawableStraightTrack implements IDrawableTrack {
		private final double coordinates[];
		
		public DrawableStraightTrack(StraightTrack t) {
			this.coordinates = new double[4];
			IVertex v1 = t.getVertex(0);
			this.coordinates[0] = v1.x();
			this.coordinates[1] = v1.y();
			v1 = t.getVertex(1);
			this.coordinates[2] = v1.x();
			this.coordinates[3] = v1.y();
		}
		
		@Override
		public void draw(CameraModelSnapshot camera, Graphics2D graphics) {
			graphics.drawLine(
				camera.world2pixX(this.coordinates[0]),
				camera.world2pixY(this.coordinates[1]),
				camera.world2pixX(this.coordinates[2]),
				camera.world2pixY(this.coordinates[3])
			);
		}
	}
	
	public static class DrawableCurvedTrack implements IDrawableTrack {
		private final double coordinates[];
		private final byte convex;
		private final double dbg[];
		
		public DrawableCurvedTrack(CurvedTrack t) {
			this.convex = t.getConvex();
			this.coordinates = new double[6];
			this.dbg = new double[8];
			IVertex v1 = t.getVertex(0);
			IVertex v2 = t.getVertex(1);
			
			double angle1 = -Math.atan2(v1.y() - t.centY(), v1.x() - t.centX());
			if(angle1 < 0.0) {
				angle1 += 2* Math.PI;
			}
			double angle2 = -Math.atan2(v2.y() - t.centY(), v2.x() - t.centX());
			if(angle2 < 0.0) {
				angle2 += 2* Math.PI;
			}
			double dist = Geometry.angleDist(angle1, angle2, this.convex);
			double radius = Math.sqrt(Math.pow(v1.x() - t.centX(), 2) + Math.pow(v1.y() - t.centY(), 2));

			this.coordinates[0] = t.centX() - radius;
			this.coordinates[1] = t.centY() - radius;
			this.coordinates[2] = 2 * radius;
			this.coordinates[3] = 2 * radius;
			this.coordinates[4] = Math.toDegrees(angle1);
			this.coordinates[5] = Math.toDegrees(dist);
			
			this.dbg[0] = v1.x();
			this.dbg[1] = v1.y();
			this.dbg[2] = v2.x();
			this.dbg[3] = v2.y();
			this.dbg[4] = t.centX();
			this.dbg[5] = t.centY();
		}

		@Override
		public void draw(CameraModelSnapshot camera, Graphics2D graphics) {
			graphics.draw(new Arc2D.Double(
				(double) camera.world2pixX(this.coordinates[0]),
				(double) camera.world2pixY(this.coordinates[1]),
				(double) camera.world2pix(this.coordinates[2]),
				(double) camera.world2pix(this.coordinates[3]),
				this.coordinates[4],
				this.coordinates[5],
				Arc2D.OPEN
			));
			
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int x, y;
			graphics.fillRect(camera.world2pixX(this.dbg[0]), camera.world2pixY(this.dbg[1]), 2, 2);
			graphics.fillRect(camera.world2pixX(this.dbg[2]), camera.world2pixY(this.dbg[3]), 2, 2);
			graphics.fillRect(x = camera.world2pixX(this.dbg[4]), y = camera.world2pixY(this.dbg[5]), 2, 2);
			
			graphics.drawLine(camera.world2pixX(this.dbg[0]), camera.world2pixY(this.dbg[1]), camera.world2pixX(this.dbg[4]), camera.world2pixY(this.dbg[5]));
			graphics.drawLine(camera.world2pixX(this.dbg[2]), camera.world2pixY(this.dbg[3]), camera.world2pixX(this.dbg[4]), camera.world2pixY(this.dbg[5]));

			graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}
	}
}
