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
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.ILightMemento;
import org.invenzzia.opentrans.visitons.geometry.ArcOps;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject.TrackObjectRecord;

/**
 * Track record can be used by the GUI thread to represent the currently edited
 * tracks. It has a slightly different structure and API than a regular {@link Track},
 * oriented towards editing. Track record and {@link Track} can be synchronized
 * in both directions. The ID is used to match these two types of objects.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackRecord implements ILightMemento {
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
	 * The length of this track in world units (metres).
	 */
	private double length;
	/**
	 * Geometrical metadata.
	 */
	private double metadata[];
	/**
	 * Records of all the track objects on this track.
	 */
	private List<TrackObjectRecord> trackObjects;
	
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
		
		this.trackObjects = new LinkedList<>();
		
		this.id = track.getId();
		this.type = track.getType();
		this.metadata = track.getMetadata();
		
		// Metadata must be converted to absolute coordinates.
		double originalMeta[] = track.getMetadata();
		if(null != originalMeta) {
			double metadata[] = new double[originalMeta.length];
			System.arraycopy(originalMeta, 0, metadata, 0, originalMeta.length);
			Segment segment = track.getFirstVertex().pos().getSegment();

			double dx = track.getFirstVertex().pos().getAbsoluteX();
			double dy = track.getFirstVertex().pos().getAbsoluteY();
			this.metadata = metadata;
			this.moveMetadataPointsByDelta(dx, dy);			
		}
		if(track.hasTrackObjects()) {
			List<TrackObject> localList = track.getTrackObjects();
			this.trackObjects = new ArrayList<>(localList.size());
			for(TrackObject to: localList) {
				TrackObjectRecord record = new TrackObjectRecord();
				record.importFrom(to);
				this.trackObjects.add(record);
			}
		}
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
	 * Returns <strong>true</strong>, if this track has a destination ID, not temporary one. It means
	 * that we need to update an existing track, not create a new one.
	 * 
	 * @return True, if this track is persisted.
	 */
	public boolean isPersisted() {
		return this.id > IIdentifiable.NEUTRAL_ID;
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
	
	public TrackRecord getOppositeTrack(TrackRecord anotherTrack) {
		if(anotherTrack.hasVertex(this.v1)) {
			if(this.v2.hasOneTrack()) {
				return null;
			}
			return this.v2.getOppositeTrack(this);
		} else {
			if(this.v1.hasOneTrack()) {
				return null;
			}
			return this.v1.getOppositeTrack(this);
		}
	}
	
	/**
	 * Returns the vertex that connects the current track with the specified track.
	 * 
	 * @param firstTrack
	 * @return Vertex that connects us with the specified track.
	 */
	public VertexRecord getVertexTo(TrackRecord firstTrack) {
		Preconditions.checkNotNull(firstTrack);
		if(firstTrack.hasVertex(this.v1)) {
			return this.v1;
		} else if(firstTrack.hasVertex(this.v2)) {
			return this.v2;
		}
		throw new IllegalArgumentException("The specified track is not connected to this track.");
	}
	
	/**
	 * Returns true, if one of the vertices has <tt>hasOneTrack() == true</tt>.
	 * 
	 * @return 
	 */
	public boolean isOpen() {
		return (this.v1.hasOneTrack() || this.v2.hasOneTrack());
	}
	
	/**
	 * This method is valid only for the open tracks, where one of the vertices has
	 * exactly 1 track connected. It returns the previous track.
	 * 
	 * @return Previous track or NULL, if this is a lone track.
	 */
	public TrackRecord getPreviousTrack() {
		VertexRecord checked = this.v1.hasOneTrack() ? this.v2 : this.v1;
		if(checked.hasOneTrack()) {
			return null;
		}
		return checked.getOppositeTrack(this);
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
	 * Converts the position from range <tt>[0.0, 1.0]</tt> to the point-tangent information.
	 * 
	 * @param t Position on a track.
	 * @return Point coordinates and the tangent.
	 */
	public Characteristics getPointCharacteristics(double t) {
		switch(this.type) {
			case NetworkConst.TRACK_STRAIGHT:
				return new Characteristics(
					LineOps.linePoint(t, this.v1.x(), this.v2.x()),
					LineOps.linePoint(t, this.v1.y(), this.v2.y()),
					this.v1.tangentFor(this)
				);
			case NetworkConst.TRACK_CURVED:
			case NetworkConst.TRACK_FREE:
		}
		throw new UnsupportedOperationException("Not supported yet.");
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
	 * Sometimes there is a need to modify the positions set in the metadata by adding
	 * some DX,DY vector to them.
	 * 
	 * @param dx
	 * @param dy 
	 */
	public final void moveMetadataPointsByDelta(double dx, double dy) {
		switch(this.type) {
			case NetworkConst.TRACK_STRAIGHT:
				metadata[0] += dx;
				metadata[1] += dy;
				metadata[2] += dx;
				metadata[3] += dy;
				break;
			case NetworkConst.TRACK_FREE:
				metadata[8] += dx;
				metadata[9] += dy;
				metadata[14] += dx;
				metadata[15] += dy;
				// Do not add break here.
			case NetworkConst.TRACK_CURVED:
				metadata[0] += dx;
				metadata[1] += dy;
				metadata[6] += dx;
				metadata[7] += dy;
				break;
		}
	}
	
	/**
	 * Computes and returns the length of this track record. The method does not change
	 * the state of the object.
	 * 
	 * @return Computes and returns the length of this track record.
	 */
	public double computeLength() {
		switch(this.type) {
			case NetworkConst.TRACK_STRAIGHT:
				return LineOps.lineLength(this.v1.x(), this.v1.y(), this.v2.x(), this.v2.y());
			case NetworkConst.TRACK_CURVED:
				return ArcOps.arcLength(this.metadata[5], LineOps.distance(this.v1.x(), this.v1.y(), this.metadata[6], this.metadata[7]));
			case NetworkConst.TRACK_FREE:
				return
					ArcOps.arcLength(this.metadata[5], LineOps.distance(this.v1.x(), this.v1.y(), this.metadata[6], this.metadata[7]))
					+ ArcOps.arcLength(this.metadata[17], LineOps.distance(this.v1.x(), this.v1.y(), this.metadata[18], this.metadata[19]));
		}
		throw new IllegalStateException("Invalid track type: "+this.type+" (track ID: "+this.id+")");
	}
	
	/**
	 * Returns the currently persisted length of this track.
	 * 
	 * @return 
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Computes and persists the length of this track.
	 */
	public void updateLength() {
		this.length = this.computeLength();
	}

	/**
	 * Replaces the tracks with places.
	 */
	public void replaceVertices() {
		VertexRecord tmp = this.v1;
		this.v1 = this.v2;
		this.v2 = tmp;
	}

	@Override
	public Object getMemento() {
		return new TrackRecordLightMemento(this.metadata, this.length, this.type);
	}

	@Override
	public void restoreMemento(Object memento) {
		TrackRecordLightMemento casted = (TrackRecordLightMemento) memento;
		this.metadata = casted.metadata;
		this.length = casted.length;
		this.type = casted.type;
	}
	
	/**
	 * Returns an immutable copy of the track object list.
	 * 
	 * @return Immutable copy of the track object list.
	 */
	public List<TrackObjectRecord> getTrackObjects() {
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
}

/**
 * These light mementos are used by transformation to remember the initial geometry
 * state before applying the transformations. If we encounter that we have broken
 * anything, we can restore the original state and send cancellation.
 * 
 * @author Tomasz Jędrzejewski
 */
class TrackRecordLightMemento {
	public final double metadata[];
	public final double length;
	public final byte type;
	
	public TrackRecordLightMemento(double metadata[], double length, byte type) {
		this.metadata = metadata;
		this.length = length;
		this.type = type;
	}
}