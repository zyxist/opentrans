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
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;

/**
 * Junction is a special type of vertex that has exactly one track connected,
 * and lies on another track, just like track objects. As name suggests, it is
 * used to make junctions for vehicles - they can change the track on them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Junction extends AbstractVertex {
	/**
	 * The track that determines the position of the junction.
	 */
	private Track masterTrack;
	/**
	 * The track connected to the junction.
	 */
	private Track slaveTrack;
	/**
	 * Position on the master track.
	 */
	private double position;
	/**
	 * Tangent in the junction towards the slave track.
	 */
	private double tangent;
	
	@Override
	public IVertexRecord createRecord(NetworkUnitOfWork unit) {
		return new JunctionRecord(this, unit);
	}

	@Override
	public double tangent() {
		return this.tangent;
	}

	@Override
	public double tangentFor(Track tr) {
		return this.tangent;
	}

	@Override
	public boolean hasAllTracks() {
		return false;
	}

	@Override
	public boolean hasOneTrack() {
		return this.slaveTrack != null;
	}

	@Override
	public boolean hasNoTracks() {
		return this.slaveTrack == null;
	}

	@Override
	public Track getTrack() {
		return this.slaveTrack;
	}

	@Override
	public void removeTrack(Track track) {
		if(track == this.slaveTrack) {
			this.slaveTrack = null;
		}
	}

	@Override
	public Track getFirstTrack() {
		return this.slaveTrack;
	}

	@Override
	public Track getSecondTrack() {
		return this.slaveTrack;
	}
	
	public Track getMasterTrack() {
		return this.masterTrack;
	}
	
	public Track getSlaveTrack() {
		return this.slaveTrack;
	}

	public double position() {
		return this.position;
	}

	/**
	 * Imports the vertex data from the junction record.
	 * 
	 * @param jr Junction record.
	 * @param world World.
	 */
	public void importFrom(JunctionRecord jr, World world) {
		if(null != this.pos) {
			this.pos.getSegment().removeVertex(this);
		}
		this.pos = world.findPosition(jr.x, jr.y);
		this.pos.getSegment().addVertex(this);
		this.position = jr.position();
		this.tangent = jr.tangent();
	}
	
	@Override
	public void importConnections(IVertexRecord vr, World world, BiMap<Long, Long> trackMapping) {
		JunctionRecord jr = (JunctionRecord) vr;
		long idMaster = jr.getMasterTrack().getId();
		long idSlave = jr.getFirstTrackActualId();
		
		if(idMaster < IIdentifiable.NEUTRAL_ID) {
			idMaster = trackMapping.get(idMaster);
		}
		if(idSlave < IIdentifiable.NEUTRAL_ID) {
			idSlave = trackMapping.get(idSlave);
		}
		this.masterTrack = world.findTrack(idMaster);
		this.slaveTrack = world.findTrack(idSlave);		
	}
}
