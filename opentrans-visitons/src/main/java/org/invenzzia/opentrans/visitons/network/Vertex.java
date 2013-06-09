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
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * This is the default implementation of 'free' vertex, which can be
 * put anywhere.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Vertex extends AbstractVertex {
	/**
	 * The first connected track.
	 */
	private Track firstTrack;
	/**
	 * The second connected track.
	 */
	private Track secondTrack;
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
	
	@Override
	public IVertexRecord createRecord(NetworkUnitOfWork unit) {
		return new VertexRecord(this);
	}
	
	/**
	 * Injects new coordinates - the method fails, if none are set. The method exists primarily
	 * for the debugging/testing purposes and should not be used.
	 * 
	 * @param coordinate 
	 */
	public void setPos(SegmentCoordinate pos) {
		Preconditions.checkState(this.pos == null, "The position is already set.");
		this.pos = pos;
	}
	
	/**
	 * Tangent specifies the slope of the tangent line given in this vertex. It is calculated
	 * from the tracks connected to it.
	 * 
	 * @return Tangent in this point.
	 */
	@Override
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
	 * @param tr Track.
	 * @return Tangent for this track.
	 */
	@Override
	public double tangentFor(Track tr) {
		if(tr == this.firstTrack) {
			return this.t1;
		} else if(tr == this.secondTrack) {
			return this.t2;
		} else {
			throw new IllegalArgumentException("The track #"+tr.getId()+" is not connected to vertex #"+this.getId());
		}
	}
	
	@Override
	public Track getFirstTrack() {
		return this.firstTrack;
	}
	
	@Override
	public Track getSecondTrack() {
		return this.secondTrack;
	}
	
	@Override
	public boolean hasAllTracks() {
		return (this.firstTrack != null) &&	(this.secondTrack != null);
	}
	
	@Override
	public boolean hasOneTrack() {
		return (this.firstTrack != null) ^ (this.secondTrack != null);
	}
	
	@Override
	public boolean hasNoTracks() {
		return (this.firstTrack == null) &&	(this.secondTrack == null);
	}
	
	@Override
	public Track getTrack() {
		if(this.firstTrack == null) {
			return this.secondTrack;
		}
		return this.firstTrack;
	}
	
	/**
	 * Removes the track from the vertex. This method shall be used only by the {@link World} instances.
	 * 
	 * @param track 
	 */
	@Override
	public void removeTrack(Track track) {
		if(this.firstTrack == track) {
			this.firstTrack = null;
		} else if(this.secondTrack == track) {
			this.secondTrack = null;
		}
	}
	
	/**
	 * Sets the tracks connected to this vertex. The method exists primarily for the debug/testing
	 * purposes and should not be used.
	 * 
	 * @param t1
	 * @param t2 
	 */
	@Deprecated
	public void setTracks(Track t1, Track t2) {
		this.firstTrack = t1;
		this.secondTrack = t2;
	}
	
	/**
	 * Imports the vertex data from the vertex record.
	 * 
	 * @param vr
	 * @param world Helper world object. We need some assistance from it.
	 */
	public void importFrom(VertexRecord vr, World world) {
		if(null != this.pos) {
			this.pos.getSegment().removeVertex(this);
		}
		this.pos = world.findPosition(vr.x(), vr.y());
		this.pos.getSegment().addVertex(this);
		
		this.t1 = vr.firstTangent();
		this.t2 = vr.secondTangent();
	}
	
	/**
	 * Imports the track data from the track record.
	 * 
	 * @param vr
	 * @param world
	 * @param trackMapping 
	 */
	@Override
	public void importConnections(IVertexRecord vr, World world, BiMap<Long, Long> trackMapping) {
		long id1 = vr.getFirstTrackActualId();
		long id2 = vr.getSecondTrackActualId();
		if(id1 < IIdentifiable.NEUTRAL_ID) {
			id1 = trackMapping.get(id1);
		}
		if(id2 < IIdentifiable.NEUTRAL_ID) {
			id2 = trackMapping.get(id2);
		}
		this.firstTrack = world.findTrack(id1);
		this.secondTrack = world.findTrack(id2);
	}
}
