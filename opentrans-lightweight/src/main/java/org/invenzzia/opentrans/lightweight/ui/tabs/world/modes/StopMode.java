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
import com.google.inject.Inject;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.model.navigator.StopNavigatorModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorController;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.editing.network.AddPlatformCmd;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows placing, removing and moving stop platforms.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(StopMode.class);
	@Inject
	private NavigatorController navigatorController;
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * The API for edit modes.
	 */
	private IEditModeAPI api;

	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}

	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.info("StopMode enabled.");
		this.api = api;
		this.navigatorController.setModel(new StopNavigatorModel());
		this.api.setStatusMessage("Manage platforms placed on tracks. To add a new platform, first select a stop in the navigator.");
	}

	@Override
	public void modeDisabled() {
		this.navigatorController.setModel(null);
		logger.info("StopMode disabled.");
	}
	
	@InModelThread(asynchronous = false)
	public TrackRecord getTrackRecord(World world, long id) {
		Track track = world.findTrack(id);
		return new TrackRecord(track);
	}
	
	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
		if(null != snapshot && snapshot.getType() == HoveredItemSnapshot.TYPE_TRACK) {
			TrackRecord tr = this.getTrackRecord(this.getWorld(), snapshot.getId());
			
			Object object = this.navigatorController.getSelectedObject();
			if(null != object) {
				Preconditions.checkState(object instanceof StopRecord, "The navigator displays wrong object types: "+object.getClass().getCanonicalName()+"; StopRecord expected.");
				StopRecord stop = (StopRecord) object;

				try {
					this.history.execute(new AddPlatformCmd(tr, snapshot.getPosition(), stop));
				} catch(CommandExecutionException exception) {
					this.dialogBuilder.showError("Error while adding a platform", exception);
				}
			} else {
				this.api.setStatusMessage("Please select a stop from the navigator first!");
			}			
		}
	}
}
