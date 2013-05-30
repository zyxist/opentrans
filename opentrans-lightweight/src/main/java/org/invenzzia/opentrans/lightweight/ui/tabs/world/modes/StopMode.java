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
import org.invenzzia.opentrans.lightweight.model.navigator.StopNavigatorModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorController;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.AbstractEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.PopupBuilder;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.CenterAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.RemovePlatformAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.RenamePlatformAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.popups.ReorientPlatformAction;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.editing.network.AddPlatformCmd;
import org.invenzzia.opentrans.visitons.editing.network.MovePlatformCmd;
import org.invenzzia.opentrans.visitons.editing.network.RemovePlatformCmd;
import org.invenzzia.opentrans.visitons.events.PlatformRemovedEvent;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.SelectedTrackObjectSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows placing, removing and moving stop platforms.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(StopMode.class);
	
	private static final String DEFAULT_STATUS_TEXT = "Select a stop from the navigator and click on a track to place a stop platform. Click on an existing platform to select it.";
	private static final String PLATFORM_SELECTED_STATUS_TEXT = "Click on a track to move the selected platform. Right-click anywhere to cancel the selection.";
	private static final String SELECT_FROM_NAVIGATOR = "Please select a stop from the navigator first!";
	
	@Inject
	private NavigatorController navigatorController;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Provider<CenterAction> centerAction;
	@Inject
	private RenamePlatformAction renamePlatformAction;
	@Inject
	private ReorientPlatformAction reorientPlatformAction;
	@Inject
	private RemovePlatformAction removePlatformAction;
	@Inject
	private EventBus eventBus;
	/**
	 * The API for edit modes.
	 */
	private IEditModeAPI api;
	/**
	 * For the purpose of platform moving, we must remember a selected platform.
	 */
	private PlatformRecord selectedPlatform;

	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}

	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.info("StopMode enabled.");
		this.api = api;
		this.navigatorController.setModel(new StopNavigatorModel());
		this.api.setStatusMessage(DEFAULT_STATUS_TEXT);
		this.eventBus.register(this);
	
		this.api.setPopup(PopupBuilder.create()
			.action(this.centerAction)
			.sep()
			.action(this.renamePlatformAction)
			.action(this.reorientPlatformAction)
			.action(this.removePlatformAction)
		);
	}

	@Override
	public void modeDisabled() {
		this.navigatorController.setModel(null);
		this.eventBus.unregister(this);
		logger.info("StopMode disabled.");
	}
	
	@InModelThread(asynchronous = false)
	public TrackRecord getTrackRecord(World world, long id) {
		Track track = world.findTrack(id);
		return new TrackRecord(track);
	}
	
	@InModelThread(asynchronous = false)
	public PlatformRecord getPlatformRecord(Project project, long stopId, int number) {
		Stop stop = project.getStopManager().findById(stopId);

		StopRecord stopRecord = new StopRecord();
		stopRecord.importData(stop, project);		
		PlatformRecord record = stopRecord.getPlatform(number);

		return record;
	}
	
	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
		if(null != snapshot) {
			switch(snapshot.getType()) {
				case HoveredItemSnapshot.TYPE_TRACK:
					if(null == this.selectedPlatform) {
						this.placeNewPlatform(snapshot);
					} else {
						this.movePlatform(snapshot);
					}
					break;
				case HoveredItemSnapshot.TYPE_PLATFORM:
					this.selectPlatform(snapshot);
					break;
			}
		}
	}
	
	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		if(null == this.selectedPlatform) {
			HoveredItemSnapshot snapshot = sceneManager.getResource(HoveredItemSnapshot.class, HoveredItemSnapshot.class);
			if(null != snapshot && snapshot.getType() == HoveredItemSnapshot.TYPE_PLATFORM) {
				PlatformRecord record = this.getPlatformRecord(this.projectHolder.getCurrentProject(), snapshot.getId(), snapshot.getNumber());
				this.renamePlatformAction.setPlatformRecord(record);
				this.reorientPlatformAction.setPlatformRecord(record);
				this.removePlatformAction.setPlatformRecord(record);
				this.api.showPopup();
			}
		} else {
			this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class, null);
			this.selectedPlatform = null;
			this.api.setStatusMessage(DEFAULT_STATUS_TEXT);
		}
	}
	
	@Override
	public void deletePressed(double worldX, double worldY) {
		if(null != this.selectedPlatform) {
			try {
				this.history.execute(new RemovePlatformCmd(this.selectedPlatform));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Error while removing a platform", exception);
			}
		}
	}

	private void placeNewPlatform(HoveredItemSnapshot snapshot) {
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
			this.api.setStatusMessage(SELECT_FROM_NAVIGATOR);
		}
	}

	private void movePlatform(HoveredItemSnapshot snapshot) {
		TrackRecord trackRecord = this.getTrackRecord(this.projectHolder.getCurrentProject().getWorld(), snapshot.getId());
		try {
			this.history.execute(new MovePlatformCmd(this.selectedPlatform, snapshot.getPosition(), trackRecord));
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Error while adding a platform", exception);
		}
	}

	private void selectPlatform(HoveredItemSnapshot snapshot) {
		this.selectedPlatform = this.getPlatformRecord(this.projectHolder.getCurrentProject(), snapshot.getId(), snapshot.getNumber());
		this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class,
			new SelectedTrackObjectSnapshot(NetworkConst.TRACK_OBJECT_PLATFORM, this.selectedPlatform.getId(), this.selectedPlatform.getNumber())
		);
		this.api.setStatusMessage(PLATFORM_SELECTED_STATUS_TEXT);
	}
	
	/**
	 * Listen for such an event to see if we do not have to remove the platform selection.
	 * 
	 * @param event 
	 */
	@Subscribe
	public void notifyPlatformRemoved(PlatformRemovedEvent event) {
		if(null != this.selectedPlatform && event.matches(selectedPlatform)) {
			this.selectedPlatform = null;
			this.sceneManager.updateResource(SelectedTrackObjectSnapshot.class, null);
			this.api.setStatusMessage(DEFAULT_STATUS_TEXT);	
		}
	}
}
