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

package org.invenzzia.opentrans.visitons.editing.common;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.data.interfaces.ICRUDManager;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AtomicRemoveCmd<
		T extends IIdentifiable & IMemento<Project>,
		R extends IIdentifiable & IRecord<T, Project>,
		M extends ICRUDManager<T> & IManagerMemento
	>
	implements ICommand
{
	/**
	 * Data for the removed item.
	 */
	private R record;
	/**
	 * Optional memento, if the remove operation has been undone.
	 */
	private Object memento;
	
	/**
	 * Creates a new 'add' command that would add the given record.
	 * 
	 * @param record 
	 */
	public AtomicRemoveCmd(R record) {
		this.record = Preconditions.checkNotNull(record, "The record cannot be empty.");
		Preconditions.checkArgument(record.getId() >= 0, "The record must have the ID greater than or equal 0.");
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		M mgr = this.getManager(project);
		T item = mgr.findById(this.record.getId());
		if(null == item) {
			throw new IllegalStateException("Attempt to execute a command that removes a non-existent object.");
		}
		this.memento = item.getMemento(project);
		mgr.removeItem(item);
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		M mgr = this.getManager(project);
		mgr.restoreMemento(this.memento);
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		try {
			this.execute(project, eventBus);
		} catch(Exception exception) {
			throw new IllegalStateException("Redo operation failed.", exception);
		}
	}

	/**
	 * Specifies, what CRUD manager will perform this command.
	 * 
	 * @param project The project instance.
	 * @return Manager reponsible for handling the operations.
	 */
	protected abstract M getManager(Project project);
}
