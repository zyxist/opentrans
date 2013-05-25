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
import org.invenzzia.opentrans.visitons.data.Line;
import org.invenzzia.opentrans.visitons.data.utils.LineComparator;

/**
 * Manages the list of transportation lines within the project.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LineManager extends AbstractDataManager<Line> implements IManagerMemento, Iterable<Line> {
	/**
	 * The managing project.
	 */
	private final Project project;
	/**
	 * Allows listing the lines in the proper order.
	 */
	private Set<Line> lines;
	
	public LineManager(Project project) {
		super();
		this.project = Preconditions.checkNotNull(project);
		this.lines = new TreeSet<>(LineComparator.get());
	}

	@Override
	public void restoreMemento(Object object) {
		Line line = new Line();
		line.restoreMemento(object, this.project);
		this.addObject(line.getId(), line);
	}

	@Override
	public Iterator<Line> iterator() {
		return this.lines.iterator();
	}
	
	@Override
	protected void afterCreate(Line item) {
		this.lines.add(item);
	}
	
	@Override
	public void updateItem(Line item) throws ModelException {
		this.lines.remove(item);
		this.lines.add(item);
	}
	
	@Override
	protected void afterRemove(Line item) {
		this.lines.remove(item);
	}
}
