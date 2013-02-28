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

package org.invenzzia.opentrans.visitons.editing.operations;

import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;
import org.invenzzia.opentrans.visitons.data.manager.VehicleTypeManager;
import org.invenzzia.opentrans.visitons.editing.common.AbstractUnitOfWorkCmd;

/**
 * Atomic history operation that provides a batch update of all vehicle types
 * available within the project. Note that the remove can be safely
 * performed only on vehicle types that do not have vehicles assigned.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Update vehicle types")
public class UpdateVehicleTypesCmd extends AbstractUnitOfWorkCmd<VehicleType, VehicleTypeRecord, VehicleTypeManager> {
	/**
	 * Creates a new command that would replay the contents of the specified unit of work.
	 * 
	 * @param unitOfWork 
	 */
	public UpdateVehicleTypesCmd(UnitOfWork<VehicleTypeRecord> unitOfWork) {
		super(unitOfWork);
	}

	@Override
	protected VehicleTypeManager getManager(Project project) {
		return project.getVehicleTypeManager();
	}

	@Override
	protected VehicleType createNewDataObject() {
		return new VehicleType();
	}
}
