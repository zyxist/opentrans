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
import org.invenzzia.opentrans.visitons.data.Route;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;
import org.invenzzia.opentrans.visitons.data.manager.RouteManager;
import org.invenzzia.opentrans.visitons.editing.common.AbstractUnitOfWorkCmd;

/**
 * Atomic history operation that provides a batch update of all
 * routes available within the project.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Update routes")
public class UpdateRoutesCmd extends AbstractUnitOfWorkCmd<Route, RouteRecord, RouteManager> {
	public UpdateRoutesCmd(UnitOfWork<RouteRecord> unitOfWork) {
		super(unitOfWork);
	}
	
	@Override
	protected RouteManager getManager(Project project) {
		return project.getRouteManager();
	}

	@Override
	protected Route createNewDataObject() {
		return new Route();
	}
}
