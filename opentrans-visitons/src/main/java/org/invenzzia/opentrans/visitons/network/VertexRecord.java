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
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * Vertex record can be used by the GUI thread to represent the currently edited
 * vertices. It has a slightly different structure and API than a normal vertex,
 * more suitable for editing, that supports on-the-fly data recalculation. Vertex
 * records and normal vertex can be synchronized in both directions.
 * 
 * <p>The important difference between these two entity types is that vertex
 * record uses absolute coordinates, whereas vertex uses relative segment coordinates.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VertexRecord {
	/**
	 * The unique ID of the vertex. Allows proper mapping to the actual vertices. 
	 */
	private long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * The X location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	private double x;
	/**
	 * The Y location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	private double y;
	/**
	 * First connected track.
	 */
	private TrackRecord firstTrack;
	/**
	 * Second connected track
	 */
	private TrackRecord secondTrack;
	/**
	 * For the import from the domain model: ID of the connected, but unimported first track.
	 */
	private long firstTrackId = IIdentifiable.NEUTRAL_ID;
	/**
	 * For the import from the domain model: ID of the connected, but unimported second track.
	 */
	private long secondTrackId = IIdentifiable.NEUTRAL_ID;
	/**
	 * Tangent for the first track: specifies, which direction this track goes out to.
	 * Condition: <tt>t1 = t2 + PI</tt>
	 */
	private double t1;
	/**
	 * Tangent for the second track: specifies, which direction this track goes out to.
	 * Condition: <tt>t2 = t1 + PI</tt>
	 */
	private double t2;
	
	/**
	 * Creates a new, empty vertex record.
	 */
	public VertexRecord() {
	}
	
	/**
	 * Creates a record which maps to the given vertex.
	 * 
	 * @param vertex Source vertex.
	 */
	public VertexRecord(Vertex vertex) {
		Preconditions.checkNotNull(vertex, "The vertex cannot be null.");
		this.id = vertex.getId();
		SegmentCoordinate coord = vertex.pos();
		this.x = coord.getAbsoluteX();
		this.y = coord.getAbsoluteY();
		this.t1 = vertex.firstTangent();
		this.t2 = vertex.secondTangent();
		if(null != vertex.getFirstTrack()) {
			this.firstTrackId = vertex.getFirstTrack().getId();
		}
		if(null != vertex.getSecondTrack()) {
			this.secondTrackId = vertex.getSecondTrack().getId();
		}
	}
	
	/**
	 * Returns the vertex ID. The value <tt>IIdentifiable.NEUTRAL_ID</tt> is returned, if the vertex is not exported
	 * to the network model, and thus - no ID is given to it yet.
	 * 
	 * @return Vertex ID.
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of this vertex. The method shall be called only by the import/export code.
	 * Once set, the ID cannot be changed, as it uniquely identifies this vertex.
	 * 
	 * @param id Vertex ID
	 */
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("The vertex record ID cannot be changed.");
		}
		this.id = id;
	}
	
	/**
	 * Returns <strong>true</strong>, if this vertex has a destination ID, not temporary one. It means
	 * that we need to update an existing vertex, not create a new one.
	 * 
	 * @return True, if this vertex is persisted.
	 */
	public boolean isPersisted() {
		return this.id > IIdentifiable.NEUTRAL_ID;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	/**
	 * Sets the new position of the vertex. The position is given in the global units, and
	 * should not exceed the boundaries of the world.
	 * 
	 * @param x
	 * @param y 
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Tangent specifies the slope of the tangent line given in this vertex. It is calculated
	 * from the tracks connected to it.
	 * 
	 * @return Tangent in this point.
	 */
	public double tangent() {
		return this.t1;
	}
	
	public double firstTangent() {
		return this.t1;
	}
	
	public double secondTangent() {
		return this.t2;
	}
	
	/**
	 * Returns the tangent for the given track in this vertex.
	 * 
	 * @param tr Track record.
	 * @return Tangent for this track.
	 */
	public double tangentFor(TrackRecord tr) {
		if(tr == this.firstTrack) {
			return this.t1;
		} else if(tr == this.secondTrack) {
			return this.t2;
		} else {
			throw new IllegalArgumentException("The track #"+tr.getId()+" is not connected to vertex #"+this.getId());
		}
	}
	
	/**
	 * Returns the tangent for the opposite track.
	 * 
	 * @param tr
	 * @return 
	 */
	public double oppositeTangentFor(TrackRecord tr) {
		if(tr == this.firstTrack) {
			return this.t2;
		} else if(tr == this.secondTrack) {
			return this.t1;
		} else {
			throw new IllegalArgumentException("The track #"+tr.getId()+" is not connected to vertex #"+this.getId());
		}
	}
	
	/**
	 * This method can be used, if the vertex has only one track connected. It returns the opposite value
	 * of the tangent, so that it can be used to derive a new track.
	 * 
	 * @return The opposite tangent to the one already set.
	 */
	public double getOpenTangent() {
		Preconditions.checkState(this.hasOneTrack(), "This method can be used only for one-track vertices.");
		if(null != this.firstTrack || this.firstTrackId != IIdentifiable.NEUTRAL_ID) {
			return Geometry.normalizeAngle(this.t1 + Math.PI);
		} else {
			return Geometry.normalizeAngle(this.t2 + Math.PI);
		}
	}

	/**
	 * Basic setter for the tangent of the first vertex. Usually, you won't be interested
	 * in using this raw method.
	 * 
	 * @param tangent
	 * @return Fluent interface.
	 */
	public VertexRecord setFirstTangent(double tangent) {
		this.t1 = tangent;
		return this;
	}
	
	/**
	 * Basic setter for the tangent of the second vertex. Usually, you won't be interested
	 * in using this raw method.
	 * 
	 * @param tangent
	 * @return Fluent interface.
	 */
	public VertexRecord setSecondTangent(double tangent) {
		this.t2 = tangent;
		return this;
	}
	
	/**
	 * Sets the tangent for the specified track. Before applying this method, the
	 * user must ensure that the operation won't break the tangent condition.
	 * 
	 * @param tr
	 * @param tangent
	 * @return Fluent interface.
	 */
	public VertexRecord setTangentFor(TrackRecord tr, double tangent) {
		if(tr == this.firstTrack) {
			this.t1 = tangent;
		} else if(tr == this.secondTrack) {
			this.t2 = tangent;
		} else {
			throw new IllegalArgumentException("The track #"+tr.getId()+" is not connected to vertex #"+this.getId());
		}
		return this;
	}
	
	/**
	 * Validates the tangent condition. Returns true, if changing the tangent for
	 * the given track to the specified value is possible.
	 * 
	 * @param tr
	 * @param tangent
	 * @return True, if change is possible and won't break anything.
	 */
	public boolean tangentChangePossible(TrackRecord tr, double tangent) {
		return false;
	}
	
	public boolean hasAllTracks() {
		return (this.firstTrack != null || this.firstTrackId != IIdentifiable.NEUTRAL_ID) &&
			(this.secondTrack != null || this.secondTrackId != IIdentifiable.NEUTRAL_ID);
	}
	
	public boolean hasOneTrack() {
		return (this.firstTrack != null || this.firstTrackId != IIdentifiable.NEUTRAL_ID) ^
			(this.secondTrack != null || this.secondTrackId != IIdentifiable.NEUTRAL_ID);
	}
	
	public boolean hasNoTracks() {
		return (this.firstTrack == null && this.firstTrackId == IIdentifiable.NEUTRAL_ID) &&
			(this.secondTrack == null && this.secondTrackId == IIdentifiable.NEUTRAL_ID);
	}
	
	/**
	 * Returns true, if there are some unimported tracks connected to this vertex.
	 * 
	 * @return 
	 */
	public boolean hasUnimportedTracks() {
		return (this.firstTrackId != IIdentifiable.NEUTRAL_ID || this.secondTrackId != IIdentifiable.NEUTRAL_ID);
	}
	
	public void addTrack(TrackRecord record) {
		if(null == this.firstTrack && IIdentifiable.NEUTRAL_ID == this.firstTrackId) {
			this.firstTrack = record;
		} else if(null == this.secondTrack && IIdentifiable.NEUTRAL_ID == this.secondTrackId) {
			this.secondTrack = record;
		} else {
			throw new IllegalStateException("Cannot connect more than two tracks to a vertex.");
		}
	}
	
	public void addTrack(long trackId) {
		if(null == this.firstTrack && IIdentifiable.NEUTRAL_ID == this.firstTrackId) {
			this.firstTrackId = trackId;
		} else if(null == this.secondTrack && IIdentifiable.NEUTRAL_ID == this.secondTrackId) {
			this.secondTrackId = trackId;
		} else {
			throw new IllegalStateException("Cannot connect more than two tracks to a vertex.");
		}
	}

	/**
	 * Returns the track record connected to this vertex. Note that the track may not be imported
	 * from the world model. In this case, this method would return <strong>null</strong>, but the
	 * ID of the connected track could be obtained with {@link #getFirstTrackId()}. To extract the ID
	 * of the connected track regardless of the storage method, use {@link #getFirstTrackActualId()}.
	 * 
	 * @return First track record.
	 */
	public TrackRecord getFirstTrack() {
		return this.firstTrack;
	}
	
	/**
	 * Returns the track record connected to this vertex. Note that the track may not be imported
	 * from the world model. In this case, this method would return <strong>null</strong>, but the
	 * ID of the connected track could be obtained with {@link #getSecondTrackId()}. To extract the ID
	 * of the connected track regardless of the storage method, use {@link #getSecondTrackActualId()}.
	 * 
	 * @return Second track record.
	 */
	public TrackRecord getSecondTrack() {
		return this.secondTrack;
	}
	
	/**
	 * If only one track is connected, the method returns it.
	 */
	public TrackRecord getTrack() {
		if(this.firstTrack == null) {
			return this.secondTrack;
		}
		return this.firstTrack;
	}
	
	/**
	 * Returns the unimported track ID. See note to {@link #getFirstTrack()}.
	 * 
	 * @return ID of the first track, which is unimported from the world model.
	 */
	public long getFirstTrackId() {
		return this.firstTrackId;
	}
	
	/**
	 * Returns the unimported track ID. See note to {@link #getSecondTrack()}.
	 * 
	 * @return ID of the second track, which is unimported from the world model.
	 */
	public long getSecondTrackId() {
		return this.secondTrackId;
	}
	
	/**
	 * Returns the track ID regardless of the storage method. See note to {@link #getFirstTrack()}.
	 * 
	 * @return ID of the first track.
	 */
	public long getFirstTrackActualId() {
		return (null != this.firstTrack ? this.firstTrack.getId() : this.firstTrackId);
	}
	
	/**
	 * Returns the track ID regardless of the storage method. See note to {@link #getSecondTrack()}.
	 * 
	 * @return ID of the second track.
	 */
	public long getSecondTrackActualId() {
		return (null != this.secondTrack ? this.secondTrack.getId() : this.secondTrackId);
	}
	
	public void removeTrack(TrackRecord tr) {
		if(this.firstTrack == tr) {
			this.firstTrack = null;
			this.firstTrackId = IIdentifiable.NEUTRAL_ID;
		} else if(this.secondTrack == tr) {
			this.secondTrack = null;
			this.secondTrackId = IIdentifiable.NEUTRAL_ID;
		}
	}
	
	/**
	 * Returns the opposite track connected to the vertex. The method throws an exception,
	 * if no opposite track is found.
	 * 
	 * @throws IllegalStateException If no opposite track exists.
	 * @param tested Tested track
	 * @return Opposite track to the tested track in this vertex.
	 */
	public TrackRecord getOppositeTrack(TrackRecord tested) {
		if(this.firstTrack == tested) {
			return this.secondTrack;
		} else if(this.secondTrack == tested) {
			return this.firstTrack;
		}
		throw new IllegalStateException("No opposite track to '"+tested.getId()+"' in vertex '"+this.getId()+"'");
	}
	
	/**
	 * Returns the track connected to this vertex that leads to the given vertex.
	 * The track must exist and be imported.
	 * 
	 * @param v Opposite vertex.
	 * @return Track that connect this vertex with the specified vertex.
	 */
	public TrackRecord getTrackTo(VertexRecord v) {
		if(null != this.firstTrack && this.firstTrack.hasVertex(v)) {
			return this.firstTrack;
		} else if(null != this.secondTrack && this.secondTrack.hasVertex(v)) {
			return this.secondTrack;
		}
		return null;
	}
	
	/**
	 * This method is used while importing the track. It replaces the ID-based reference
	 * with the newly imported track.
	 * 
	 * @param tr 
	 */
	public void replaceReferenceWithRecord(TrackRecord tr) {
		if(this.firstTrackId == tr.getId()) {
			this.firstTrack = tr;
			this.firstTrackId = IIdentifiable.NEUTRAL_ID;
		} else if(this.secondTrackId == tr.getId()) {
			this.secondTrack = tr;
			this.secondTrackId = IIdentifiable.NEUTRAL_ID;
		}
	}
}
