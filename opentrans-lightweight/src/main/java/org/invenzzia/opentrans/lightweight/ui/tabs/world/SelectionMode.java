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

import java.awt.Cursor;
import java.util.LinkedHashSet;
import java.util.Set;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.ops.MoveVertex;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.SelectionSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selection mode allows selecting group of objects and moving them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMode extends AbstractEditMode {
	private static final int NOTHING = 0;
	private static final int SELECT_AREA = 1;
	private static final int DRAG_VERTEX = 2;
	private static final int DRAG_GROUP = 3;
	
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
	
	private double dragInitialPosX;
	private double dragInitialPosY;
	
	private int selectionMode = NOTHING;
	
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
		this.resetState();
	}
	
	private void resetState() {
		this.selectedTracks.clear();
		this.selectedVertices.clear();
		this.resetUnitOfWork();
		this.resetRenderingStream();
	}
	
	@InModelThread(asynchronous = false)
	public VertexRecord importVertex(long vertexId) {
		return this.currentUnit.importVertex(this.getWorld(), vertexId);
	}
	
	@InModelThread(asynchronous = false)
	public TrackRecord importTrack(long trackId) {
		return this.currentUnit.importTrack(this.getWorld(), trackId);
	}
	
	
	@InModelThread(asynchronous = false)
	public void importTracksFromSelection(World world, double x1, double y1, double x2, double y2) {
		Set<Track> tracks = world.findTracksInArea(x1, y1, x2, y2);
		for(Track track: tracks) {
			this.currentUnit.importTrack(world, track);
		}
	}
	
	@Override
	public boolean captureDragEvents() {
		return true;
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
		this.resetState();
	}
	
	@Override
	public void mouseStartsDragging(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		int selectedVerticesNum = this.selectedVertices.size();
		int selectedTracksNum = this.selectedTracks.size();
		this.api.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if(selectedVerticesNum == 1 && selectedTracksNum == 0) {
			VertexRecord vertexRecord = this.selectedVertices.iterator().next();
			this.dragInitialPosX = worldX;
			this.dragInitialPosY = worldY;
			this.selectionMode = DRAG_VERTEX;
		} else if(selectedVerticesNum == 0 && selectedTracksNum == 0) {
			this.selectionMode = SELECT_AREA;
			this.dragInitialPosX = worldX;
			this.dragInitialPosY = worldY;
		} else {
			this.selectionMode = NOTHING;
		}
	}
	
	@Override
	public void mouseDrags(double worldX, double worldY, double deltaX, double deltaY, boolean altDown, boolean ctrlDown) {
		if(this.selectionMode == SELECT_AREA) {
			this.sceneManager.updateResource(SelectionSnapshot.class, new SelectionSnapshot(this.dragInitialPosX, this.dragInitialPosY, worldX, worldY));
		} else if(selectionMode == DRAG_VERTEX) {
			this.transformEngine.op(MoveVertex.class).move(this.selectedVertices.iterator().next(), worldX, worldY,
				(ctrlDown ? (altDown ? NetworkConst.MODE_ALT2 : NetworkConst.MODE_ALT1) : NetworkConst.MODE_DEFAULT)
			);
			this.currentUnit.exportScene(this.sceneManager);
		}
	}
	
	@Override
	public void mouseStopsDragging(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.api.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if(this.selectionMode == SELECT_AREA) {
			this.createUnitOfWork();
			this.importTracksFromSelection(this.getWorld(), this.dragInitialPosX, this.dragInitialPosY, worldX, worldY);
			if(this.currentUnit.isEmpty()) {
				this.resetState();
			} else {
				this.currentUnit.exportScene(this.sceneManager);
			}
			this.selectionMode = NOTHING;
			this.sceneManager.updateResource(SelectionSnapshot.class, null);
		} else if(this.selectionMode == DRAG_VERTEX) {
			this.applyChanges("Move single vertex");
			this.resetState();
			this.selectionMode = NOTHING; 
		}
	}
	
	@Override
	public void deletePressed(double worldX, double worldY) {
		if(this.selectedTracks.size() > 0) {
			for(TrackRecord tr: this.selectedTracks) {
				this.currentUnit.removeTrack(tr);
			}
			this.applyChanges("Delete tracks");
			this.resetState();
		} else if(this.selectedVertices.size() > 0) {
			this.recordImporter.importAllMissingNeighbors(this.currentUnit, this.selectedVertices);
			for(VertexRecord vr: this.selectedVertices) {
				this.currentUnit.removeVertex(vr);
			}
			this.applyChanges("Delete vertices");
			this.resetState();
		}
	}

	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}
}
