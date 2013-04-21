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
	private long id = -1;
	/**
	 * The X location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	private double x;
	/**
	 * The Y location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	private double y;
	/**
	 * The curve tangent in this vertex.
	 */
	private double tangent;
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
	private long firstTrackId = -1;
	/**
	 * For the import from the domain model: ID of the connected, but unimported second track.
	 */
	private long secondTrackId = -1;
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		if(-1 != this.id) {
			throw new IllegalStateException("The vertex record ID cannot be changed.");
		}
		this.id = id;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double tangent() {
		return this.tangent;
	}
	
	public void setTangent(double tangent) {
		this.tangent = tangent;
	}
	
	public boolean hasAllTracks() {
		return (this.firstTrack != null || this.firstTrackId != -1) &&
			(this.secondTrack != null || this.secondTrackId != -1);
	}
	
	public boolean hasOneTrack() {
		return (this.firstTrack != null || this.firstTrackId != -1) ^
			(this.secondTrack != null || this.secondTrackId != -1);
	}
	
	public boolean hasNoTracks() {
		return (this.firstTrack == null && this.firstTrackId == -1) &&
			(this.secondTrack == null && this.secondTrackId == -1);
	}
	
	public void addTrack(TrackRecord record) {
		if(null == this.firstTrack && -1 == this.firstTrackId) {
			this.firstTrack = record;
		} else if(null == this.secondTrack && -1 == this.secondTrackId) {
			this.secondTrack = record;
		} else {
			throw new IllegalStateException("Cannot connect more than two tracks to a vertex.");
		}
	}
	
	public void addTrack(long trackId) {
		if(null == this.firstTrack && -1 == this.firstTrackId) {
			this.firstTrackId = trackId;
		} else if(null == this.secondTrack && -1 == this.secondTrackId) {
			this.secondTrackId = trackId;
		} else {
			throw new IllegalStateException("Cannot connect more than two tracks to a vertex.");
		}
	}

	public TrackRecord getFirstTrack() {
		return this.firstTrack;
	}
	
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
	
	public long getFirstTrackId() {
		return this.firstTrackId;
	}
	
	public long getSecondTrackId() {
		return this.secondTrackId;
	}
	
	public void removeTrack(TrackRecord tr) {
		if(this.firstTrack == tr) {
			this.firstTrack = null;
			this.firstTrackId = -1;
		} else if(this.secondTrack == tr) {
			this.secondTrack = null;
			this.secondTrackId = -1;
		} else {
			throw new IllegalArgumentException("The track '"+tr.getId()+"' is not connected to vertex '"+this.getId()+"'");
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
}
