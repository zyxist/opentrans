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
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackRecord {
	/**
	 * The unique ID used for the identification.
	 */
	private long id = -1;
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
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		if(-1 != this.id) {
			throw new IllegalStateException("This track record already has an ID.");
		}
		this.id = id;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public void setType(byte type) {
		this.type = type;
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
}
