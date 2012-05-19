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
import javax.validation.Valid;
import org.invenzzia.helium.domain.annotation.RelationshipIndex;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * Simulation-specific vehicle data.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AssignedVehicle implements ISimulationData, Serializable {
	@Valid
	private Vehicle vehicle;
	@Valid
	@RelationshipIndex(entity = Depot.class, inversedBy = "vehicles")
	private Simulation simulation;
	
	public Vehicle getVehicle() {
		return this.vehicle;
	}
	
	public void setVehicle(Vehicle vehicle) {
		if(null != this.vehicle) {
			this.vehicle.unbindAssignedVehicle(this.simulation);
		}
		this.vehicle = vehicle;
		this.vehicle.bindAssignedVehicle(this);
	}

	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}
}
