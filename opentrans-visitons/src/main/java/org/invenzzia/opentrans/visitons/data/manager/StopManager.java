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

package org.invenzzia.opentrans.visitons.data.manager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Stop;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopManager extends AbstractDataManager<Stop> implements IManagerMemento, Iterable<Stop> {
	/**
	 * Allows listing the stops in the alphabetical order.
	 */
	private Set<Stop> stops;
	/**
	 * Set of used stop names, which must be unique.
	 */
	private Set<String> stopNames;
	/**
	 * The managing project.
	 */
	private final Project project;
	
	public StopManager(Project project) {
		super();
		this.project = Preconditions.checkNotNull(project);
		this.stops = new TreeSet<>(Ordering.usingToString());
		this.stopNames = new LinkedHashSet<>();
	}

	@Override
	protected void beforeCreate(Stop item) throws ModelException {
		if(this.stopNames.contains(item.getName())) {
			throw new ModelException("The stop name '"+item+"' is already in use.");
		}
	}
	
	@Override
	protected void afterCreate(Stop item) {
		this.stops.add(item);
		this.stopNames.add(item.getName());
	}
	
	@Override
	public void updateItem(Stop item) throws ModelException {
		if(null != item.getPreviousName() && !item.getPreviousName().equals(item.getName())) {
			this.stops.remove(item);
			if(this.stopNames.contains(item.getName())) {
				throw new ModelException("The stop name '"+item+"' is already in use.");
			}
			this.stopNames.remove(item.getPreviousName());
			this.stopNames.add(item.getName());
			this.stops.add(item);
		}
	}
	
	@Override
	protected void afterRemove(Stop item) {
		this.stops.remove(item);
		this.stopNames.remove(item.getName());
	}
	
	@Override
	public void restoreMemento(Object memento) {
		Stop stop = new Stop();
		stop.restoreMemento(memento, this.project);
		this.addObject(stop.getId(), stop);
		this.stops.add(stop);
		this.stopNames.add(stop.getName());
	}
	
	/**
	 * Returns true, if the given name is already in use.
	 * 
	 * @param name Stop name to test.
	 * @return True, if this name is in use.
	 */
	public boolean nameExists(String name) {
		return this.stopNames.contains(name);
	}
	
	@Override
	public Iterator<Stop> iterator() {
		return this.stops.iterator();
	}
}
