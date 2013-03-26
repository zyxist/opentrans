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
 * Represents a track in the infrastructure graph. 
 * 
 * @author Tomasz Jędrzejewski
 */
public class Track {
	public static enum TrackType { STRAIGHT, CURVED, FREE };
	/**
	 * Numerical identifier for the storage purposes.
	 */
	private long id;
	/**
	 * Switchable track type.
	 */
	private TrackType type;
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
	 * Creates a new, default, unbound track with id = -1.
	 */
	public Track() {
		this.id = -1;
		this.type = TrackType.STRAIGHT;
	}
	
	public Track(TrackType type) {
		this.id = -1;
		this.type = Preconditions.checkNotNull(type);
	}
	
	/**
	 * Returns the ID of the track. If the ID is equal to -1, this track has not an ID assigned yet.
	 * 
	 * @return The ID of the track.
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of the track. This method may be called only if the track does not have an ID selected yet.
	 * 
	 * @param id The ID of the track.
	 */
	public void setId(long id) {
		if(this.id != -1) {
			throw new IllegalStateException("This track already has an ID.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the track length.
	 * 
	 * @return Current track length. 
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Returns one of the two vertices assigned to this track. 0 is the first vertex and
	 * 1 is the second vertex.
	 * 
	 * @param i The number of the vertex to return: 0 or 1.
	 * @return The vertex or NULL if the vertex is not assigned yet.
	 */
	public Vertex getVertex(int i) {
		if(i == 0) {
			return this.v1;
		} else {
			return this.v2;
		}
	}
	
	/**
	 * Sets a new vertex for the track.
	 * 
	 * @param i The ID of the vertex to store: 0 or 1.
	 * @param vertex The vertex to assign.
	 */
	public void setVertex(int i, Vertex vertex) {
		if(i == 0) {
			this.v1 = vertex;
		} else {
			this.v2 = vertex;
		}
	}
	
	/**
	 * Returns the vertex lying in the opposition to this one.
	 * 
	 * @param vertex The vertex we know.
	 * @return The opposite vertex.
	 */
	public Vertex getOppositeVertex(Vertex vertex) {
		if(vertex == this.v1) {
			return this.v2;
		}
		return this.v1;
	}
	
	/**
	 * Populates the first free vertex slot. If there is no free vertex slot, the method
	 * returns <strong>false</strong>.
	 * 
	 * @param vertex The vertex to set.
	 */
	public boolean setFreeVertex(Vertex vertex) {
		if(null == this.v1) {
			this.v1 = vertex;
			return true;
		} else if(null == this.v2) {
			this.v2 = vertex;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true, if the track is connected to the given vertex. If the argument is <strong>null</strong>,
	 * the method returns <strong>false</strong>.
	 * 
	 * @param vertex The vertex to check.
	 * @return True, if this vertex is connected.
	 */
	public boolean isConnectedToVertex(Vertex vertex) {
		if(null == vertex) {
			 return false;
		}
		return vertex.equals(this.v1) || vertex.equals(this.v2);
	}
	
	/**
	 * Returns <strong>true</strong>, if at least one of the vertex slots is empty.
	 * 
	 * @return True, if there is an empty slot.
	 */
	public boolean hasFreeVertexSlots() {
		return this.v1 == null || this.v2 == null;
	}
	
	/**
	 * Returns the type of this track: {@link TrackType#STRAIGHT}, {@link TrackType#FREE} or
	 * {@link TrackType#CURVED}.
	 * 
	 * @return Track type. 
	 */
	public TrackType getType() {
		return this.type;
	}
	
	/**
	 * Returns a copy of the internal metadata array. The actual content of the array depends on
	 * the track type.
	 * 
	 * @return Copy of the internal metadata array. 
	 */
	public double[] getMetadata() {
		double cpy[] = new double[this.metadata.length];
		System.arraycopy(this.metadata, 0, cpy, 0, this.metadata.length);
		return cpy;
	}
	
	/**
	 * Compute extra numeric metadata, depending on the track type. This method shall be called every
	 * time we change the geometry of the infrastructure.
	 */
	public void compute() {
		
	}
}
