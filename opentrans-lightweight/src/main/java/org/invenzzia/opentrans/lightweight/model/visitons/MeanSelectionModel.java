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
package org.invenzzia.opentrans.lightweight.model.visitons;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;

/**
 * Handles the combo boxes that allow selecting the mean of transport.
 * 
 * @author zyxist
 */
public class MeanSelectionModel implements ComboBoxModel<MeanOfTransportRecord> {
	/**
	 * Imported records that are displayed by this model.
	 */
	private List<MeanOfTransportRecord> records;
	/**
	 * The currently selected record.
	 */
	private MeanOfTransportRecord selectedRecord = null;
	/**
	 * Registered model listeners.
	 */
	private Set<ListDataListener> listeners;
	
	/**
	 * Creates the mean of transport selection model. This constructor
	 * shall be used in the model thread in order to retrieve the means
	 * of transport. Then it should be passed to the GUI thread.
	 * 
	 * @param project 
	 */
	public MeanSelectionModel(Project project) {
		this.records = new LinkedList<>();
		this.listeners = new LinkedHashSet<>();
		for(MeanOfTransport mot: project.getMeanOfTransportManager().getRecords()) {
			MeanOfTransportRecord record = new MeanOfTransportRecord();
			record.importData(mot, project);
			this.records.add(record);
		}
	}
	
	@Override
	public void setSelectedItem(Object record) {
		if(!(record instanceof MeanOfTransportRecord)) {
			throw new IllegalArgumentException("The selected item must be a record of mean of transport.");
		}
		this.selectedRecord = (MeanOfTransportRecord) record;
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
	public MeanOfTransportRecord getElementAt(int i) {
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
	 * Helps finding a mean of transport record that matches the given ID,
	 * because the records do not store the references, but just ID-s.
	 * 
	 * @param id Mean of transport ID.
	 * @return Record with this ID.
	 */
	public MeanOfTransportRecord findById(long id) {
		for(MeanOfTransportRecord record: this.records) {
			if(record.getId() == id) {
				return record;
			}
		}
		throw new IllegalStateException("The mean of transport with the ID #"+id+" does not exist.");
	}
}
