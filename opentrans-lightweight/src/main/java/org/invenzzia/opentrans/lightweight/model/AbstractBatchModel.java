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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.model.visitons.VisitonsSelectionModel;
import org.invenzzia.opentrans.visitons.Project;

/**
 * If we do not want to keep the model retrieval code in our tab/window controllers,
 * we can use batch model, which updates the data from the model thread in one shot,
 * and sends an asynchronous notification back to the controller, when the data are
 * ready for use.
 * 
 * <p>The batch model relies on the interception to ensure the execution in the proper
 * thread, which means that the subclass instances must be created by the dependency
 * injection container.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractBatchModel {
	@Inject
	private IProjectHolder projectHolder;
	/**
	 * List of listeners that shall be notified about the data
	 * availability.
	 */
	private Set<IBatchModelListener> listeners = new LinkedHashSet<>();
	/**
	 * The batch model can automatically update the selection models.
	 */
	private Set<VisitonsSelectionModel> selectionModels = new LinkedHashSet<>();
	
	public void addSelectionModel(VisitonsSelectionModel<?, ?> selectionModel) {
		this.selectionModels.add(selectionModel);
	}
	
	public void removeSelectionModel(VisitonsSelectionModel<?, ?> selectionModel) {
		this.selectionModels.remove(selectionModel);
	}
	
	public void removeSelectionModels() {
		this.selectionModels.clear();
	}	
	
	/**
	 * Adds a new batch model listener.
	 * 
	 * @param listener New listener.
	 */
	public void addBatchModelListener(IBatchModelListener listener) {
		this.listeners.add(Preconditions.checkNotNull(listener));
	}
	
	/**
	 * Removes the existing batch model listener.
	 * 
	 * @param listener 
	 */
	public void removeBatchModelListener(IBatchModelListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Removes all batch model listeners registered in this model.
	 */
	public void removeBatchModelListeners() {
		this.listeners.clear();
	}

	/**
	 * Asynchronously downloads the data from the model thread, and then
	 * notifies the Swing thread listeners, when the data become available.
	 * The method is guaranteed to be executed in the model thread in the
	 * asynchronous mode.
	 */
	@InModelThread(asynchronous = true)
	public void updateData() {
		Project project = this.projectHolder.getCurrentProject();
		this.collectData(project);
		
		Map<VisitonsSelectionModel, List> downloadedRecords = new LinkedHashMap<>();
		for(VisitonsSelectionModel vsm: this.selectionModels) {
			downloadedRecords.put(vsm, vsm.updateModel(project));
		}
		this.notifyListeners(downloadedRecords);
	}
	
	/**
	 * Notifies all the listeners about the data availability.
	 */
	@InSwingThread(asynchronous = true)
	protected void notifyListeners(Map<VisitonsSelectionModel, List> selectionModelData) {
		for(Map.Entry<VisitonsSelectionModel, List> entry: selectionModelData.entrySet()) {
			entry.getKey().installModel(entry.getValue());
		}
		for(IBatchModelListener listener: this.listeners) {
			listener.modelDataAvailable(this);
		}
	}
	
	/**
	 * Overwrite this method to provide custom data download code.
	 */
	protected abstract void collectData(final Project project);
}
