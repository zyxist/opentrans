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
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;

/**
 * Working copy of the {@link Junction}. Transformations are performed on the
 * records, which are later synchronized to the actual model.
 * 
 * @author Tomasz Jędrzejewski
 */
public class JunctionRecord extends AbstractVertexRecord {
	/**
	 * The master track that determines the position of the junction
	 */
	private TrackRecord masterTrack;
	/**
	 * Position on the master track.
	 */
	private double position;
	/**
	 * The track connected to the junction.
	 */
	private TrackRecord slaveTrack;
	/**
	 * If the slave track is not imported, we keep here its ID.
	 */
	private long slaveTrackId;
	/**
	 * Tangent in the junction towards the slave track.
	 */
	private double tangent;
	
	/**
	 * Creates a new junction record that is bound to the given track.
	 * 
	 * @param trackRecord 
	 */
	public JunctionRecord(TrackRecord trackRecord) {
		this.masterTrack = trackRecord;
		this.position = 0.0;
		Characteristics c = trackRecord.getPointCharacteristics(0.0);
		this.x = c.x();
		this.y = c.y();
	}
	
	public JunctionRecord(Junction junction, NetworkUnitOfWork unit) {
		this.setId(junction.getId());
		
		this.masterTrack = unit.findTrack(junction.getMasterTrack().getId());
		this.slaveTrackId = junction.getSlaveTrack().getId();
		
		this.position = junction.position();
		this.tangent = junction.tangent();
		
		Characteristics c = this.masterTrack.getPointCharacteristics(this.position);
		this.x = c.x();
		this.y = c.y();
	}

	@Override
	public double tangent() {
		return this.tangent;
	}

	@Override
	public double tangentFor(TrackRecord tr) {
		return this.tangent;
	}

	@Override
	public double oppositeTangentFor(TrackRecord tr) {
		return Geometry.normalizeAngle(this.tangent + Math.PI);
	}

	@Override
	public double getOpenTangent() {
		return Geometry.normalizeAngle(this.tangent + Math.PI);
	}

	@Override
	public boolean areTangentsOK() {
		return true;
	}

	@Override
	public boolean hasAllTracks() {
		return false;
	}

	@Override
	public boolean hasOneTrack() {
		return (this.slaveTrack != null) || (this.slaveTrackId != IIdentifiable.NEUTRAL_ID);
	}

	@Override
	public boolean hasNoTracks() {
		return (this.slaveTrack == null) && (this.slaveTrackId == IIdentifiable.NEUTRAL_ID);
	}

	@Override
	public TrackRecord getTrack() {
		return this.slaveTrack;
	}

	@Override
	public TrackRecord getFirstTrack() {
		return this.slaveTrack;
	}

	@Override
	public TrackRecord getSecondTrack() {
		return null;
	}

	@Override
	public long getFirstTrackId() {
		return this.slaveTrackId;
	}

	@Override
	public long getSecondTrackId() {
		return IIdentifiable.NEUTRAL_ID;
	}

	@Override
	public long getFirstTrackActualId() {
		if(null != this.slaveTrack) {
			return this.slaveTrack.getId();
		}
		return this.slaveTrackId;
	}

	@Override
	public long getSecondTrackActualId() {
		return IIdentifiable.NEUTRAL_ID;
	}

	@Override
	public void replaceReferenceWithRecord(TrackRecord tr) {
		if(tr.getId() == this.slaveTrackId) {
			this.slaveTrack = tr;
			this.slaveTrackId = IIdentifiable.NEUTRAL_ID;
		}
	}

	@Override
	public void removeTrack(TrackRecord track) {
		if(track == this.slaveTrack || track.getId() == this.slaveTrackId) {
			this.slaveTrack = null;
			this.slaveTrackId = IIdentifiable.NEUTRAL_ID;
		}
	}

	@Override
	public JunctionRecord setTangentFor(TrackRecord tr, double tangent) {
		if(tr == this.slaveTrack) {
			this.tangent = tangent;
		}
		return this;
	}
	
	public void setSlaveTrack(TrackRecord tr) {
		this.slaveTrack = tr;
	}
	
	/**
	 * Sets the position on the master track.
	 * 
	 * @param t 
	 */
	public void setPosition(double t) {
		Preconditions.checkArgument(t >= 0.0 && t <= 1.0, "position outside the range");
		Preconditions.checkState(this.masterTrack != null, "Please set the master track first!");
		this.position = t;

		Characteristics c = this.masterTrack.getPointCharacteristics(t);
		this.x = c.x();
		this.y = c.y();
		this.tangent = c.tangent();
	}
	
	public double position() {
		return this.position;
	}

	@Override
	public boolean hasUnimportedTracks() {
		return this.slaveTrackId != IIdentifiable.NEUTRAL_ID;
	}
	
	public TrackRecord getSlaveTrack() {
		return this.slaveTrack;
	}
	
	public TrackRecord getMasterTrack() {
		return this.masterTrack;
	}

	@Override
	public Object getMemento() {
		return new JunctionRecordLightMemento(this.x, this.y, this.position, this.tangent);
	}

	@Override
	public void restoreMemento(Object memento) {
		JunctionRecordLightMemento casted = (JunctionRecordLightMemento) memento;
		this.x = casted.x;
		this.y = casted.y;
		this.position = casted.position;
		this.tangent = casted.tangent;
	}
}

/**
 * These light mementos are used by transformation to remember the initial geometry
 * state before applying the transformations. If we encounter that we have broken
 * anything, we can restore the original state and send cancellation.
 * 
 * @author Tomasz Jędrzejewski
 */
class JunctionRecordLightMemento {
	public final double x;
	public final double y;
	public final double position;
	public final double tangent;
	
	public JunctionRecordLightMemento(double x, double y, double position, double tangent) {
		this.x = x;
		this.y = y;
		this.position = position;
		this.tangent = tangent;
	}
}
