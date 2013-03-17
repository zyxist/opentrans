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

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;

/**
 * The model for displaying the list of vehicles.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTableModel extends AbstractTableModel {
	/**
	 * The list of vehicles to display.
	 */
	private List<VehicleRecord> vehicles;
	/**
	 * We need the vehicle type records to display them as well.
	 */
	private Map<Long, VehicleTypeRecord> vehicleTypes;
	
	/**
	 * Updates the table model and notifies the listeners. The method must be called
	 * in the Swing thread.
	 * 
	 * @param vehicles
	 * @param vehicleTypes 
	 */
	public final void updateModel(List<VehicleRecord> vehicles, Map<Long, VehicleTypeRecord> vehicleTypes) {
		this.vehicles = Preconditions.checkNotNull(vehicles);
		this.vehicleTypes = Preconditions.checkNotNull(vehicleTypes);
		this.fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if(null == this.vehicles) {
			return 0;
		}
		return this.vehicles.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int col) {
		if(col == 0) {
			return "Name";
		}
		return "Vehicle type";
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		VehicleRecord record = this.vehicles.get(rowIndex);
		switch(columnIndex) {
			case 0:
				return record.getName();
			case 1:
				return this.vehicleTypes.get(record.getVehicleTypeId()).getName();
		}
		return null;
	}
	
	/**
	 * Returns the record at the specified index. The method throws an exception, if the
	 * index is out of range.
	 * 
	 * @param idx Record index.
	 * @return Record at this index.
	 */
	public VehicleRecord getRecord(int idx) {
		if(null == this.vehicles || idx < 0 || idx >= this.vehicles.size()) {
			throw new IllegalArgumentException("The record index is out of range.");
		}
		return this.vehicles.get(idx);
	}
}
