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
	
	/**
	 * Imports the current data from the track record.
	 * 
	 * @param tr
	 * @param world 
	 * @param vertexMapping Vertices in track record may be new - we need a mapping to the actual ID-s.
	 */
	public void importFrom(TrackRecord tr, World world, BiMap<Long, Long> vertexMapping) {
		this.type = tr.getType();
		this.metadata = tr.getMetadata();
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
}
