/*
 * Copyright (C) 2013 Tomasz Jędrzejewski
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.model.lists;

import java.util.Comparator;
import org.invenzzia.opentrans.lightweight.model.EntityListModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Route;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;
import org.invenzzia.opentrans.visitons.data.manager.RouteManager;
import org.invenzzia.opentrans.visitons.data.utils.RouteRecordComparator;

/**
 * Data model for the item list in the dialog window.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RouteModel extends EntityListModel<Route, RouteRecord, RouteManager> {
	@Override
	protected RouteManager getDataManager(Project project) {
		return project.getRouteManager();
	}

	@Override
	protected RouteRecord createRecord() {
		return new RouteRecord();
	}
	
	@Override
	protected Comparator<RouteRecord> getComparator() {
		return RouteRecordComparator.get();
	}
}
