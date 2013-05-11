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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

import java.util.LinkedHashSet;
import java.util.Set;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selection mode allows selecting group of objects and moving them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(SelectionMode.class);
	
	private static final String DEFAULT_STATUS = "Select track and vertices by clicking on them.";
	/**
	 * We keep selected vertices separately to distinguish them from vertices imported due
	 * to the transformation requirements.
	 */
	private Set<VertexRecord> selectedVertices;
	/**
	 * We keep the selected tracks separately to distinguish them from tracks imported
	 * due to the transformation requirements.
	 */
	private Set<TrackRecord> selectedTracks;
	
	public SelectionMode() {
		this.selectedVertices = new LinkedHashSet<>();
		this.selectedTracks = new LinkedHashSet<>();
	}
	
	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.debug("SelectionMode enabled.");
		this.api = api;
		this.api.setStatusMessage(DEFAULT_STATUS);
	}

	@Override
	public void modeDisabled() {
		logger.debug("SelectionMode disabled.");
		this.selectedVertices.clear();
	}
	
	@InModelThread(asynchronous = false)
	public VertexRecord importVertex(long vertexId) {
		return this.currentUnit.importVertex(this.getWorld(), vertexId);
	}
	
	@InModelThread(asynchronous = false)
	public TrackRecord importTrack(long trackId) {
		return this.currentUnit.importTrack(this.getWorld(), trackId);
	}
	
	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot hovered = this.getHoveredItemSnapshot();
		if(null != hovered) {
			if(!this.hasUnitOfWork()) {
				this.createUnitOfWork();
			}
			switch(hovered.getType()) {
				case HoveredItemSnapshot.TYPE_VERTEX:
					this.selectedVertices.add(this.importVertex(hovered.getId()));
					break;
				case HoveredItemSnapshot.TYPE_TRACK:
					this.selectedTracks.add(this.importTrack(hovered.getId()));
					break;
			}
			this.currentUnit.exportScene(this.sceneManager);
		}
	}
	
	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.selectedTracks.clear();
		this.selectedVertices.clear();
		this.resetUnitOfWork();
		this.resetRenderingStream();
	}
}
