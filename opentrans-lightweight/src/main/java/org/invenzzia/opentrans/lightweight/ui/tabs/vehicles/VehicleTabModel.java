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

package org.invenzzia.opentrans.lightweight.ui.tabs.vehicles;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.invenzzia.opentrans.lightweight.model.AbstractBatchModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;
import org.invenzzia.opentrans.visitons.data.manager.VehicleManager;

/**
 * The model that provides the data for the vehicle tab.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTabModel extends AbstractBatchModel {
	private List<VehicleRecord> vehicles;
	
	private Map<Long, VehicleTypeRecord> vehicleTypes;
	
	public List<VehicleRecord> getVehicles() {
		return this.vehicles;
	}
	
	public Map<Long, VehicleTypeRecord> getVehicleTypes() {
		return this.vehicleTypes;
	}
	
	public void clear() {
		this.vehicles = null;
		this.vehicleTypes = null;
	}

	@Override
	protected void collectData(Project project) {
		VehicleManager vm = project.getVehicleManager();
		List<VehicleRecord> records = new ArrayList<>(vm.size());
		for(Vehicle vehicle: vm) {
			VehicleRecord record = new VehicleRecord();
			record.importData(vehicle, project);
			records.add(record);
		}
		Map<Long, VehicleTypeRecord> vehicleTypes = new LinkedHashMap<>();
		for(VehicleType vt: project.getVehicleTypeManager().getRecords()) {
			VehicleTypeRecord record = new VehicleTypeRecord();
			record.importData(vt, project);
			vehicleTypes.put(Long.valueOf(record.getId()), record);
		}
		this.vehicles = records;
		this.vehicleTypes = vehicleTypes;
	}
}
