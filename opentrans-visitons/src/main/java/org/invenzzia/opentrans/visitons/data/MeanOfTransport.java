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
import org.invenzzia.helium.data.Relation;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;

class MeanOfTransportBase implements IIdentifiable {
	/**
	 * Internal unique ID used for recovery.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Mean of transport name.
	 */
	private String name;
	/**
	 * Coefficient used for calculations of the friction force in the simulation.
	 * It is specific to the ground material (i.e. road for buses, track for trams).
	 */
	private double rollingFrictionCoefficient;
	/**
	 * Controls the maximum safe speed on curves, linking them with curve radius.
	 */
	private double maxSafeSpeedRadiusCoefficient;
	/**
	 * If set to true, vehicles can overtake each other on a track.
	 */
	private boolean overtakingAllowed;	
	/**
	 * Punishment given to the speed, when one vehicle tries to overtake another. Value
	 * 1.0 means no punishment, 0.9 means speed reduced by 10%.
	 */
	private double overtakingPunishment;
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the previously set ID.");
		}
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRollingFrictionCoefficient() {
		return rollingFrictionCoefficient;
	}

	public void setRollingFrictionCoefficient(double rollingFrictionCoefficient) {
		this.rollingFrictionCoefficient = rollingFrictionCoefficient;
	}

	public double getMaxSafeSpeedRadiusCoefficient() {
		return maxSafeSpeedRadiusCoefficient;
	}

	public void setMaxSafeSpeedRadiusCoefficient(double maxSafeSpeedRadiusCoefficient) {
		this.maxSafeSpeedRadiusCoefficient = maxSafeSpeedRadiusCoefficient;
	}

	public double getOvertakingPunishment() {
		return overtakingPunishment;
	}

	public void setOvertakingPunishment(double overtakingPunishment) {
		this.overtakingPunishment = overtakingPunishment;
	}
	
	public boolean isOvertakingAllowed() {
		return this.overtakingAllowed;
	}
	
	public void setOvertakingAllowed(boolean value) {
		this.overtakingAllowed = value;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

/**
 * Represents a mean of transport available in the simulation. It defines
 * the common properties for all vehicle types from the same mean of transport
 * group (i.e. all buses, all trams) etc.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class MeanOfTransport extends MeanOfTransportBase implements IMemento<Project> {

	/**
	 * All vehicle types assigned to this mean of transport.
	 */
	private final Relation<VehicleType> vehicleTypes = new Relation<>();
	
	/**
	 * Returns the relation manager that provides access to all vehicle types
	 * attached to this mean of transport.
	 * 
	 * @return All vehicle types attached to this mean of transport.
	 */
	public Relation<VehicleType> getVehicleTypes() {
		return this.vehicleTypes;
	}

	@Override
	public Object getMemento(Project project) {
		MeanOfTransportRecord record = new MeanOfTransportRecord();
		record.importData(this, project);
		return record;
	}

	@Override
	public void restoreMemento(Object memento, Project project) {
		Preconditions.checkNotNull(memento, "The memento is NULL!");
		if(!(memento instanceof MeanOfTransportRecord)) {
			throw new IllegalArgumentException("Invalid memento for MeanOfTransport class: "+memento.getClass().getCanonicalName());
		}
		MeanOfTransportRecord record = (MeanOfTransportRecord) memento;
		record.exportData(this, project);
		this.id = record.id;
	}

	/**
	 * For carrying the data.
	 */
	public final static class MeanOfTransportRecord extends MeanOfTransportBase implements IRecord<MeanOfTransport, Project> {
		private boolean hasVehicleTypes;
		
		@Override
		public void importData(MeanOfTransport mot, Project project) {
			this.setId(mot.getId());
			this.setName(mot.getName());
			this.setRollingFrictionCoefficient(mot.getRollingFrictionCoefficient());
			this.setMaxSafeSpeedRadiusCoefficient(mot.getMaxSafeSpeedRadiusCoefficient());
			this.setOvertakingPunishment(mot.getOvertakingPunishment());
			this.setOvertakingAllowed(mot.isOvertakingAllowed());
			this.hasVehicleTypes = (!mot.getVehicleTypes().isEmpty());
		}

		@Override
		public void exportData(MeanOfTransport mot, Project project) {
			mot.setName(this.getName());
			mot.setRollingFrictionCoefficient(this.getRollingFrictionCoefficient());
			mot.setMaxSafeSpeedRadiusCoefficient(this.getMaxSafeSpeedRadiusCoefficient());
			mot.setOvertakingPunishment(this.getOvertakingPunishment());
			mot.setOvertakingAllowed(this.isOvertakingAllowed());
		}
	
		/**
		 * Returns true, if there are vehicle types assigned to this mean of transport.
		 * 
		 * @return 
		 */
		public boolean hasVehicleTypes() {
			return this.hasVehicleTypes;
		}
	}
}

