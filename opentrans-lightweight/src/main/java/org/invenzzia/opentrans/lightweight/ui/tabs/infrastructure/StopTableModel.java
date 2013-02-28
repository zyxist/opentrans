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

package org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopTableModel extends AbstractTableModel {
	/**
	 * Here we display the values from.
	 */
	private List<StopRecord> stops;
	
	public StopTableModel(List<StopRecord> allStops) {
		this.stops = allStops;
	}

	@Override
	public int getRowCount() {
		return this.stops.size();
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
		return "# of lines";
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StopRecord record = this.stops.get(rowIndex);
		switch(columnIndex) {
			case 0:
				return record.getName();
			case 1:
				return Integer.valueOf(0);
		}
		return null;
	}

}
