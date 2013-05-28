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

package org.invenzzia.opentrans.lightweight.model.navigator;

import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;

/**
 * Displays the list of all stops in the navigator panel.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopNavigatorModel extends NavigatorModel {
	@Override
	public String getObjectName() {
		return "Stops";
	}

	@Override
	@InModelThread(asynchronous = true)
	public void loadItems(Project project) {
		List<StopRecord> stops = new LinkedList<>();
		
		for(Stop stop: project.getStopManager()) {
			StopRecord record = new StopRecord();
			record.importData(stop, project);
			stops.add(record);
		}
		this.installItems(stops);
	}
}
