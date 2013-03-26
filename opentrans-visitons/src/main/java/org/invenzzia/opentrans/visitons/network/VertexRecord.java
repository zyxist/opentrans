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
	 * Connected tracks. In case of a record, this array can contain two types of objects:
	 * <ul>
	 *  <li>{@link TrackRecord} - reference to an imported track record,</li>
	 *  <li>{@link Long} - the ID of the track that has not been imported to the editable model (proxy).</li>
	 * </ul>
	 * Note that certain geometrical transformations cannot pass through vertex records that contain
	 * proxies. In this case proxies must be replaced by the imported records.</li>
	 */
	private Object tracks[];
	
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
	
	public int getTrackNum() {
		if(null == this.tracks) {
			return 0;
		}
		return this.tracks.length;
	}
	
	public void addTrack(TrackRecord record) {
		if(null == this.tracks) {
			this.tracks = new Object[1];
			this.tracks[0] = record;
		} else {
			Object newTracks[] = new Object[this.tracks.length + 1];
			System.arraycopy(this.tracks, 0, newTracks, 0, this.tracks.length);
			newTracks[this.tracks.length] = record;
			this.tracks = newTracks;
		}
	}
	
	public void addTrack(long trackId) {
		if(null == this.tracks) {
			this.tracks = new Object[1];
			this.tracks[0] = Long.valueOf(trackId);
		} else {
			Object newTracks[] = new Object[this.tracks.length + 1];
			System.arraycopy(this.tracks, 0, newTracks, 0, this.tracks.length);
			newTracks[this.tracks.length] = Long.valueOf(trackId);
			this.tracks = newTracks;
		}
	}

	public Object getTrack(int i) {
		if(null == this.tracks) {
			throw new IllegalArgumentException("Invalid index: "+i);
		}
		Preconditions.checkElementIndex(i, this.tracks.length);
		return this.tracks[i];
	}
}
