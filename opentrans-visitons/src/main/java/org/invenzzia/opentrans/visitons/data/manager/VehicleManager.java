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

package org.invenzzia.opentrans.visitons.data.manager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;

/**
 * Keeps the registry of all the vehicles in the project and provides a data
 * access for them. The vehicles must have unique names, and the vehicle manager
 * verifies this constraint.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleManager extends AbstractDataManager<Vehicle> implements IManagerMemento, Iterable<Vehicle> {
	/**
	 * The managing project.
	 */
	private final Project project;
	/**
	 * Allows listing the vehicles in the proper order.
	 */
	private Set<Vehicle> vehicles;
	/**
	 * For discovering the uniqueness of the vehicle names.
	 */
	private Set<String> vehicleNames;
	
	public VehicleManager(final Project project) {
		this.project = Preconditions.checkNotNull(project);
		this.vehicles = new TreeSet<>(Ordering.usingToString());
		this.vehicleNames = new LinkedHashSet<>();
	}
	
	@Override
	protected void beforeCreate(Vehicle item) throws ModelException {
		if(this.vehicleNames.contains(item.getName())) {
			throw new ModelException("The vehicle name '"+item+"' is already in use.");
		}
	}
	
	@Override
	protected void afterCreate(Vehicle item) {
		this.vehicleNames.add(item.getName());
		this.vehicles.add(item);
	}
	
	@Override
	public void updateItem(Vehicle item) throws ModelException {
		if(null != item.getNewName()) {
			if(this.vehicleNames.contains(item.getNewName())) {
				item.rejectName();
				throw new ModelException("The vehicle name '"+item+"' is already in use.");
			}
			this.vehicles.remove(item);
			this.vehicleNames.remove(item.getName());
			item.applyName();
			this.vehicles.add(item);
			this.vehicleNames.add(item.getName());
		}
	}
	
	@Override
	protected void afterRemove(Vehicle item) {
		this.vehicles.remove(item);
		this.vehicleNames.remove(item.getName());
	}
	
	/**
	 * Finds the vehicle by its name.
	 * 
	 * @param name Vehicle name.
	 * @return Vehicle.
	 */
	public Vehicle findByName(String name) {
		for(Vehicle vehicle: this.vehicles) {
			if(vehicle.getName().equals(name)) {
				return vehicle;
			}
		}
		return null;
	}
	
	/**
	 * Finds the first vehicle by its name part.
	 * 
	 * @param namePart Part of the name we are looking for.
	 * @return The first matched vehicle.
	 */
	public Vehicle findByNamePart(String namePart) {
		for(Vehicle vehicle: this.vehicles) {
			if(vehicle.getName().contains(namePart)) {
				return vehicle;
			}
		}
		return null;
	}
	
	/**
	 * Returns true, if the specified vehicle name is already in use.
	 * 
	 * @param name The name to verify.
	 * @return True, if the vehicle with such name exists.
	 */
	public boolean nameExists(String name) {
		return this.vehicleNames.contains(name);
	}
	
	@Override
	public void restoreMemento(Object memento) {
		Vehicle vehicle = new Vehicle();
		vehicle.restoreMemento(memento, this.project);
		this.addObject(vehicle.getId(), vehicle);
		this.vehicles.add(vehicle);
		this.vehicleNames.add(vehicle.getName());
	}

	@Override
	public Iterator<Vehicle> iterator() {
		return this.vehicles.iterator();
	}
}
