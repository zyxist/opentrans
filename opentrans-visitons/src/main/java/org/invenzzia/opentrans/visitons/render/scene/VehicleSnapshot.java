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

package org.invenzzia.opentrans.visitons.render.scene;

import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.visitons.data.Vehicle;

/**
 * For drawing vehicles. They are distinct from other track objects, because they consist
 * of several knots, and thus, they cannot be drawn on editable track records which may be
 * incomplete.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleSnapshot {
	private List<RenderableVehicle> vehicles = new LinkedList<>();
	
	public void addVehicle(Vehicle vehicle) {
		this.vehicles.add(new RenderableVehicle(vehicle.getId(), vehicle.getName(), vehicle.computeKnots()));
	}
	
	public List<RenderableVehicle> getVehicles() {
		return this.vehicles;
	}
	
	public static class RenderableVehicle {
		public final long id;
		public final String name;
		public final double knots[];
		
		public RenderableVehicle(long id, String name, double knots[]) {
			this.id = id;
			this.name = name;
			this.knots = knots;
		}
	}
}

