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
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.helium.data.interfaces.*;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractUnitOfWorkCmd <
		T extends IIdentifiable & IMemento<Project>,
		R extends IIdentifiable & IRecord<T, Project>,
		M extends ICRUDManager<T> & IManagerMemento
	>
	implements ICommand
{
	/**
	 * List of all changes.
	 */
	private final UnitOfWork<R> unitOfWork;
	/**
	 * The mementos of the old object state.
	 */
	private Map<Object, Object> mementos = null;
	
	/**
	 * Creates the new command that replays the unit of work on some manager.
	 * 
	 * @param unitOfWork 
	 */
	public AbstractUnitOfWorkCmd(UnitOfWork<R> unitOfWork) {
		this.unitOfWork = Preconditions.checkNotNull(unitOfWork);
	}
	
	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		M mgr = this.getManager(project);
		this.mementos = new LinkedHashMap<>();
		
		for(R record: this.unitOfWork.getInsertedRecords()) {
			T item = this.createNewDataObject();
			record.exportData(item, project);
			mgr.addItem(item);
			record.setId(item.getId());
		}
		for(R record: this.unitOfWork.getUpdatedRecords()) {
			T item = mgr.findById(record.getId());
			this.mementos.put(record, item.getMemento(project));
			record.exportData(item, project);
			mgr.updateItem(item);
		}
		for(R record: this.unitOfWork.getRemovedRecords()) {
			T item = mgr.findById(record.getId());
			this.mementos.put(record, item.getMemento(project));
			mgr.removeItem(item);
		}
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		try {
			M mgr = this.getManager(project);
			for(R record: this.unitOfWork.getInsertedRecords()) {
				T item = mgr.findById(record.getId());
				mgr.removeItem(record.getId());
				this.mementos.put(record, item.getMemento(project));
			}
			for(R record: this.unitOfWork.getUpdatedRecords()) {
				T item = mgr.findById(record.getId());
				Object memento = this.mementos.get(record);
				item.restoreMemento(memento, project);
				mgr.updateItem(item);
				this.mementos.remove(record);
			}
			for(R record: this.unitOfWork.getRemovedRecords()) {
				Object memento = this.mementos.get(record);
				mgr.restoreMemento(memento);
				this.mementos.remove(record);
			}
		} catch(ModelException exception) {
			throw new IllegalStateException("The undo operation failed.", exception);
		}
	}
	
	@Override
	public void redo(Project project, EventBus eventBus) {
		try {
			M mgr = this.getManager(project);
			for(R record: this.unitOfWork.getInsertedRecords()) {
				Object memento = this.mementos.get(record);
				mgr.restoreMemento(memento);
				this.mementos.remove(record);
			}
			for(R record: this.unitOfWork.getUpdatedRecords()) {
				T item = mgr.findById(record.getId());
				this.mementos.put(record, item.getMemento(project));
				record.exportData(item, project);
				mgr.updateItem(item);
			}
			for(R record: this.unitOfWork.getRemovedRecords()) {
				T item = mgr.findById(record.getId());
				this.mementos.put(record, item.getMemento(project));
				mgr.removeItem(item);
			}
		} catch(ModelException exception) {
			throw new IllegalStateException("The undo operation failed.", exception);
		}
	}
	
	/**
	 * Specifies, what CRUD manager will perform this command.
	 * 
	 * @param project The project instance.
	 * @return Manager reponsible for handling the operations.
	 */
	protected abstract M getManager(Project project);
	
	/**
	 * Specifies, how to create a new data object.
	 * 
	 * @return New, fresh data object.
	 */
	protected abstract T createNewDataObject();
}
