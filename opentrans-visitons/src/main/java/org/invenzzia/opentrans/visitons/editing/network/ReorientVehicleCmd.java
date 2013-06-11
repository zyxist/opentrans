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

package org.invenzzia.opentrans.visitons.editing.network;

import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.editing.common.AbstractReorientObjectCmd;

/**
 * Allows changing the orientation of vehicle.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ReorientVehicleCmd extends AbstractReorientObjectCmd<VehicleRecord, Vehicle> {
	public ReorientVehicleCmd(VehicleRecord record) {
		super(record, "Change vehicle orientation");
	}

	@Override
	protected byte advanceOrientation(byte oldOrientation) {
		return (byte)((oldOrientation + 1) % 2);
	}

	@Override
	protected Vehicle getEntity(Project project) {
		return project.getVehicleManager().findById(this.record.getId());
	}
}
