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

package org.invenzzia.opentrans.visitons.events;

import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;

/**
 * The event is sent, when the vehicle is removed. This allows the GUI
 * to make some extra actions then.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleRemovedEvent {
	private final long id;
	
	public VehicleRemovedEvent(Vehicle vehicle) {
		this.id = vehicle.getId();
	}

	public long getId() {
		return this.id;
	}
	
	public boolean matches(VehicleRecord record) {
		return this.id == record.getId();
	}
}