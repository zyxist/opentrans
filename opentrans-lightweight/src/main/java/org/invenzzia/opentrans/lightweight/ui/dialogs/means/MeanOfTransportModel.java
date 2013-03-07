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

package org.invenzzia.opentrans.lightweight.ui.dialogs.means;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MeanOfTransportModel implements ListModel<MeanOfTransportRecord> {
	/**
	 * List of the visible records.
	 */
	private List<MeanOfTransportRecord> records;
	/**
	 * The storage for the updated entities.
	 */
	private UnitOfWork<MeanOfTransportRecord> unitOfWork;
	/**
	 * Listeners notified about model changes.
	 */
	private Set<ListDataListener> listeners;
	
	/**
	 * Initializes the internal data structures.
	 */
	public MeanOfTransportModel() {
		this.listeners = new LinkedHashSet<>();
		this.unitOfWork = new UnitOfWork<>();
	}

	@InModelThread(asynchronous = false)
	public void loadData(Project project) {
		List<MeanOfTransportRecord> records = new ArrayList<>(project.getMeanOfTransportManager().size());
		for(MeanOfTransport mot: project.getMeanOfTransportManager().getRecords()) {
			MeanOfTransport.MeanOfTransportRecord record = new MeanOfTransport.MeanOfTransportRecord();
			record.importData(mot);
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
	public MeanOfTransportRecord getElementAt(int index) {
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
	public void insertRecord(MeanOfTransportRecord record) {
		this.records.add(record);
		this.unitOfWork.insert(record);
		this.fireContentChanged();
	}
}
