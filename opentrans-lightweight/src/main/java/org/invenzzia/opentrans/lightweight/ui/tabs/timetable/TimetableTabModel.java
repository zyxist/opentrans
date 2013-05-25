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

package org.invenzzia.opentrans.lightweight.ui.tabs.timetable;

import java.util.ArrayList;
import java.util.List;
import org.invenzzia.opentrans.lightweight.model.AbstractBatchModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Route;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;
import org.invenzzia.opentrans.visitons.data.manager.RouteManager;

/**
 * The model that provides the data for the timetable tab.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TimetableTabModel extends AbstractBatchModel {
	/**
	 * All the selected lines.
	 */
	private List<RouteRecord> routes;
	
	public List<RouteRecord> getLines() {
		return this.routes;
	}
	
	public void clear() {
		this.routes = null;
	}

	@Override
	protected void collectData(Project project) {
		RouteManager lm = project.getRouteManager();
		List<RouteRecord> records = new ArrayList<>(lm.size());
		for(Route route: lm) {
			RouteRecord record = new RouteRecord();
			record.importData(route, project);
			records.add(record);
		}
		this.routes = records;
	}
}
