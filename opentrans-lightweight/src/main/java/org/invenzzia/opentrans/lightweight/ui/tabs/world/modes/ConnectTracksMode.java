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

package org.invenzzia.opentrans.lightweight.ui.tabs.world.modes;

import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.network.IVertex;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ops.BindVertices;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this mode, we select two vertices connected to a single track. After selecting the second
 * vertex, a curve is drawn between them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConnectTracksMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(ConnectTracksMode.class);
	private static final String DEFAULT_STATUS = "Connect two ending vertices with a curve. Select the first vertex.";
	private static final String ONLY_SINGLE_TRACK = "The selected vertex must be connected with exactly one track.";
	private static final String FIRST_VERTEX_SELECTED = "Select the destination vertex to create a curve. The position of this vertex may be adjusted to match the constraints.";
	/**
	 * First vertex.
	 */
	private VertexRecord firstVertex;
	/**
	 * Second vertex - here, the position of this point may be adjusted by the transformation algorithm.
	 */
	private VertexRecord secondVertex;

	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.debug("ConnectTracksMode enabled.");
		this.api = api;
		this.api.setStatusMessage(DEFAULT_STATUS);
		this.firstVertex = null;
		this.secondVertex = null;
	}

	@Override
	public void modeDisabled() {
		this.resetState();
		logger.debug("ConnectTracksMode disabled.");
	}
	
	@InModelThread(asynchronous = false)
	public VertexRecord importFreeVertex(final Project project, long vertexId) {
		IVertex vertex = project.getWorld().findVertex(vertexId);
		if(vertex.hasAllTracks()) {
			return null;
		}
		if(null == this.currentUnit) {
			this.createUnitOfWork();
		}
		return (VertexRecord) this.currentUnit.importVertex(project.getWorld(), vertex);
	}
	
	private void resetState() {
		this.api.setStatusMessage(DEFAULT_STATUS);
		this.firstVertex = null;
		this.secondVertex = null;
		this.currentUnit = null;
		this.sceneManager.updateResource(EditableTrackSnapshot.class, null);
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
		if(null != snapshot) {
			if(snapshot.getType() == HoveredItemSnapshot.TYPE_VERTEX) {
				if(null == this.firstVertex) {
					this.firstVertex = this.importFreeVertex(this.projectHolder.getCurrentProject(), snapshot.getId());
					if(null != this.firstVertex) {
						this.api.setStatusMessage(FIRST_VERTEX_SELECTED);
					} else {
						this.api.setStatusMessage(ONLY_SINGLE_TRACK);
					}
					this.currentUnit.exportScene(this.sceneManager);
				} else {
					this.secondVertex = this.importFreeVertex(this.getProject(), snapshot.getId());
					this.transformEngine.op(BindVertices.class).bind(this.firstVertex, this.secondVertex);
					this.applyChanges("Connect two tracks");
					this.resetState();
				}
			}
		}
	}
	
	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.resetState();
	}

	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}
}
