/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.editing.network;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.history.ICommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Removes a platform that belongs to a specified stop.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RemovePlatformCmd implements ICommand, ICommandDetails {
	/**
	 * The platform to remove (and a memento, too).
	 */
	private final PlatformRecord platformRecord;
	/**
	 * Memento of the track object.
	 */
	private Object memento;
	
	public RemovePlatformCmd(PlatformRecord platformRecord) {
		this.platformRecord = Preconditions.checkNotNull(platformRecord);
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		Platform platform = stop.getPlatform(this.platformRecord.getNumber());
		stop.removePlatform(platform);
		TrackObject to = platform.getTrackObject();
		this.memento = to.getMemento(project);
		to.getTrack().removeTrackObject(platform);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		TrackObject trackObject = new TrackObject();
		trackObject.restoreMemento(memento, project);
		Platform platform = new Platform(stop, this.platformRecord, trackObject);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		Platform platform = stop.getPlatform(this.platformRecord.getNumber());
		stop.removePlatform(platform);
		TrackObject to = platform.getTrackObject();
		to.getTrack().removeTrackObject(platform);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}
	
	@Override
	public String getCommandName() {
		return "Remove platform for stop '"+this.platformRecord.getStop().getName()+"'";
	}

	private Platform findPlatform(Project project) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		return stop.getPlatform(this.platformRecord.getNumber());
	}
}
