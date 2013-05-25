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
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Route;
import org.invenzzia.opentrans.visitons.data.utils.RouteComparator;

/**
 * Manages the list of transportation routes within the project.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RouteManager extends AbstractDataManager<Route> implements IManagerMemento, Iterable<Route> {
	/**
	 * The managing project.
	 */
	private final Project project;
	/**
	 * Allows listing the routes in the proper order.
	 */
	private Set<Route> routes;
	
	public RouteManager(Project project) {
		super();
		this.project = Preconditions.checkNotNull(project);
		this.routes = new TreeSet<>(RouteComparator.get());
	}

	@Override
	public void restoreMemento(Object object) {
		Route route = new Route();
		route.restoreMemento(object, this.project);
		this.addObject(route.getId(), route);
	}

	@Override
	public Iterator<Route> iterator() {
		return this.routes.iterator();
	}
	
	@Override
	protected void afterCreate(Route item) {
		this.routes.add(item);
	}
	
	@Override
	public void updateItem(Route item) throws ModelException {
		this.routes.remove(item);
		this.routes.add(item);
	}
	
	@Override
	protected void afterRemove(Route item) {
		this.routes.remove(item);
	}
}
