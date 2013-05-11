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
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Vertex implements IIdentifiable {
	/**
	 * Unique numerical identifier of this vertex.
	 */
	private long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Where the vertex is located?
	 */
	private SegmentCoordinate position;
	/**
	 * The vertex tangent.
	 */
	private double tangent;
	/**
	 * The first connected track.
	 */
	private Track firstTrack;
	/**
	 * The second connected track.
	 */
	private Track secondTrack;
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the ID of the vertex.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the information about the position of this vertex on the world.
	 * 
	 * @return 
	 */
	public SegmentCoordinate pos() {
		return this.position;
	}
	
	/**
	 * Tangent specifies the slope of the tangent line given in this vertex. It is calculated
	 * from the tracks connected to it.
	 * 
	 * @return Tangent in this point.
	 */
	public double tangent() {
		return this.tangent;
	}
	
	public Track getFirstTrack() {
		return this.firstTrack;
	}
	
	public Track getSecondTrack() {
		return this.secondTrack;
	}
	
	public boolean hasAllTracks() {
		return (this.firstTrack != null) &&	(this.secondTrack != null);
	}
	
	public boolean hasOneTrack() {
		return (this.firstTrack != null) ^ (this.secondTrack != null);
	}
	
	public boolean hasNoTracks() {
		return (this.firstTrack == null) &&	(this.secondTrack == null);
	}
	
	/**
	 * If only one track is connected, the method returns it.
	 */
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
	void removeTrack(Track track) {
		if(this.firstTrack == track) {
			this.firstTrack = null;
		} else if(this.secondTrack == track) {
			this.secondTrack = null;
		}
	}
	
	/**
	 * Imports the vertex data from the vertex record.
	 * 
	 * @param vr
	 * @param world Helper world object. We need some assistance from it.
	 */
	public void importFrom(VertexRecord vr, World world) {
		if(null != this.position) {
			this.position.getSegment().removeVertex(this);
		}
		this.position = world.findPosition(vr.x(), vr.y());
		this.position.getSegment().addVertex(this);
		
		this.tangent = vr.tangent();
	}
	
	/**
	 * Imports the track data from the track record.
	 * 
	 * @param vr
	 * @param world
	 * @param trackMapping 
	 */
	public void importConnections(VertexRecord vr, World world, BiMap<Long, Long> trackMapping) {
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
