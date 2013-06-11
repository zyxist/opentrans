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
package org.invenzzia.opentrans.visitons.data;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.Parent;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.network.IVertex;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.objects.INamedTrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject.TrackObjectRecord;


/**
 * Common data of a vehicle.
 */
class VehicleBase implements IIdentifiable {
	/**
	 * Unique, internal, non-modifiable vehicle ID.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Name of this vehicle.
	 */
	private String name;
	
	private double speed;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the previously set ID.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the name of the vehicle.
	 * 
	 * @return Stop name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the new vehicle name.
	 * 
	 * @param name Vehicle name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	
}

/**
 * Represents a single vehicle.
 * 
 * @author zyxist
 */
public class Vehicle extends VehicleBase implements IMemento<Project>, INamedTrackObject {
	/**
	 * The vehicle type that describes the attributes of this vehicle.
	 */
	private final Parent<VehicleType> vehicleType = new Parent<>();
	/**
	 * New name that should be set after formal verification.
	 */
	private String newName = null;
	/**
	 * Describes the position of the vehicle on the track.
	 */
	private TrackObject<Vehicle> trackObject;
	
	/**
	 * Returns the reference to the parent vehicle type.
	 * @return Parent vehicle type reference.
	 */
	public Parent<VehicleType> getVehicleType() {
		return this.vehicleType;
	}
	
	/**
	 * Returns the new name for verification.
	 * 
	 * @return New name.
	 */
	public String getNewName() {
		return this.newName;
	}
	
	/**
	 * Sets the new name that should be verified before applying.
	 * 
	 * @param name 
	 */
	public void setNewName(String name) {
		this.newName = name;
	}
	
	/**
	 * Applies the new name - it is correct and unique.
	 */
	public void applyName() {
		this.setName(this.newName);
		this.newName = null;
	}
	
	/**
	 * Reject the new name, and clear the memory.
	 */
	public void rejectName() {
		this.newName = null;
	}
	
	
	@Override
	public Object getMemento(Project domainModel) {
		VehicleRecord memento = new VehicleRecord();
		memento.importData(this, domainModel);
		return memento;
	}

	@Override
	public void restoreMemento(Object memento, Project domainModel) {
		Preconditions.checkNotNull(memento, "Attempt to restore an empty memento.");
		if(!(memento instanceof VehicleRecord)) {
			throw new IllegalArgumentException("Invalid memento for Vehicle class: "+memento.getClass().getCanonicalName());
		}
		VehicleRecord record = (VehicleRecord) memento;
		record.exportData(this, domainModel);
		this.id = record.id;
	}
	
	public void setTrackObject(TrackObject<Vehicle> trackObject) {
		if(null != this.trackObject) {
			this.trackObject.setObject(null);
			this.trackObject.getTrack().removeTrackObject(this);
		}
		this.trackObject = trackObject;
		if(null != this.trackObject) {
			this.trackObject.setObject(this);
		}
	}

	@Override
	public String getTrackObjectName() {
		return this.getName();
	}

	@Override
	public TrackObject getTrackObject() {
		return this.trackObject;
	}

	@Override
	public int getType() {
		return NetworkConst.TRACK_OBJECT_VEHICLE;
	}
	
	/**
	 * For the accurate drawing of a vehicle, we must compute the 'knots': places, where the
	 * segments join. The method returns an array with X,Y coordinates of all the knots in
	 * the proper orientation along tracks.
	 * 
	 * @return 
	 */
	public double[] computeKnots() {
		VehicleType vt = this.getVehicleType().get();
		int segments = vt.getNumberOfSegments();
		double knots[] = new double[segments * 2 + 2];
		
		Characteristics c = this.trackObject.getTrack().getPointCharacteristics(this.trackObject.getPosition());
		knots[0] = c.x();
		knots[1] = c.y();
		
		int idx = 2;
		int orientation = this.trackObject.getOrientation();
		
		Track tr = this.trackObject.getTrack();
		double tl = tr.getLength();
		double pos = this.trackObject.getPosition() * tl;
		double delta = (vt.getLength() / (double)segments);
		for(int i = 0; i < segments; i++) {
			if(orientation == 0) {
				pos -= delta;
				if(delta < 0.0) {
					double diff = delta - pos;
					IVertex v = tr.getFirstVertex();
					if(v instanceof Vertex) {
						Vertex vv = (Vertex) v;
						tr = vv.getOppositeTrack(tr);
						tl = tr.getLength();
						if(tr.getFirstVertex() == vv) {
							orientation = 1;
							pos = diff;
						} else {
							orientation = 0;
							pos = tl - diff;
						}
					}
				}
			} else {
				pos += delta;
				if(delta >= tl) {
					double diff = delta - tl;
					IVertex v = tr.getFirstVertex();
					if(v instanceof Vertex) {
						Vertex vv = (Vertex) v;
						tr = vv.getOppositeTrack(tr);
						tl = tr.getLength();
						if(tr.getFirstVertex() == vv) {
							orientation = 1;
							pos = diff;
						} else {
							orientation = 0;
							pos = tl - diff;
						}
					}
				}
			}
			double p = pos / tl;
			c = tr.getPointCharacteristics(p);
			knots[idx++] = c.x();
			knots[idx++] = c.y();
		}
		return knots;
	}
	
	
	
	public static final class VehicleRecord extends VehicleBase implements IRecord<Vehicle, Project> {
		/**
		 * The Id of the vehicle type that describes the attributes of this vehicle.
		 */
		private long vehicleTypeId;
		
		/**
		 * Returns the ID of the vehicle type.
		 * 
		 * @return Vehicle type ID.
		 */
		public long getVehicleTypeId() {
			return this.vehicleTypeId;
		}
		
		/**
		 * Sets the Id of the vehicle type.
		 * 
		 * @param id Vehicle type ID.
		 */
		public void setVehicleTypeId(long id) {
			this.vehicleTypeId = id;
		}
		
		@Override
		public void exportData(Vehicle original, Project domainModel) {
			if(this.vehicleTypeId == IIdentifiable.NEUTRAL_ID) {
				throw new IllegalStateException("Invalid vehicle type ID.");
			}
			if(null == original.getName() && IIdentifiable.NEUTRAL_ID == original.getId()) {
				original.setName(this.getName());
			} else if(!this.getName().equals(original.getName())) {
				original.setNewName(this.getName());
			}
			original.getVehicleType().set(domainModel.getVehicleTypeManager().findById(this.vehicleTypeId));
		}

		@Override
		public void importData(Vehicle original, Project domainModel) {
			this.id = original.getId();
			this.setName(original.getName());
			this.setVehicleTypeId(original.getVehicleType().get().getId());
		}
	}
}
