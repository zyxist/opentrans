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
import org.invenzzia.opentrans.visitons.geometry.LineOps;
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
	 * Reference to the straight track. There can be only one straight track assigned
	 * to each vertex; otherwise geometrical calculations make no sense. Of course, it
	 * does not have to be.
	 */
	protected StraightTrack straightTrack;
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
		for(ITrack track: this.tracks) {
			if(!track.isVertexChangeAllowed(this, this.tempX, this.tempY)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void applyUpdate() {
		if(null != this.tempSegment) {
			this.x = this.tempX;
			this.y = this.tempY;
			this.segment = this.tempSegment;
			for(ITrack track: this.tracks) {
				if(null != track) {
					track.verticesUpdated();
				}
			}
		}
	}

	@Override
	public void rollbackUpdate() {
		this.tempX = 0.0;
		this.tempY = 0.0;
		this.tempSegment = null;
		for(ITrack track: this.tracks) {
			if(null != track) {
				track.verticesNotUpdated();
			}
		}
	}

	@Override
	public void markAsDeleted() {
	}

	@Override
	public boolean isDeleted() {
		return false;
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
	public void expand(int by) {
		Preconditions.checkArgument(by > 0, "'by' argument must be greater than 0.");
		ITrack tt[] = new ITrack[this.tracks.length + by];
		System.arraycopy(tt, 0, this.tracks, 0, this.tracks.length);
		this.tracks = tt;
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
		if(track instanceof StraightTrack) {
			if(null != this.straightTrack) {
				throw new IllegalArgumentException(String.format("Straight track already assigned to vertex #%d", this.id));
			}
			this.straightTrack = (StraightTrack) track;
		}
		this.tracks[id] = Preconditions.checkNotNull(track);
	}
	
	@Override
	public void getTangent(int from, double tan[]) {
		if(null != this.straightTrack) {
			IVertex another = this.straightTrack.getVertex(0);
			if(another == this) {
				another = this.straightTrack.getVertex(1);
			}
			LineOps.toGeneral(this.x, this.y, another.x(), another.y(), from, tan);
		} else {
			ITrack tt = this.tracks[0];
			IVertex another = this.straightTrack.getVertex(0);
			if(another == this) {
				tt.getTangentInVertex(1, from, tan);
			} else {
				tt.getTangentInVertex(0, from, tan);
			}
		}
	}

}
