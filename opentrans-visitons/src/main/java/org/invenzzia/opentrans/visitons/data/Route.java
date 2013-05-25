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

package org.invenzzia.opentrans.visitons.data;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.helium.data.utils.MementoUtils;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.types.RouteNumber;



/**
 * Common code shared by both the record and the actual entity.
 * 
 * @author Tomasz Jędrzejewski
 */
class RouteBase implements IIdentifiable {
	/**
	 * Internal unique ID used for recovery.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Number of the route.
	 */
	protected RouteNumber number = RouteNumber.DEFAULT_NUMBER;
	/**
	 * Optional extra description about this route.
	 */
	protected String description;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	public RouteNumber getNumber() {
		return this.number;
	}
	
	public void setNumber(RouteNumber number) {
		this.number = Preconditions.checkNotNull(number);
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		if(null == description) {
			this.description = "";
		} else { 
			this.description = description;
		}
	}
	
	@Override
	public String toString() {
		return this.number.toString();
	}
}

/**
 * Represents a regular transport route operated by vehicles. Each route has
 * an identification number, and a timetable.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class Route extends RouteBase implements IMemento<Project> {

	@Override
	public Object getMemento(Project domainModel) {
		RouteRecord record = new RouteRecord();
		record.importData(this, domainModel);
		return record;
	}

	@Override
	public void restoreMemento(Object memento, Project domainModel) {
		RouteRecord record = MementoUtils.checkMemento(memento, RouteRecord.class, Route.class);
		record.exportData(this, domainModel);
		this.id = record.id;
	}
	
	public final static class RouteRecord extends RouteBase implements IRecord<Route, Project> {
		@Override
		public void exportData(Route original, Project domainModel) {
			original.setNumber(this.getNumber());
			original.setDescription(this.getDescription());
		}

		@Override
		public void importData(Route original, Project domainModel) {
			this.setId(original.getId());
			this.setNumber(original.getNumber());
			this.setDescription(original.getDescription());
		}
	}
}
