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
import org.invenzzia.helium.data.Relation;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;

/**
 * Base class shared by the actual data object and the record.
 * 
 * @author Tomasz Jędrzejewski
 */
class VehicleTypeBase implements IIdentifiable {
	/**
	 * Unique internal ID of this vehicle type.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Unique name of the vehicle type.
	 */
	private String name;
	/**
	 * Vehicle type mass, without passengers, in KG.
	 */
	private int mass;
	/**
	 * Vehicle length in metres.
	 */
	private double length;
	/**
	 * Maximum capacity - the number of passengers.
	 */
	private int maximumCapacity;
	/**
	 * Number of segments this vehicle is divided to (or single cars, if this is a train).
	 */
	private int numberOfSegments;
	/**
	 * Engine power in newtons.
	 */
	private int enginePower;
	/**
	 * Number of passengers per minute, that can leave or enter the vehicle at the stop.
	 */
	private int passengerExchangeRatio;

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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMass() {
		return this.mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public double getLength() {
		return this.length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public int getMaximumCapacity() {
		return this.maximumCapacity;
	}

	public void setMaximumCapacity(int maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}

	public int getNumberOfSegments() {
		return this.numberOfSegments;
	}

	public void setNumberOfSegments(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments;
	}

	public int getEnginePower() {
		return this.enginePower;
	}

	public void setEnginePower(int enginePower) {
		this.enginePower = enginePower;
	}

	public int getPassengerExchangeRatio() {
		return this.passengerExchangeRatio;
	}

	public void setPassengerExchangeRatio(int passengerExchangeRatio) {
		this.passengerExchangeRatio = passengerExchangeRatio;
	}

	@Override
	public String toString() {
		return this.name;
	}
}

/**
 * Vehicle type represents a group of vehicles that share the same technical
 * characteristics and can be considered instances of the same model.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class VehicleType extends VehicleTypeBase implements IMemento<Project> {
	/**
	 * Mean of transport represented by this type.
	 */
	private final Parent<MeanOfTransport> meanOfTransport = new Parent<>();
	/**
	 * Vehicles that belong to this class.
	 */
	private Relation<Vehicle> vehicles = new Relation<>();

	/**
	 * Returns the object for storing the information about a parent mean of transport.
	 * 
	 * @return Parent mean of transport holder.
	 */
	public Parent<MeanOfTransport> getMeanOfTransport() {
		return this.meanOfTransport;
	}
	
	/**
	 * Returns the vehicles that belong to this vehicle type.
	 * 
	 * @return Vehicle relation. 
	 */
	public Relation<Vehicle> getVehicles() {
		return this.vehicles;
	}

	@Override
	public Object getMemento(Project project) {
		VehicleTypeRecord memento = new VehicleTypeRecord();
		memento.importData(this, project);
		return memento;
	}

	@Override
	public void restoreMemento(Object memento, Project project) {
		Preconditions.checkNotNull(memento, "Attempt to restore an empty memento.");
		if(!(memento instanceof VehicleTypeRecord)) {
			throw new IllegalArgumentException("Invalid memento for VehicleType class: "+memento.getClass().getCanonicalName());
		}
		VehicleTypeRecord record = (VehicleTypeRecord) memento;
		record.exportData(this, project);
		this.id = record.id;
	}

	public final static class VehicleTypeRecord extends VehicleTypeBase implements IRecord<VehicleType, Project> {
		/**
		 * Mean of transport this vehicle type belongs to.
		 */
		private long meanOfTransportId;
		/**
		 * Notifies that there are vehicles assigned to this vehicle type.
		 */
		private boolean hasVehicles;
		
		/**
		 * Returns the mean of transport this vehicle type is assigned to.
		 * 
		 * @return 
		 */
		public long getMeanOfTransportId() {
			return this.meanOfTransportId;
		}
		
		/**
		 * Sets the new mean of transport assignment.
		 * 
		 * @param id The ID of the mean of transport.
		 */
		public void setMeanOfTransport(long id) {
			this.meanOfTransportId = id;
		}
		
		/**
		 * Returns true, if there are vehicles assigned to this vehicle type.
		 * @return Whether there are vehicles assigned.
		 */
		public boolean hasVehicles() {
			return this.hasVehicles;
		}
		
		@Override
		public void exportData(VehicleType original, Project project) {
			if(this.meanOfTransportId == IIdentifiable.NEUTRAL_ID) {
				throw new IllegalStateException("Invalid mean of transport");
			}
			original.setName(this.getName());
			original.setLength(this.getLength());
			original.setMass(this.getMass());
			original.setEnginePower(this.getEnginePower());
			original.setMaximumCapacity(this.getMaximumCapacity());
			original.setNumberOfSegments(this.getNumberOfSegments());
			original.setPassengerExchangeRatio(this.getPassengerExchangeRatio());
			original.getMeanOfTransport().set(project.getMeanOfTransportManager().findById(this.meanOfTransportId));
		}

		@Override
		public void importData(VehicleType original, Project project) {
			this.setId(original.getId());
			this.setName(original.getName());
			this.setLength(original.getLength());
			this.setMass(original.getMass());
			this.setEnginePower(original.getEnginePower());
			this.setMaximumCapacity(original.getMaximumCapacity());
			this.setNumberOfSegments(original.getNumberOfSegments());
			this.setPassengerExchangeRatio(original.getPassengerExchangeRatio());
			this.meanOfTransportId = original.getMeanOfTransport().get().getId();
			this.hasVehicles = !original.getVehicles().isEmpty();
		}
	}
}
