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

import com.google.common.base.Preconditions;
import java.awt.Cursor;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractEditState;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractStateMachineEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.ops.CreateNewTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.ExtendTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.MoveVertex;
import org.invenzzia.opentrans.visitons.network.transform.ops.SnapTrackToTrack;
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
	private final DrawingStartsFromOpenVertexState STATE_CONTINUE_TRACK = new DrawingStartsFromOpenVertexState();
	/**
	 * Single instance of one of the states.
	 */
	private final DrawingInProgressDrawTrackState STATE_DRAWING = new DrawingInProgressDrawTrackState();
	/**
	 * The vertex that is updated according to the mouse movements.
	 */
	private VertexRecord boundVertex;
	
	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.debug("DrawTrackMode enabled.");
		this.api = api;
		this.api.setStatusMessage(DEFAULT_STATUS);
		this.setState(this.STATE_CURSOR_FREE);
	}
	
	@Override
	public void modeDisabled() {
		this.resetState();
		logger.debug("DrawTrackMode disabled.");
	}
	
	public void resetState() {
		this.currentUnit = null;
		this.transformer = null;
		this.boundVertex = null;
		this.resetIgnoring();
		this.resetRenderingStream();
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
		this.boundVertex = currentUnit.importVertex(project.getWorld(), vertex);		
		return true;
	}
	
	@InModelThread(asynchronous = false)
	public VertexRecord importSingleVertex(final World world, long vertexId) {
		if(vertexId < IIdentifiable.NEUTRAL_ID) {
			VertexRecord vr = currentUnit.findVertex(vertexId);
			if(vr.hasAllTracks()) {
				return null;
			}
			return vr;
		}
		Vertex vertex = world.findVertex(vertexId);
		if(vertex.hasAllTracks()) {
			return null;
		}
		if(!this.hasUnitOfWork()) {
			this.createUnitOfWork();
		}
		VertexRecord vr = currentUnit.importVertex(world, vertex);
		currentUnit.importTrack(world, vertex.getTrack());
		return vr;
	}

	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
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
			HoveredItemSnapshot snapshot = getHoveredItemSnapshot();
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
		private boolean started = false;
		private double x;
		private double y;
		
		@Override
		public boolean captureMotionEvents() {
			return this.started;
		}
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			if(!hasUnitOfWork()) {
				logger.debug("STATE_NEW_TRACK: creating the unit of work.");
				createUnitOfWork();
			}
			this.x = worldX;
			this.y = worldY;
			this.started = true;
		}
		
		@Override
		public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			try {
				logger.debug("STATE_NEW_TRACK: creating the bound vertex.");
				boundVertex = transformEngine.op(CreateNewTrack.class).create(this.x, this.y, worldX, worldY);
				if(null == boundVertex) {
					logger.debug("STATE_NEW_TRACK: not able to create.");
					setState(STATE_CURSOR_FREE);
					resetState();
				} else {
					addForIgnoring(boundVertex.getTrack(), boundVertex);
					setState(STATE_DRAWING);
				}
			} finally {
				this.started = false;
			}
		}
	}
	
	/**
	 * Long, but self-descriptive. In this case OK.
	 */
	class DrawingStartsFromOpenVertexState extends AbstractEditState {
		private boolean started = false;
		
		@Override
		public boolean captureMotionEvents() {
			return this.started;
		}
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			HoveredItemSnapshot snapshot = getHoveredItemSnapshot();
			if(importFreeVertex(projectHolder.getCurrentProject(), snapshot.getId())) {
				this.started = true;
				currentUnit.exportScene(sceneManager);
			} else {
				setState(STATE_CURSOR_FREE);
			}
		}
		
		@Override
		public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			Preconditions.checkState(null != boundVertex);
			try {
				boundVertex = transformEngine.op(ExtendTrack.class).extend(boundVertex, worldX, worldY,
						(altDown ? NetworkConst.MODE_ALT1 : NetworkConst.MODE_DEFAULT)
					);
				addForIgnoring(boundVertex.getTrack(), boundVertex);
				currentUnit.exportScene(sceneManager);
				setState(STATE_DRAWING);
			} finally {
				this.started = false;
			}
		}
	}
	
	/**
	 * Final state, where we go once the drawing is started. Here we're handling the mouse
	 * movements etc.
	 */
	class DrawingInProgressDrawTrackState extends AbstractEditState {
		private boolean started = false;
		
		@Override
		public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			if(null != boundVertex) {
				HoveredItemSnapshot snapshot = getHoveredItemSnapshot();
				if(null != snapshot) {
					TrackRecord importedTrack = null;
					if(snapshot.getType() == HoveredItemSnapshot.TYPE_TRACK) {
						importedTrack = currentUnit.importTrack(getWorld(), snapshot.getId());
					} else if(snapshot.getType() == HoveredItemSnapshot.TYPE_VERTEX) {
						VertexRecord vr = importSingleVertex(getWorld(), snapshot.getId());
						importedTrack = (vr != null ? vr.getTrack() : null);
					}
					if(null != importedTrack && importedTrack.isOpen()) {
						logger.debug("STATE_DRAWING: snap to another track.");
						try {
							transformEngine.op(SnapTrackToTrack.class).snap(boundVertex, importedTrack);
							applyChanges("Draw tracks");
						} catch(Throwable thr) {
							logger.error("Exception occurred while snapping the tracks.", thr);
						} finally {
							resetState();
							setState(STATE_CURSOR_FREE);
							return;
						}
					}
				}
				logger.debug("STATE_DRAWING: finalizing the position of previous track.");
				transformEngine.op(MoveVertex.class).move(boundVertex, worldX, worldY, (ctrlDown ? NetworkConst.MODE_ALT1 : NetworkConst.MODE_DEFAULT));
				this.started = true; // Inform that by the next move, we start a new track.
				currentUnit.exportScene(sceneManager);
			}
		}
		
		@Override
		public boolean captureMotionEvents() {
			return true;
		}
		
		@Override
		public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			Preconditions.checkState(null != boundVertex);
			if(this.started) {
				logger.debug("STATE_DRAWING: extending the track.");
				this.started = false;
				boundVertex = transformEngine.op(ExtendTrack.class).extend(boundVertex, worldX, worldY,
						(altDown ? NetworkConst.MODE_ALT1 : NetworkConst.MODE_DEFAULT)
					);
				addForIgnoring(boundVertex.getTrack(), boundVertex);
			} else {
				transformEngine.op(MoveVertex.class).move(boundVertex, worldX, worldY,
					(ctrlDown ? NetworkConst.MODE_ALT1 : NetworkConst.MODE_DEFAULT)
				);
			}
			currentUnit.exportScene(sceneManager);
		}
		
		@Override
		public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
			try {
				logger.debug("STATE_DRAWING: finishing the construction and saving the data to the world model.");
				if(this.started) {
					this.started = false;
				} else {
					currentUnit.removeTrack(boundVertex.getTrack());
				}
				applyChanges("Draw tracks");
			} finally {
				setState(STATE_CURSOR_FREE);
				resetState();
			}
		}
	}
}
