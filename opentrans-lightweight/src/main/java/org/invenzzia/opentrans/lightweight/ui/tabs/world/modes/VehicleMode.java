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
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.model.navigator.VehicleNavigatorModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorController;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.PopupBuilder;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.CenterAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.RemoveVehicleAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.ReorientVehicleAction;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.editing.network.MoveVehicleCmd;
import org.invenzzia.opentrans.visitons.editing.network.PlaceVehicleCmd;
import org.invenzzia.opentrans.visitons.editing.operations.RemoveVehicleCmd;
import org.invenzzia.opentrans.visitons.events.VehicleRemovedEvent;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.SelectedTrackObjectSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows placing vehicles on the tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(StopMode.class);
	
	private static final String DEFAULT_STATUS_TEXT = "Select a vehicle from the navigator and click on a track to place it. Click on an existing vehicle to select it.";
	private static final String PLATFORM_SELECTED_STATUS_TEXT = "Click on a track to move the selected vehicle. Right-click anywhere to cancel the selection.";
	private static final String SELECT_FROM_NAVIGATOR = "Please select a vehicle from the navigator first!";
	@Inject
	private NavigatorController navigatorController;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private EventBus eventBus;
	@Inject
	private Provider<CenterAction> centerAction;
	@Inject
	private ReorientVehicleAction reorientVehicleAction;
	@Inject
	private RemoveVehicleAction removeVehicleAction;
	/**
	 * The API for edit modes.
	 */
	private IEditModeAPI api;
	/**
	 * For the purpose of vehicle moving, we must remember a selected vehicle.
	 */
	private VehicleRecord selectedVehicle;
	
	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}

	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.info("VehicleMode enabled.");
		this.api = api;
		this.navigatorController.setModel(new VehicleNavigatorModel());
		this.api.setStatusMessage(DEFAULT_STATUS_TEXT);
		this.eventBus.register(this);
	
		this.api.setPopup(PopupBuilder.create()
			.action(this.centerAction)
			.sep()
			.action(this.reorientVehicleAction)
			.action(this.removeVehicleAction)
		);
	}

	@Override
	public void modeDisabled() {
		this.navigatorController.setModel(null);
		this.eventBus.unregister(this);
		logger.info("VehicleMode disabled.");
	}
	
	@InModelThread(asynchronous = false)
	public TrackRecord getTrackRecord(World world, long id) {
		Track track = world.findTrack(id);
		return new TrackRecord(track);
	}
	
	@InModelThread(asynchronous = false)
	public VehicleRecord getVehicleRecord(Project project, long id) {
		Vehicle vehicle = project.getVehicleManager().findById(id);

		VehicleRecord record = new VehicleRecord();
		record.importData(vehicle, project);		
		return record;
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
		if(null != snapshot) {
			switch(snapshot.getType()) {
				case HoveredItemSnapshot.TYPE_TRACK:
					if(null == this.selectedVehicle) {
						this.placeVehicle(snapshot);
					} else {
						this.moveVehicle(snapshot);
					}
					break;
				case HoveredItemSnapshot.TYPE_VEHICLE:
					this.selectVehicle(snapshot);
					break;
			}
		}
	}
	
	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		if(null == this.selectedVehicle) {
			HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
			if(null != snapshot && snapshot.getType() == HoveredItemSnapshot.TYPE_PLATFORM) {
				VehicleRecord record = this.getVehicleRecord(this.projectHolder.getCurrentProject(), snapshot.getId());
				this.reorientVehicleAction.setVehicleRecord(record);
				this.removeVehicleAction.setVehicleRecord(record);
				this.api.showPopup();
			}
		} else {
			this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class, null);
			this.selectedVehicle = null;
			this.api.setStatusMessage(DEFAULT_STATUS_TEXT);
		}
	}
	
	@Override
	public void deletePressed(double worldX, double worldY) {
		if(null != this.selectedVehicle) {
			try {
				this.history.execute(new RemoveVehicleCmd(this.selectedVehicle));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Error while removing a vehicle", exception);
			}
		}
	}
	
	private void placeVehicle(HoveredItemSnapshot snapshot) {
		TrackRecord tr = this.getTrackRecord(this.getWorld(), snapshot.getId());

		Object object = this.navigatorController.getSelectedObject();
		if(null != object) {
			Preconditions.checkState(object instanceof VehicleRecord, "The navigator displays wrong object types: "+object.getClass().getCanonicalName()+"; VehicleRecord expected.");
			VehicleRecord vr = (VehicleRecord) object;
			try {
				this.history.execute(new PlaceVehicleCmd(tr, snapshot.getPosition(), vr));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Error while adding a platform", exception);
			}
		} else {
			this.api.setStatusMessage(SELECT_FROM_NAVIGATOR);
		}
	}
	
	private void moveVehicle(HoveredItemSnapshot snapshot) {
		TrackRecord trackRecord = this.getTrackRecord(this.projectHolder.getCurrentProject().getWorld(), snapshot.getId());
		try {
			this.history.execute(new MoveVehicleCmd(this.selectedVehicle, snapshot.getPosition(), trackRecord));
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Error while moving a vehicle", exception);
		}
	}
	
	private void selectVehicle(HoveredItemSnapshot snapshot) {
		this.selectedVehicle = this.getVehicleRecord(this.projectHolder.getCurrentProject(), snapshot.getId());
		this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class,
			new SelectedTrackObjectSnapshot(NetworkConst.TRACK_OBJECT_VEHICLE, this.selectedVehicle.getId())
		);
		this.api.setStatusMessage(PLATFORM_SELECTED_STATUS_TEXT);
	}
	
	/**
	 * Listen for such an event to see if we do not have to remove the platform selection.
	 * 
	 * @param event 
	 */
	@Subscribe
	public void notifyPlatformRemoved(VehicleRemovedEvent event) {
		if(null != this.selectedVehicle && event.matches(selectedVehicle)) {
			this.selectedVehicle = null;
			this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class, null);
			this.api.setStatusMessage(DEFAULT_STATUS_TEXT);	
		}
	}
}
