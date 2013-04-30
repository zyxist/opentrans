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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.network.NetworkLayoutChangeCmd;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.network.transform.Transformations;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows drawing new tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DrawTrackMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(DrawTrackMode.class);
	
	@Inject
	private Provider<NetworkUnitOfWork> unitOfWorkProvider;
	@Inject
	private SceneManager sceneManager;
	@Inject
	private History<ICommand> history;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private CameraModel cameraModel;
	/**
	 * Currently constructed unit of work.
	 */
	private NetworkUnitOfWork currentUnit;
	/**
	 * Transformations performed on that unit.
	 */
	private Transformations transformer;
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
	 * Currently constructed track.
	 */
	private TrackRecord track;
	/**
	 * Type of the next drawn track.
	 */
	private int nextType = 0;
	
	@Override
	public void modeEnabled() {
		logger.debug("DrawTrackMode enabled.");
	}
	
	@Override
	public void modeDisabled() {
		this.currentUnit = null;
		this.transformer = null;
		this.track = null;
		this.boundVertex = null;
		this.previousBoundVertex = null;
		this.resetRenderingStream();
		logger.debug("DrawTrackMode disabled.");
	}
	
	@Override
	public boolean captureMotionEvents() {
		return this.currentUnit != null;
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		if(null == this.currentUnit) {
			logger.debug("leftAction: creating the unit of work.");
			this.currentUnit = this.unitOfWorkProvider.get();
			this.transformer = new Transformations(this.currentUnit);
		}
		if(null == this.boundVertex) {
			logger.debug("leftAction: creating the bound vertex.");
			VertexRecord vr = new VertexRecord();
			vr.setPosition(worldX, worldY);
			this.currentUnit.addVertex(vr);
			this.previousBoundVertex = vr;
		} else {
			logger.debug("leftAction: advancing the bound vertex.");
			this.currentUnit.addVertex(this.boundVertex);
			this.previousBoundVertex = this.boundVertex;
			this.boundVertex = null;
			this.nextType = (this.nextType == 0 ? 1 : 0);
		}

		this.currentUnit.exportScene(this.sceneManager);
	}

	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		try {
			logger.debug("rightAction: finishing the construction and saving the data to the world model.");
			this.history.execute(new NetworkLayoutChangeCmd(this.currentUnit));
			this.exportScene(this.projectHolder.getCurrentProject().getWorld());
		} catch(CommandExecutionException exception) {
			logger.error("Exception occurred while saving the network unit of work.", exception);
		} finally {
			this.currentUnit = null;
			this.transformer = null;
			this.track = null;
			this.boundVertex = null;
			this.previousBoundVertex = null;
			this.nextType = 0;
			this.resetRenderingStream();
		}
	}
	
	@Override
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		if(null == this.boundVertex) {
			this.boundVertex = new VertexRecord();
			if(this.nextType == 0) {
				this.transformer.createStraightTrack(this.previousBoundVertex, this.boundVertex);
			} else {
				this.transformer.createCurvedTrack(this.previousBoundVertex, this.boundVertex);
			}
		} else {
			TrackRecord tr = this.boundVertex.getTrackTo(this.previousBoundVertex);
			if(this.nextType == 0) {
				this.transformer.updateStraightTrack(
					tr,
					this.boundVertex,
					worldX,
					worldY,
					(ctrlDown ? Transformations.STR_MODE_FREE : Transformations.STR_MODE_LENGHTEN)
				);
			} else {
				this.transformer.updateCurvedTrack(tr, this.boundVertex, worldX, worldY);
			}
		}
		this.currentUnit.exportScene(this.sceneManager);
	}
	
	private void resetRenderingStream() {
		this.sceneManager.updateResource(EditableTrackSnapshot.class, null);
	}
	
	@InModelThread(asynchronous = true)
	public void exportScene(final World world) {
		world.exportScene(this.sceneManager, this.cameraModel, false);
	}
}
