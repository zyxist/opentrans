/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.invenzzia.helium.domain.annotation.Identifier;
import org.invenzzia.opentrans.visitons.Simulation;
import org.invenzzia.opentrans.visitons.exception.NoSimulationDataException;

/**
 * A single vehicle. Most of the parameters of vehicle is defined by its 
 * "class" (note that this term does not mean a Java class, but simply
 * a real-world type of the vehicle). Here, we have very little to do.
 *
 * @author Tomasz Jędrzejewski
 */
public class Vehicle implements Serializable {
	@Min(value = 0)
	@Identifier
	private int id;
	@NotNull
	@Size(min = 1, max = 30)
	private String name;
	/**
	 * Simulation-specific vehicle data references are kept in this map.
	 */
	private Map<Simulation, AssignedVehicle> simulationData = new LinkedHashMap<>();

	public Vehicle() {
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int iterator) {
		this.id = iterator;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	void bindAssignedVehicle(AssignedVehicle asv) {
		this.simulationData.put(asv.getSimulation(), asv);
	}
	
	void unbindAssignedVehicle(AssignedVehicle asv) {
		this.simulationData.remove(asv.getSimulation());
	}

	void unbindAssignedVehicle(Simulation simulation) {
		this.simulationData.remove(simulation);
	}
	
	/**
	 * Returs the simulation-specific vehicle data for the given simulation. If there is no
	 * entry for the given simulation, an exception is thrown.
	 * 
	 * @param simulation The probed simulation.
	 * @return Simulation-specific vehicle data.
	 * @throws NoSimulationDataException 
	 */
	public AssignedVehicle getSimulationData(Simulation simulation) throws NoSimulationDataException {
		AssignedVehicle vehicle = this.simulationData.get(simulation);
		if(null == vehicle) {
			throw new NoSimulationDataException(String.format("The vehicle is not assigned to the simulation '%s'.", simulation.getName()), simulation);
		}
		return vehicle;
	}
}
