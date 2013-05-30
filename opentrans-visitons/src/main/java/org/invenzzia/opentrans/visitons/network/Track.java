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
import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.network.objects.ITrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

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
	 * Various stationary objects put on the track.
	 */
	private List<TrackObject> trackObjects;
	
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
	 * Surprise! This method is the part of the public API, because track objects are added directly
	 * to the real tracks. The method also attaches the track itself to the track object.
	 * 
	 * @param trackObject 
	 */
	public void addTrackObject(TrackObject trackObject) {
		if(null == this.trackObjects) {
			this.trackObjects = new LinkedList<>();
		}
		this.trackObjects.add(trackObject);
		trackObject.setTrack(this);
	}
	
	/**
	 * Returns an immutable copy of the track object list.
	 * 
	 * @return Immutable copy of the track object list.
	 */
	public List<TrackObject> getTrackObjects() {
		if(null == this.trackObjects) {
			return ImmutableList.of();
		}
		return ImmutableList.copyOf(this.trackObjects);
	}
	
	/**
	 * Returns true, if the track has any track objects.
	 * 
	 * @return 
	 */
	public boolean hasTrackObjects() {
		return this.trackObjects != null;
	}
	
	/**
	 * We remove the track object by providing the actual object backed by it.
	 * 
	 * @param backedObject 
	 */
	public void removeTrackObject(ITrackObject backedObject) {
		if(null != this.trackObjects) {
			TrackObject toRemove = null;
			for(TrackObject to: this.trackObjects) {
				if(to.getObject().equals(backedObject)) {
					toRemove = to;
				}
			}
			if(null != toRemove) {
				this.trackObjects.remove(toRemove);
				if(this.trackObjects.isEmpty()) {
					this.trackObjects = null;
				}
			}
		}
	}
	
	/**
	 * Converts the position from range <tt>[0.0, 1.0]</tt> to the point-tangent information.
	 * 
	 * @param t Position on a track.
	 * @return Point coordinates and the tangent.
	 */
	public Characteristics getPointCharacteristics(double t) {
		double k, a, b;
		double ax = this.v1.pos().getAbsoluteX();
		double ay =  this.v1.pos().getAbsoluteY();
		switch(this.type) {
			case NetworkConst.TRACK_STRAIGHT:
				return new Characteristics(
					LineOps.linePoint(t, ax, this.v2.pos().getAbsoluteX()),
					LineOps.linePoint(t, ay, this.v2.pos().getAbsoluteY()),
					this.v1.tangentFor(this)
				);
			case NetworkConst.TRACK_CURVED:
				a = LineOps.getTangent(this.metadata[6], this.metadata[7], this.metadata[8], this.metadata[9]);
				k = t * this.metadata[5] + (this.metadata[3] > 0.0 ? a : -a);
				return new Characteristics(
					Math.cos(k) * (this.metadata[2] / 2.0) + this.metadata[6] + ax,
					Math.sin(k) * (this.metadata[2] / 2.0) + this.metadata[7] + ay,
					Geometry.normalizeAngle(k + Math.PI / 2.0)
				);
			case NetworkConst.TRACK_FREE:
		}
		throw new UnsupportedOperationException("Not supported yet.");
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
					metadata[8] -= dx;
					metadata[9] -= dy;
					metadata[14] -= dx;
					metadata[15] -= dy;
					metadata[0] -= dx;
					metadata[1] -= dy;
					metadata[6] -= dx;
					metadata[7] -= dy;
					break;
				case NetworkConst.TRACK_CURVED:
					metadata[0] -= dx;
					metadata[1] -= dy;
					metadata[6] -= dx;
					metadata[7] -= dy;
					metadata[8] -= dx;
					metadata[9] -= dy;
					metadata[10] -= dx;
					metadata[11] -= dy;
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
}
