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
package org.invenzzia.opentrans.visitons.infrastructure;

import com.google.common.base.Preconditions;

/**
 * One of three primitives for building tracks: a straight line.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StraightTrack extends AbstractTrack<StraightTrack> {
	/**
	 * Line angles, for the view point of vertex 0 and vertex 1.
	 */
	private double angles[];
	
	public StraightTrack(long id) {
		super(id);
		this.angles = new double[2];
	}
	
	/**
	 * In case of a straight line, each coordinates are valid, so we always return true.
	 * 
	 * @param vertex
	 * @param x
	 * @param y
	 * @return Always true.
	 */
	@Override
	public boolean isVertexChangeAllowed(IVertex vertex, double x, double y) {
		return true;
	}
	
	@Override
	public void verticesUpdated() {
		this.angles[0] = Math.atan2(this.vertices[1].y() - this.vertices[0].y(), this.vertices[1].x() - this.vertices[0].y());
		this.angles[1] = Math.atan2(this.vertices[0].y() - this.vertices[1].y(), this.vertices[0].x() - this.vertices[1].y());
	}
	
	@Override
	public StraightTrack fork() {
		StraightTrack track = new StraightTrack(this.getId());
		track.vertices[0] = this.vertices[0];
		track.vertices[1] = this.vertices[1];
		track.angles[0] = this.angles[0];
		track.angles[1] = this.angles[1];
		return track;
	}

	@Override
	public void copyFrom(StraightTrack copy) {
		Preconditions.checkArgument(copy.getId() == this.getId(), "Cannot copy from a track with a different ID.");
		this.vertices[0] = copy.vertices[0];
		this.vertices[1] = copy.vertices[1];
		this.angles[0] = copy.angles[0];
		this.angles[1] = copy.angles[1];
	}
}
