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
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
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
}
