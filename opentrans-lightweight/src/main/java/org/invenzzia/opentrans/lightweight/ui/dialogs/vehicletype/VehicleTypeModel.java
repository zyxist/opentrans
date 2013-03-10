/*
 * Copyright (C) 2013 zyxist
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.invenzzia.opentrans.visitons.data.VehicleType.VehicleTypeRecord;

/**
 *
 * @author zyxist
 */
public class VehicleTypeModel implements ListModel<VehicleTypeRecord> {
	/**
	 * List of the visible records.
	 */
	private List<VehicleTypeRecord> records;
	/**
	 * The storage for the updated entities.
	 */
	private UnitOfWork<VehicleTypeRecord> unitOfWork;
	/**
	 * Listeners notified about model changes.
	 */
	private Set<ListDataListener> listeners;
	
	/**
	 * Initializes the internal data structures.
	 */
	public VehicleTypeModel() {
		this.listeners = new LinkedHashSet<>();
		this.unitOfWork = new UnitOfWork<>();
	}

	@InModelThread(asynchronous = false)
	public void loadData(Project project) {
		List<VehicleTypeRecord> records = new ArrayList<>(project.getMeanOfTransportManager().size());
		for(VehicleType mot: project.getVehicleTypeManager().getRecords()) {
			VehicleTypeRecord record = new VehicleTypeRecord();
			record.importData(mot, project);
			records.add(record);
		}
		this.records = records;
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
	public VehicleTypeRecord getElementAt(int index) {
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
	 * Inserts the new record to the model.
	 * 
	 * @param record 
	 */
	public void insertRecord(VehicleTypeRecord record) {
		this.records.add(record);
		this.unitOfWork.insert(record);
		this.fireContentChanged();
	}
	
	/**
	 * Updates the record in the model.
	 * 
	 * @param record 
	 */
	public void updateRecord(VehicleTypeRecord record) {
		this.unitOfWork.update(record);
	}
	
	/**
	 * Removes the record from the model.
	 * 
	 * @param record 
	 */
	public void removeRecord(VehicleTypeRecord record) {
		this.records.remove(record);
		this.unitOfWork.remove(record);
		this.fireContentChanged();
	}
	
	/**
	 * Returns the unit of work created by this model during the modification.
	 * 
	 * @return Produced unit of work. 
	 */
	public UnitOfWork<VehicleTypeRecord> getUnitOfWork() {
		return this.unitOfWork;
	}
}
