/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.model.lists;

import org.invenzzia.opentrans.lightweight.model.EntityListModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;
import org.invenzzia.opentrans.visitons.data.manager.VehicleTypeManager;

/**
 * Data model for the item list of vehicle types.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTypeModel extends EntityListModel<VehicleType, VehicleTypeRecord, VehicleTypeManager> {
	@Override
	protected VehicleTypeManager getDataManager(Project project) {
		return project.getVehicleTypeManager();
	}

	@Override
	protected VehicleTypeRecord createRecord() {
		return new VehicleTypeRecord();
	}
}
