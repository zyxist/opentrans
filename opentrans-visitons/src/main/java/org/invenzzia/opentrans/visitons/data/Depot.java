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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.invenzzia.helium.domain.annotation.Identifier;
import org.invenzzia.helium.domain.relation.RelationshipPerspective;

/**
 * Represents a vehicle depot. While the depots are a part of the infrastructure and thus
 * are shared among the simulation, they might have different vehicle lists for different
 * simulations.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Depot {
	@Min(value = 0)
	@Identifier
	private int id;
	@NotNull
	@Size(min = 2, max = 30)
	private String name;
	
	private RelationshipPerspective<Depot, Vehicle> vehicles;

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Injector for the relationship perspective. Do not use explicitely.
	 * @param perspective 
	 */
	public void setVehiclesPerspective(RelationshipPerspective perspective) {
		this.vehicles = (RelationshipPerspective<Depot, Vehicle>) perspective;
	}
	
	public RelationshipPerspective<Depot, Vehicle> getVehicles() {
		return this.vehicles;
	}
}
