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

package org.invenzzia.opentrans.lightweight.model.selectors;

import java.util.List;
import org.invenzzia.opentrans.lightweight.model.VisitonsSelectionModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Route;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;

/**
 * Model for displaying the list of routes in the combo box.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RouteSelectionModel extends VisitonsSelectionModel<Route, RouteRecord> {
	@Override
	protected List<Route> getRecordsFromManager(Project project) {
		return project.getRouteManager().getRecords();
	}

	@Override
	protected RouteRecord createNewRecord() {
		return new RouteRecord();
	}

	@Override
	protected void checkCasting(Object suspectedRecord) {
		if(!(suspectedRecord instanceof RouteRecord)) {
			throw new IllegalArgumentException("The selected item must be a record of route.");
		}
	}
}
