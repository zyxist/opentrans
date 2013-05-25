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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.visitons.Project;

/**
 * The common combo box selection model for Visitons objects that reduces
 * the number of boilerplate code. The model downloads the list of records
 * from the Visitons project (this must be done in the model thread), and
 * the provides this data to the combo box. 
 * 
 * @param P Object type
 * @param R Record type
 * @author Tomasz Jędrzejewski
 */
public abstract class VisitonsSelectionModel<P, R extends IRecord<P, Project> & IIdentifiable> implements ComboBoxModel<R> {
	/**
	 * Imported records that are displayed by this model.
	 */
	private List<R> records;
	/**
	 * The currently selected record.
	 */
	private R selectedRecord = null;
	/**
	 * Registered model listeners.
	 */
	private Set<ListDataListener> listeners;

	/**
	 * Initializes an empty model.
	 */
	public VisitonsSelectionModel() {
		this.records = new LinkedList<>();
		this.listeners = new LinkedHashSet<>();
	}
	
	/**
	 * Initial data load.
	 * 
	 * @param project 
	 */
	@InModelThread(asynchronous = false)
	public void loadData(Project project) {
		this.records = new LinkedList<>();
		for(P item: this.getRecordsFromManager(project)) {
			R record = this.createNewRecord();
			record.importData(item, project);
			this.records.add(record);
		}
	}
	
	/**
	 * Updates the combo box data model. The method must be called in the model thread.
	 * 
	 * @param project The project to update the data from.
	 */
	@InModelThread(asynchronous = false)
	public List<R> updateModel(Project project) {
		List<R> newRecords = new LinkedList<>();
		for(P item: this.getRecordsFromManager(project)) {
			R record = this.createNewRecord();
			record.importData(item, project);
			newRecords.add(record);
		}
		return newRecords;
	}
	
	/**
	 * Installs the model contents downloaded from the model thread. The method
	 * must be used in the Swing thread.
	 * 
	 * @param newRecords 
	 */
	@InSwingThread(asynchronous = true)
	public void installModel(List<R> newRecords) {
		this.records = newRecords;
		this.updateSelectedItem();
		final ListDataEvent evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.records.size());
		for(ListDataListener listener: this.listeners) {
			listener.contentsChanged(evt);
		}
	}
	
	@Override
	public void setSelectedItem(Object record) {
		if(null == record) {
			this.selectedRecord = null;
		} else {
			this.checkCasting(record);
			this.selectedRecord = (R) record;
		}
		final ListDataEvent evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.records.size());
		for(ListDataListener listener: this.listeners) {
			listener.contentsChanged(evt);
		}
	}

	@Override
	public Object getSelectedItem() {
		return this.selectedRecord;
	}

	@Override
	public int getSize() {
		return this.records.size();
	}

	@Override
	public R getElementAt(int i) {
		return this.records.get(i);
	}

	@Override
	public void addListDataListener(ListDataListener ll) {
		this.listeners.add(ll);
	}

	@Override
	public void removeListDataListener(ListDataListener ll) {
		this.listeners.remove(ll);
	}
	
	/**
	 * Helps finding a record that matches the given ID, because the records do not
	 * store the references, but just ID-s.
	 * 
	 * @param id Item ID.
	 * @return Record with this ID.
	 */
	public R findById(long id) {
		for(R record: this.records) {
			if(record.getId() == id) {
				return record;
			}
		}
		throw new IllegalStateException("The record with the ID #"+id+" does not exist.");
	}
	
	/**
	 * Returns the list of the original objects to display from the project.
	 * 
	 * @param project The project we are importing from.
	 * @return List of items.
	 */
	protected abstract List<P> getRecordsFromManager(final Project project);
	/**
	 * Returns an empty, new instance of the record.
	 * 
	 * @return New, empty record.
	 */
	protected abstract R createNewRecord();
	/**
	 * Checks casting to the record type in the combo box model methods which do
	 * not support generics. The method may throw {@link IllegalArgumentException},
	 * if the suspected record is not a valid record.
	 * 
	 * @param suspectedRecord The uncasted object to check.
	 */
	protected abstract void checkCasting(Object suspectedRecord);
	
	/**
	 * The method is used, when the contents of the model are changed to determine,
	 * which record shall be selected.
	 */
	private void updateSelectedItem() {
		int size = this.getSize();
		Object selectedItem = this.getSelectedItem();
		if(size > 0 && selectedItem == null) {
			this.setSelectedItem(this.getElementAt(0));
		} else if(size == 0 && selectedItem != null) {
			this.setSelectedItem(null);
		} else {
			boolean found = false;
			for(int i = 0; i < size; i++) {
				R rec = (R) this.getElementAt(i);
				if(rec.getId() == ((R) selectedItem).getId()) {
					this.setSelectedItem(rec);
					found = true;
					break;
				}
			}
			if(!found) {
				this.setSelectedItem(size > 0 ? this.getElementAt(0) : null);
			}
		}
	}
}
