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
import org.invenzzia.opentrans.visitons.utils.MutableSegmentCoordinate;
import org.invenzzia.opentrans.visitons.world.Segment;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Vertex extends MutableSegmentCoordinate implements IVertex<Vertex> {
	/**
	 * Contains the references to edges coming out from this vertex.
	 */
	protected ITrack[] tracks;
	/**
	 * Temporary X, used in the move operation.
	 */
	protected double tempX;
	/**
	 * Temporary Y, used in the move operation.
	 */
	protected double tempY;
	/**
	 * Temporary segment, used in the move operation.
	 */
	protected Segment tempSegment;
	
	protected long id;

	/**
	 * Creates a vertex at the given coordinates.
	 *
	 * @param edgeNum
	 * @param x
	 * @param y
	 */
	public Vertex(long id, int trackNum, Segment segment, double x, double y) {
		super(segment, x, y);
		this.tracks = new ITrack[trackNum];
		this.id = id;
	}

	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void registerUpdate(Segment segment, double x, double y) {
		this.tempX = x;
		this.tempY = y;
		this.tempSegment = Preconditions.checkNotNull(segment, "Attempt to set an empty segment.");
	}

	@Override
	public boolean isUpdatePossible() {
		return false;
	}

	@Override
	public void applyUpdate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void rollbackUpdate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void markAsDeleted() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isDeleted() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public Vertex fork() {
		Vertex nv = new Vertex(this.getId(), this.tracks.length, this.segment, this.x, this.y);
		System.arraycopy(this.tracks, 0, nv.tracks, 0, this.tracks.length);
		return nv;
	}
	
	@Override
	public void copyFrom(Vertex copy) {
		Preconditions.checkArgument(copy.id == this.id, "The data can be copied from vertices with the same ID only.");

		this.x = copy.x;
		this.y = copy.y;
		this.tracks = new ITrack[copy.tracks.length];
		System.arraycopy(copy.tracks, 0, this.tracks, 0, this.tracks.length);
	}

	@Override
	public int getTrackCount() {
		return this.tracks.length;
	}

	@Override
	public ITrack[] getTracks() {
		ITrack tt[] = new ITrack[this.tracks.length];
		System.arraycopy(tt, 0, this.tracks, 0, this.tracks.length);
		return tt;
	}

	@Override
	public void setTrack(int id, ITrack track) {
		Preconditions.checkArgument(id >= 0 && id < this.tracks.length, "Invalid track index.");
		this.tracks[id] = Preconditions.checkNotNull(track);
	}
}
