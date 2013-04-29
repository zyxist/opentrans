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
	 * Creates a new track.
	 * 
	 * @param id
	 * @param record 
	 */
	public Track(TrackRecord record) {
		Preconditions.checkNotNull(record, "The TrackRecord passed to Track() constructor is empty.");
		this.type = record.getType();
		this.metadata = record.getMetadata();
	}
	
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
		if(-1 != this.id) {
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
}
