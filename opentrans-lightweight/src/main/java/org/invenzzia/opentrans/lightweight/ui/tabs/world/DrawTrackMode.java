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
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.network.transform.Transformations;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;

/**
 * This mode allows drawing new tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DrawTrackMode extends AbstractEditMode {
	@Inject
	private Provider<NetworkUnitOfWork> unitOfWorkProvider;
	@Inject
	private SceneManager sceneManager;
	
	private NetworkUnitOfWork currentUnit;
	private Transformations transformer;
	
	private VertexRecord boundVertex;
	private VertexRecord previousBoundVertex;
	private TrackRecord track;
	/**
	 * Type of the next drawn track.
	 */
	private int nextType = 0;
	
	@Override
	public void modeEnabled() {
	}
	
	@Override
	public void modeDisabled() {
		this.currentUnit = null;
		this.transformer = null;
		this.track = null;
		this.boundVertex = null;
		this.previousBoundVertex = null;
		this.resetRenderingStream();
	}
	
	@Override
	public boolean captureMotionEvents() {
		return this.currentUnit != null;
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		if(null == this.currentUnit) {
			this.currentUnit = this.unitOfWorkProvider.get();
			this.transformer = new Transformations(this.currentUnit);
		}
		if(null == this.boundVertex) {
			VertexRecord vr = new VertexRecord();
			vr.setPosition(worldX, worldY);
			this.currentUnit.addVertex(vr);
			this.previousBoundVertex = vr;
		} else {
			this.currentUnit.addVertex(this.boundVertex);
			this.previousBoundVertex = this.boundVertex;
			this.boundVertex = null;
			this.nextType = (this.nextType == 0 ? 1 : 0);
		}

		this.currentUnit.exportScene(this.sceneManager);
	}

	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.currentUnit = null;
		this.transformer = null;
		this.track = null;
		this.boundVertex = null;
		this.previousBoundVertex = null;
		this.nextType = 0;
		this.resetRenderingStream();
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
}
