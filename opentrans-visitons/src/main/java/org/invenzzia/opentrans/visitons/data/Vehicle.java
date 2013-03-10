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

import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;


/**
 * Common data of a vehicle.
 */
class VehicleBase implements IIdentifiable {
	/**
	 * Unique, internal, non-modifiable vehicle ID.
	 */
	protected long id;
	/**
	 * Name of this vehicle.
	 */
	private String name;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		if(-1 != this.id) {
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
public class Vehicle extends VehicleBase implements IMemento<Project> {
	@Override
	public Object getMemento(Project domainModel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void restoreMemento(Object object, Project domainModel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public static final class VehicleRecord extends VehicleBase implements IRecord<Vehicle, Project> {
		@Override
		public void exportData(Vehicle original, Project domainModel) {
			original.setName(this.getName());
		}

		@Override
		public void importData(Vehicle original, Project domainModel) {
			this.setId(original.getId());
			this.setName(original.getName());
		}
	}
}
