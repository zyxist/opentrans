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

import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;

class StopBase implements IIdentifiable {
	/**
	 * Unique internal stop ID.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Unique stop name.
	 */
	private String name;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the previously set ID.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the name of the stop.
	 * 
	 * @return Stop name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the new stop name.
	 * 
	 * @param name Stop name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

public final class Stop extends StopBase implements IMemento<Project> {
	/**
	 * Previous stop name, for the purpose of updating the index.
	 */
	private String previousName;
	
	@Override
	public void setName(String name) {
		this.previousName = name;
		super.setName(name);
	}
	
	/**
	 * Returns the previous stop name.
	 * 
	 * @return Previous stop name.
	 */
	public String getPreviousName() {
		return this.previousName;
	}

	@Override
	public Object getMemento(Project domainModel) {
		StopRecord memento = new StopRecord();
		memento.importData(this, domainModel);
		return memento;
	}

	@Override
	public void restoreMemento(Object memento, Project domainModel) {
		if(!(memento instanceof StopRecord)) {
			throw new IllegalArgumentException("Invalid memento for Stop class: "+memento.getClass().getCanonicalName());
		}
		StopRecord record = (StopRecord) memento;
		record.exportData(this, domainModel);
		this.id = record.getId();
	}
	
	public final static class StopRecord extends StopBase implements IRecord<Stop, Project> {
		@Override
		public void exportData(Stop original, Project domainModel) {
			original.setName(this.getName());
		}

		@Override
		public void importData(Stop original, Project domainModel) {
			this.setId(original.getId());
			this.setName(original.getName());
		}
	}
}