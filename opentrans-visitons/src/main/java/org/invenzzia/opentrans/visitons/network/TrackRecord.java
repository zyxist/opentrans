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

package org.invenzzia.opentrans.visitons.network;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.geometry.Point;

/**
 * Track record can be used by the GUI thread to represent the currently edited
 * tracks. It has a slightly different structure and API than a regular {@link Track},
 * oriented towards editing. Track record and {@link Track} can be synchronized
 * in both directions. The ID is used to match these two types of objects.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackRecord {
	/**
	 * The unique ID used for the identification.
	 */
	private long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * The track type.
	 */
	private byte type;
	/**
	 * First vertex this track is connected to.
	 */
	private VertexRecord v1 = null;
	/**
	 * Second vertex this track is connected to.
	 */
	private VertexRecord v2 = null;
	/**
	 * Geometrical metadata.
	 */
	private double metadata[];
	
	/**
	 * Creates a new, empty instance of track.
	 */
	public TrackRecord() {
	}
	
	/**
	 * Creates a track record which maps an existing {@link Track} object. We must also 
	 * provide the two records for the vertices, which must be imported separately. Note
	 * that this constructor creates a partially initialized object - you must call
	 * {@link #setVertices()} as soon as possible. See note for this method for the
	 * reason.
	 * 
	 * @param track The source track.
	 * @param firstVertex The record for the first vertex.
	 * @param secondVertex The record for the second vertex.
	 */
	public TrackRecord(Track track) {
		Preconditions.checkNotNull(track);
		this.id = track.getId();
		this.type = track.getType();
		this.metadata = track.getMetadata();
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("This track record already has an ID.");
		}
		this.id = id;
	}
	
	/**
	 * Return the type of the vertex: straight, curved or free.
	 * 
	 * @return Track type. See {@link NetworkConst} constants.
	 */
	public byte getType() {
		return this.type;
	}
	
	/**
	 * Sets the track type: {@link NetworkConst#TRACK_STRAIGHT},
	 * {@link NetworkConst#TRACK_CURVED} or {@link NetworkConst#TRACK_FREE}.
	 * 
	 * @param type Track type.
	 */
	public void setType(byte type) {
		Preconditions.checkArgument(type >= 0 && type <= 2, "Invalid track type: "+type);
		this.type = type;
	}

	/**
	 * This method is used by the importing code - we cannot put setting these
	 * pieces of code into the constructor, because we must put the newly created
	 * record into a map before importing vertices. Otherwise we could fall into
	 * a situation, where the same track would be imported several times.
	 * 
	 * @param v1
	 * @param v2 
	 */
	public void setVertices(VertexRecord firstVertex, VertexRecord secondVertex) {
		Preconditions.checkNotNull(firstVertex);
		Preconditions.checkNotNull(secondVertex);
		this.v1 = firstVertex;
		this.v2 = secondVertex;
	}

	public void setFreeVertex(VertexRecord newVertex) {
		if(null == this.v1) {
			this.v1 = newVertex;
		} else if(null == this.v2) {
			this.v2 = newVertex;
		} else {
			throw new IllegalStateException("Attept to connect a third vertex to a track.");
		}
	}
	
	public VertexRecord getFirstVertex() {
		return this.v1;
	}
	
	public VertexRecord getSecondVertex() {
		return this.v2;
	}
	
	public VertexRecord getOppositeVertex(VertexRecord tested) {
		if(this.v1 == tested) {
			return this.v2;
		} else {
			return this.v1;
		}
	}
	
	public void replaceVertex(VertexRecord oldVertex, VertexRecord newVertex) {
		if(this.v1 == oldVertex) {
			this.v1 = newVertex;
		} else if(this.v2 == oldVertex) {
			this.v2 = newVertex;
		} else {
			throw new IllegalStateException("The old vertex '"+oldVertex+"' is not connected to this track.");
		}
	}
	
	/**
	 * Returns true, if the given vertex belongs to this track.
	 * 
	 * @param vr Vertex to check.
	 * @return True, if it belongs to this track.
	 */
	public boolean hasVertex(VertexRecord vr) {
		return this.v1 == vr || this.v2 == vr;
	}
	
	/**
	 * Sets the track geometry metadata. The exact array structure and size depend on the track
	 * type.
	 * 
	 * @param metadata 
	 */
	public void setMetadata(double metadata[]) {
		this.metadata = metadata;
	}
	
	/**
	 * Returns the track metadata. The content of the array depends on the track type.
	 * Note that for performance reasons, the returned array <strong>is not a copy</strong>.
	 * Be careful.
	 * 
	 * @return Track geometry metadata.
	 */
	public double[] getMetadata() {
		return this.metadata;
	}
	
	/**
	 * Returns the control point for vertex <tt>vr</tt>, and this track. Control points
	 * help finding the proper orientation of some geometrical shapes.
	 * 
	 * @param vr Vertex belonging to this track.
	 * @return X coordinate of the control point for this vertex.
	 */
	public Point controlPoint(VertexRecord vr) {
		switch(this.type) {
			// In the straight track, we take the opposite vertex as a control point.
			case NetworkConst.TRACK_STRAIGHT:
				if(vr == this.v1) {
					return new Point(this.v2.x(), this.v2.y());
				} else {
					return new Point(this.v1.x(), this.v1.y());
				}
			// In other track types, control points must be calculated and provided in the
			// metadata.
			case NetworkConst.TRACK_CURVED:
				if(vr == this.v1) {
					return new Point(this.metadata[8], this.metadata[9]);
				} else {
					return new Point(this.metadata[10], this.metadata[11]);
				}
			case NetworkConst.TRACK_FREE:
				if(vr == this.v1) {
					return new Point(this.metadata[8], this.metadata[9]);
				} else {
					return new Point(this.metadata[22], this.metadata[23]);
				}
		}
		throw new IllegalStateException("Invalid track type: "+this.type);
	}
}
