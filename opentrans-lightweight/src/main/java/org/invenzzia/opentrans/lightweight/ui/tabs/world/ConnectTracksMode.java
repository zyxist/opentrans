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
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.bindings.ActualImporter;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.network.NetworkLayoutChangeCmd;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.IRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.network.transform.Transformations;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConnectTracksMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(ConnectTracksMode.class);
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
	@Inject
	@ActualImporter
	private IRecordImporter recordImporter;
	/**
	 * Controller API for edit modes.
	 */
	private IEditModeAPI api;
	/**
	 * Currently constructed unit of work.
	 */
	private NetworkUnitOfWork currentUnit;
	/**
	 * Transformations performed on that unit.
	 */
	private Transformations transformer;
	
	private VertexRecord firstVertex;
	private VertexRecord secondVertex;

	@Override
	public void modeEnabled(IEditModeAPI api) {
		this.api = api;
		this.firstVertex = null;
		this.secondVertex = null;
	}

	@Override
	public void modeDisabled() {
		this.resetState();
	}
	
	@InModelThread(asynchronous = false)
	public VertexRecord importFreeVertex(final Project project, long vertexId) {
		Vertex vertex = project.getWorld().findVertex(vertexId);
		if(vertex.hasAllTracks()) {
			return null;
		}
		if(null == this.currentUnit) {
			this.currentUnit = this.unitOfWorkProvider.get();
			this.transformer = new Transformations(this.currentUnit, this.recordImporter);
		}
		return this.currentUnit.importVertex(vertex);
	}
	
	@InModelThread(asynchronous = true)
	public void exportScene(final World world) {
		world.exportScene(this.sceneManager, this.cameraModel, false);
	}
	
	private void resetState() {
		this.firstVertex = null;
		this.secondVertex = null;
		this.currentUnit = null;
		this.transformer = null;
		this.sceneManager.updateResource(EditableTrackSnapshot.class, null);
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
		if(null != snapshot) {
			if(snapshot.getType() == HoveredItemSnapshot.TYPE_VERTEX) {
				if(null == this.firstVertex) {
					this.firstVertex = this.importFreeVertex(this.projectHolder.getCurrentProject(), snapshot.getId());
					this.currentUnit.exportScene(this.sceneManager);
				} else {
					try {
						this.secondVertex = this.importFreeVertex(this.projectHolder.getCurrentProject(), snapshot.getId());
						this.transformer.connectTwoVertices(this.firstVertex, this.secondVertex);
						this.history.execute(new NetworkLayoutChangeCmd(currentUnit));
						exportScene(projectHolder.getCurrentProject().getWorld());
					} catch(CommandExecutionException exception) {
						logger.error("Exception occurred while saving the network unit of work.", exception);
					} finally {
						this.resetState();
					}
				}
			}
		}
	}
	
	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.resetState();
	}
}
