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

package org.invenzzia.opentrans.lightweight.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.visitons.Project;

/**
 * We have several types of models for listing items, and all of them
 * share the same logic. This class makes it common. The entity list model
 * manages the unit of work, which can be persisted, when the changes
 * are approved.
 * 
 * @param E entity type
 * @param R record type for this entity
 * @param M data manager for this type of entities
 * @author Tomasz Jędrzejewski
 */
public abstract class EntityListModel<
		E extends IIdentifiable,
		R extends IRecord<E, Project> & IIdentifiable,
		M extends AbstractDataManager<E>
	>
	implements ListModel<R>
{
	/**
	 * List of the visible records.
	 */
	private List<R> records = new LinkedList<>();
	/**
	 * Optional comparator, if used - the list is ordered.
	 */
	private Comparator<R> comparator;
	/**
	 * The storage for the updated entities.
	 */
	private UnitOfWork<R> unitOfWork;
	/**
	 * Who is notified about model changes?
	 */
	private Set<ListDataListener> listeners;

	public EntityListModel() {
		this.listeners = new LinkedHashSet<>();
		this.unitOfWork = new UnitOfWork<>();
		
		this.comparator = this.getComparator();
	}
	
	/**
	 * Gets the data manager responsible for storing the data within the project.
	 * The method is used by the model data loader.
	 * 
	 * @param project
	 * @return Data manager extracted from the project.
	 */
	protected abstract M getDataManager(Project project);
	/**
	 * We cannot instantiate generic types, so we must help ourselves with such an method.
	 * It is supposed to return a newly created record.
	 * 
	 * @return 
	 */
	protected abstract R createRecord();
	/**
	 * Comparator allows setting the element order. If comparator is NULL, the item list
	 * is unordered.
	 * 
	 * @return 
	 */
	protected Comparator<R> getComparator() {
		return null;
	}
	
	/**
	 * Allows importing the full set of items from the given project... but this must be done
	 * in the model thread.
	 * 
	 * @param project 
	 */
	@InModelThread(asynchronous = true)
	public void loadData(Project project) {
		List<R> records = new LinkedList<>();
		for(E item: this.getDataManager(project).getRecords()) {
			R record = this.createRecord();
			record.importData(item, project);
			records.add(record);
		}
		this.installNewRecordSet(records);
	}
	
	@InSwingThread(asynchronous = true)
	protected void installNewRecordSet(List<R> records) {
		this.records = records;

		this.fireContentChanged();
	}
	
	@InSwingThread(asynchronous = true)
	public void fireContentChanged() {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.records.size());
		for(ListDataListener listener: this.listeners) {
			listener.contentsChanged(event);
		}
	}
	
	@Override
	public int getSize() {
		return this.records.size();
	}

	@Override
	public R getElementAt(int index) {
		return this.records.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		this.listeners.remove(l);
	}
	
	/**
	 * Inserts a new record to the model.
	 * 
	 * @param record
	 */
	public void insertRecord(R record) {
		this.records.add(record);
		this.unitOfWork.insert(record);
		this.rebuildSortedList();
		this.fireContentChanged();
	}
	
	/**
	 * Updates the record in the model.
	 * 
	 * @param record 
	 */
	public void updateRecord(R record) {
		this.unitOfWork.update(record);
		this.rebuildSortedList();
		this.fireContentChanged();
	}
	
	/**
	 * Removes the record from the model.
	 * 
	 * @param record 
	 */
	public void removeRecord(R record) {
		if(null != record) {
			this.records.remove(record);
			this.unitOfWork.remove(record);
			this.rebuildSortedList();
			this.fireContentChanged();
		}
	}
	
	/**
	 * Returns the unit of work created by this model during the modification.
	 * 
	 * @return Produced unit of work. 
	 */
	public UnitOfWork<R> getUnitOfWork() {
		return this.unitOfWork;
	}
	
	private void rebuildSortedList() {
		if(null != this.comparator) {
			Collections.sort(this.records, this.comparator);
		}
	}
}
