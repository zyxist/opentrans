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
import com.google.common.collect.BiMap;
import org.invenzzia.helium.data.interfaces.IIdentifiable;

/**
 * Represents a track in the network infrastructure graph. Tracks are created from
 * track records.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Track {
	/**
	 * Numerical identifier for the storage purposes.
	 */
	private long id;
	/**
	 * Switchable track type.
	 */
	private byte type;
	/**
	 * First connected vertex.
	 */
	private Vertex v1;
	/**
	 * Second connected vertex.
	 */
	private Vertex v2;
	/**
	 * Track length in world units (metres).
	 */
	private double length;
	/**
	 * Extra metadata for certain forms of tracks.
	 */
	private double metadata[];
	
	/**
	 * @return Unique track ID.
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Sets the track ID, if it is not set yet.
	 * 
	 * @param id New track ID.
	 */
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("The ID is already set for the track.");
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
	 * Returns the track type metadata. Do not even try to modify the returned array.
	 * 
	 * @return Track metadata for drawing etc.
	 */
	public double[] getMetadata() {
		return this.metadata;
	}
	
	public Vertex getFirstVertex() {
		return this.v1;
	}
	
	public Vertex getSecondVertex() {
		return this.v2;
	}
	
	/**
	 * Returns the length of this track.
	 * 
	 * @return Length of this track in world units.
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Sets the length. This method shall not be used outside the I/O code.
	 * 
	 * @param length 
	 */
	public void setLength(double length) {
		this.length = length;
	}
	
	/**
	 * Returns the vertex on the opposite side of the track.
	 * 
	 * @param tested The vertex we know.
	 * @return The vertex opposite to it.
	 */
	public Vertex getOppositeVertex(Vertex tested) {
		if(this.v1 == tested) {
			return this.v2;
		} else {
			return this.v1;
		}
	}
	
	/**
	 * Sets the track vertices. The method exists primarily for debugging/testing purposes and should
	 * not be used in the production.
	 * 
	 * @param v1
	 * @param v2 
	 */
	@Deprecated
	public void setVertices(Vertex v1, Vertex v2) {
		this.v1 = Preconditions.checkNotNull(v1);
		this.v2 = Preconditions.checkNotNull(v2);
	}
	
	/**
	 * Helper method for the {@link World} instance to deal with track removal.
	 */
	public void removeFromVertices() {
		this.v1.removeTrack(this);
		this.v2.removeTrack(this);
	}
	
	/**
	 * Imports the current data from the track record.
	 * 
	 * @param tr The source track record.
	 * @param world Find the actual vertex instances here.
	 * @param vertexMapping Vertices in track record may be new - we need a mapping to the actual ID-s.
	 */
	public void importFrom(TrackRecord tr, World world, BiMap<Long, Long> vertexMapping) {
		this.type = tr.getType();		
		
		this.metadata = tr.getMetadata();
		long actualId = tr.getFirstVertex().getId();
		if(actualId < IIdentifiable.NEUTRAL_ID) {
			actualId = vertexMapping.get(Long.valueOf(actualId));
		}
		this.v1 = world.findVertex(actualId);
		actualId = tr.getSecondVertex().getId();
		if(actualId < IIdentifiable.NEUTRAL_ID) {
			actualId = vertexMapping.get(Long.valueOf(actualId));
		}
		this.v2 = world.findVertex(actualId);
		
		// Metadata must be converted to relative coordinates.
		double originalMeta[] = tr.getMetadata();
		if(null != originalMeta) {
			double metadata[] = new double[originalMeta.length];
			System.arraycopy(originalMeta, 0, metadata, 0, originalMeta.length);
			double dx = tr.getFirstVertex().x();
			double dy = tr.getFirstVertex().y();
			switch(this.type) {
				case NetworkConst.TRACK_STRAIGHT:
					metadata[0] -= dx;
					metadata[1] -= dy;
					metadata[2] -= dx;
					metadata[3] -= dy;
					break;
				case NetworkConst.TRACK_FREE:
					metadata[12] -= dx;
					metadata[13] -= dy;
					metadata[18] -= dx;
					metadata[19] -= dy;
					// Do not add break here.
				case NetworkConst.TRACK_CURVED:
					metadata[0] -= dx;
					metadata[1] -= dy;
					metadata[6] -= dx;
					metadata[7] -= dy;
					break;

			}
			this.metadata = metadata;
		}
		
		this.length = tr.getLength();
	}
	
	/**
	 * Imports the connections to the vertices.
	 * 
	 * @param tr
	 * @param world
	 * @param vertexMapping 
	 */
	public void importConnections(TrackRecord tr, World world, BiMap<Long, Long> vertexMapping) {
		long id1 = tr.getFirstVertex().getId();
		long id2 = tr.getSecondVertex().getId();
		if(id1 < IIdentifiable.NEUTRAL_ID) {
			id1 = vertexMapping.get(id1);
		}
		if(id2 < IIdentifiable.NEUTRAL_ID) {
			id2 = vertexMapping.get(id2);
		}
		this.v1 = world.findVertex(id1);
		this.v2 = world.findVertex(id2);
	}
	
	/**
	 * When the world is being extended, the absolute coordinates stored in the 
	 * metadata might be a bit problematic. We must modify them.
	 * 
	 * @param dx
	 * @param dy 
	 */
	public void adjustMetadata(double dx, double dy) {
		
	}
}
