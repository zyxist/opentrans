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
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.IRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.network.transform.Transformations;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;

/**
 * Common code shared by most of the edit modes used for drawing tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractTrackModeAPI {
	@Inject
	protected Provider<NetworkUnitOfWork> unitOfWorkProvider;
	@Inject
	protected SceneManager sceneManager;
	@Inject
	protected History<ICommand> history;
	@Inject
	protected IProjectHolder projectHolder;
	@Inject
	protected CameraModel cameraModel;
	@Inject
	@ActualImporter
	protected IRecordImporter recordImporter;	
	/**
	 * Controller API for edit modes.
	 */
	protected IEditModeAPI api;
	/**
	 * Currently constructed unit of work.
	 */
	protected NetworkUnitOfWork currentUnit;
	/**
	 * Transformations performed on that unit.
	 */
	protected Transformations transformer;
	
	/**
	 * Creates a new unit of work. You must first check whether the unit of work exists.
	 */
	protected void createUnitOfWork() {
		this.currentUnit = this.unitOfWorkProvider.get();
		this.transformer = new Transformations(this.currentUnit, this.recordImporter);
	}
	
	/**
	 * Returns true, if the unit of work exists.
	 * 
	 * @return Whether the unit of work exists.
	 */
	protected boolean hasUnitOfWork() {
		return null != this.currentUnit;
	}
	
	/**
	 * Clears the unit of work.
	 */
	protected void resetUnitOfWork() {
		this.currentUnit = null;
		this.transformer = null;
	}
	
	/**
	 * Exports the final scene to the renderer. The method delegates this action
	 * to the model thread.
	 * 
	 * @param world World to export.
	 */
	@InModelThread(asynchronous = true)
	public void exportScene(final World world) {
		world.exportScene(this.sceneManager, this.cameraModel, false);
	}
	
	/**
	 * Clears the editable track snapshot in the renderer.
	 */
	protected void resetRenderingStream() {
		this.sceneManager.updateResource(EditableTrackSnapshot.class, null);
	}
	
	/**
	 * Extracts the world from the current project.
	 * 
	 * @return World of the current project.
	 */
	protected World getWorld() {
		return this.projectHolder.getCurrentProject().getWorld();
	}
	
	/**
	 * Extracts the current project.
	 * 
	 * @return Current project. 
	 */
	protected Project getProject() {
		return this.projectHolder.getCurrentProject();
	}
	
	/**
	 * Returns the snapshot containing the information about the currently hovered item.
	 * If nothing is hovered, the method returns null.
	 * 
	 * @return Info, which object is currently hovered by the mouse cursor.
	 */
	protected HoveredItemSnapshot getHoveredItemSnapshot() {
		return this.sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
	}
	
	/**
	 * Creates a history command and executes it.
	 */
	protected void applyChanges() {
		if(!this.currentUnit.isEmpty()) {
			try {
				this.history.execute(new NetworkLayoutChangeCmd(this.currentUnit));
				this.exportScene(this.getWorld());
			} catch(CommandExecutionException exception) {
				this.handleCommandExecutionError(exception);
			}
		}
	}
	
	protected abstract void handleCommandExecutionError(CommandExecutionException exception);
}
