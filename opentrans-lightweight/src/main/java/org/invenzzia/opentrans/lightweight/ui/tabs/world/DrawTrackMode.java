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
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.network.NetworkLayoutChangeCmd;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.Transformations;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows drawing new tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DrawTrackMode extends AbstractStateMachineEditMode {
	private final Logger logger = LoggerFactory.getLogger(DrawTrackMode.class);
	private static final String DEFAULT_STATUS = "Start drawing tracks by clicking within the world area, on the existing vertex or track.";
	
	/**
	 * Single instance of one of the states.
	 */
	private final CursorFreeDrawTrackState STATE_CURSOR_FREE = new CursorFreeDrawTrackState();
	/**
	 * Single instance of one of the states.
	 */
	private final NewDrawingStartsDrawTrackState STATE_NEW_TRACK = new NewDrawingStartsDrawTrackState();
	/**
	 * Single instance of one of the states.
	 */
	private final DrawingStartsFromExistingHalfFreePointDrawTrackState STATE_CONTINUE_TRACK = new DrawingStartsFromExistingHalfFreePointDrawTrackState();
	/**
	 * Single instance of one of the states.
	 */
	private final DrawingInProgressDrawTrackState STATE_DRAWING = new DrawingInProgressDrawTrackState();
	/**
	 * The vertex that is updated according to the mouse movements.
	 */
	private VertexRecord boundVertex;
	/**
	 * The previously edited vertex - currently adjusted track is connected
	 * to it on the opposite side.
	 */
	private VertexRecord previousBoundVertex;
	/**
	 * Type of the next drawn track.
	 */
	private int nextType = 0;
	
	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.debug("DrawTrackMode enabled.");
		this.api = api;
		this.api.setStatusMessage(DEFAULT_STATUS);
		this.setState(this.STATE_CURSOR_FREE);
	}
	
	@Override
	public void modeDisabled() {
		this.currentUnit = null;
		this.transformer = null;
		this.boundVertex = null;
		this.previousBoundVertex = null;
		this.resetRenderingStream();
		logger.debug("DrawTrackMode disabled.");
	}
	
	@InModelThread(asynchronous = false)
	public boolean isVertexFree(final Project project, long vertexId) {
		return !project.getWorld().findVertex(vertexId).hasAllTracks();
	}
	
	@InModelThread(asynchronous = false)
	public boolean importFreeVertex(final Project project, long vertexId) {
		Vertex vertex = project.getWorld().findVertex(vertexId);
		if(vertex.hasAllTracks()) {
			return false;
		}
		if(!this.hasUnitOfWork()) {
			this.createUnitOfWork();
		}
		VertexRecord record = currentUnit.importVertex(vertex);
		this.previousBoundVertex = record;
		
		Track track = vertex.getTrack();
		if(null != track) {
			this.nextType = (track.getType() == NetworkConst.TRACK_STRAIGHT ? 1 : 0);
		} else {
			this.nextType = 0;
		}
		
		return true;
	}
	
	/**
	 * This is what happens if we are not currently drawing anything. This is an entry point for
	 * other states in the machine.
	 */
	class CursorFreeDrawTrackState extends AbstractEditState {
		private boolean cursorChanged = false;
		
		@Override
		public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
			if(null == snapshot) {
				if(this.cursorChanged) {
					api.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					this.cursorChanged = false;
				}
			} else if(snapshot.getType() == HoveredItemSnapshot.TYPE_VERTEX) {
				if(!isVertexFree(projectHolder.getCurrentProject(), snapshot.getId())) {
					api.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					cursorChanged = true;
				} else {
					api.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}
		
		@Override
		public boolean captureMotionEvents() {
			return true;
		}
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
			if(null == snapshot) {
				setState(STATE_NEW_TRACK);
			} else if(snapshot.getType() == HoveredItemSnapshot.TYPE_VERTEX) {
				setState(STATE_CONTINUE_TRACK);
			} else {
				return;
			}
			// Delegate the call to the new state.
			getState().leftActionPerformed(worldX, worldY, altDown, ctrlDown);
		}
	}
	
	class NewDrawingStartsDrawTrackState extends AbstractEditState {
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			if(null == currentUnit) {
				logger.debug("leftAction: creating the unit of work.");
				currentUnit = unitOfWorkProvider.get();
				transformer = new Transformations(currentUnit, recordImporter);
			}
			logger.debug("leftAction: creating the bound vertex.");
			VertexRecord vr = new VertexRecord();
			vr.setPosition(worldX, worldY);
			currentUnit.addVertex(vr);
			previousBoundVertex = vr;
			currentUnit.exportScene(sceneManager);
			
			setState(STATE_DRAWING);
		}
	}
	
	/**
	 * Long, but self-descriptive. In this case OK.
	 */
	class DrawingStartsFromExistingHalfFreePointDrawTrackState extends AbstractEditState {
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
			if(importFreeVertex(projectHolder.getCurrentProject(), snapshot.getId())) {
				currentUnit.exportScene(sceneManager);
				setState(STATE_DRAWING);
			} else {
				setState(STATE_CURSOR_FREE);
			}
		}
	}
	
	/**
	 * Final state, where we go once the drawing is started. Here we're handling the mouse
	 * movements etc.
	 */
	class DrawingInProgressDrawTrackState extends AbstractEditState {
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			if(null != boundVertex) {
				logger.debug("leftAction: advancing the bound vertex.");
				currentUnit.addVertex(boundVertex);
				previousBoundVertex = boundVertex;
				boundVertex = null;
				nextType = (nextType == 0 ? 1 : 0);

				currentUnit.exportScene(sceneManager);
			}
		}
		
		@Override
		public boolean captureMotionEvents() {
			return true;
		}
		
		@Override
		public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			if(null == boundVertex) {
				boundVertex = new VertexRecord();
				if(nextType == 0) {
					transformer.createStraightTrack(previousBoundVertex, boundVertex);
				} else {
					transformer.createCurvedTrack(previousBoundVertex, boundVertex);
				}
			} else {
				TrackRecord tr = boundVertex.getTrackTo(previousBoundVertex);
				if(nextType == 0) {
					transformer.updateStraightTrack(
						tr,
						boundVertex,
						worldX,
						worldY,
						(ctrlDown ? Transformations.STR_MODE_FREE : Transformations.STR_MODE_LENGHTEN)
					);
				} else {
					transformer.updateCurvedTrack(tr, boundVertex, worldX, worldY);
				}
			}
			currentUnit.exportScene(sceneManager);
		}
		
		@Override
		public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			try {
				logger.debug("rightAction: finishing the construction and saving the data to the world model.");
				if(null != boundVertex) {
					TrackRecord tr = boundVertex.getTrackTo(previousBoundVertex);
					if(null != tr) {
						currentUnit.removeTrack(tr);
					}
					if(!currentUnit.isEmpty()) {
						history.execute(new NetworkLayoutChangeCmd(currentUnit));
					}
					exportScene(projectHolder.getCurrentProject().getWorld());
				}
				setState(STATE_CURSOR_FREE);
			} catch(CommandExecutionException exception) {
				logger.error("Exception occurred while saving the network unit of work.", exception);
			} finally {
				currentUnit = null;
				transformer = null;
				boundVertex = null;
				previousBoundVertex = null;
				nextType = 0;
				resetRenderingStream();
			}
		}
	}
}
