/*
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
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * Element of the scene manager that describes the immutable track layout that is visible on the screen.
 * The snapshot contains simple immutable track object snapshots. They have the geometric information
 * necessary to draw them on the screen in the proper locations.
 * 
 * <p>If the scene is changed, the new track snapshot must be generated.
 * 
 * @author Tomasz Jędrzejewski
 */
@Deprecated
public class TrackSnapshot {
	/**
	 * Visible tracks to draw.
	 */
	private final List<ITrackRecord> tracks;
	
	/**
	 * Creates a new, empty track snapshot.
	 */
	public TrackSnapshot() {
		this.tracks = new LinkedList<>();
	}
	
	/**
	 * Adds a new drawable track object record.
	 * 
	 * @param dt Track object record.
	 */
	public void addDrawableTrack(ITrackRecord dt) {
		this.tracks.add(Preconditions.checkNotNull(dt));
	}
	
	/**
	 * Returns the list of all the track records kept by this snapshots. We don't care
	 * about the mutability, this must be fast, so we assume that you won't modify the
	 * returned list.
	 * 
	 * @return List of track records to draw.
	 */
	public List<ITrackRecord> getDrawableTracks() {
		return this.tracks;
	}
	
	/**
	 * Generic interface that defines an immutable record with the geometrical
	 * information about a certain track.
	 */
	public static interface ITrackRecord {
		/**
		 * Draws the given track on the screen.
		 * 
		 * @param camera The camera information snapshot.
		 * @param graphics The screen to draw on.
		 */
		public void draw(CameraModelSnapshot camera, Graphics2D graphics);
	}
	
	/**
	 * The record of the straight track: draws a straight line between two points.
	 */
	public static class StraightTrackRecord implements ITrackRecord {
		private final double coordinates[];
		
		public StraightTrackRecord(Track t) {
			this.coordinates = new double[4];
			Vertex v1 = t.getVertex(0);
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
	
	/**
	 * The record of a curved track: draws an arc between two points.
	 */
	public static class ArcTrackRecord implements ITrackRecord {
		private final double coordinates[];
		private final int convex;
		private final double dbg[];
		
		public ArcTrackRecord(Track t) {
			double metadata[] = t.getMetadata();
			
			this.convex = metadata[0] > 0.0 ? 1 : -1;
			this.coordinates = new double[6];
			this.dbg = new double[8];
			Vertex v1 = t.getVertex(0);
			Vertex v2 = t.getVertex(1);
			
			double angle1 = -Math.atan2(v1.y() - metadata[2], v1.x() - metadata[1]);
			if(angle1 < 0.0) {
				angle1 += 2* Math.PI;
			}
			double angle2 = -Math.atan2(v2.y() - metadata[2], v2.x() - metadata[1]);
			if(angle2 < 0.0) {
				angle2 += 2* Math.PI;
			}
			double dist = Geometry.angleDist(angle1, angle2, (byte)this.convex);
			double radius = Math.sqrt(Math.pow(v1.x() - metadata[1], 2) + Math.pow(v1.y() - metadata[2], 2));

			this.coordinates[0] = metadata[1] - radius;
			this.coordinates[1] = metadata[2] - radius;
			this.coordinates[2] = 2 * radius;
			this.coordinates[3] = 2 * radius;
			this.coordinates[4] = Math.toDegrees(angle1);
			this.coordinates[5] = Math.toDegrees(dist);
			
			this.dbg[0] = v1.x();
			this.dbg[1] = v1.y();
			this.dbg[2] = v2.x();
			this.dbg[3] = v2.y();
			this.dbg[4] = metadata[1];
			this.dbg[5] = metadata[2];
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
	
	/**
	 * The record of a free track: draws a Bezier curve between the two points. The control points
	 * are precomputed and cannot be freely moved.
	 */
	public static class FreeTrackRecord implements ITrackRecord {
		@Override
		public void draw(CameraModelSnapshot camera, Graphics2D graphics) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
