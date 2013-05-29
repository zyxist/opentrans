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
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Moves the platform to the new position.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MovePlatformCmd implements ICommand, ICommandDetails {
	/**
	 * Modified platform record.
	 */
	private final PlatformRecord platformRecord;
	/**
	 * New position of the platform.
	 */
	private final double newPosition;
	/**
	 * Memento of the old track object.
	 */
	private Object memento;
	/**
	 * ID of the destination track.
	 */
	private final long trackId;
	
	public MovePlatformCmd(PlatformRecord platform, double position, TrackRecord track) {
		Preconditions.checkNotNull(track);
		Preconditions.checkArgument(position >= 0.0 && position <= 1.0, "The position must be within range [0.0, 1.0]");
		this.platformRecord = Preconditions.checkNotNull(platform);
		this.newPosition = position;
		this.trackId = track.getId();
	}
	

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Platform platform = this.findPlatform(project);
		TrackObject to = platform.getTrackObject();
		this.memento = to.getMemento(project);
		this.attach(platform, project);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Platform platform = this.findPlatform(project);
		TrackObject to = platform.getTrackObject();
		to.restoreMemento(this.memento, project);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		this.attach(this.findPlatform(project), project);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}
	
	@Override
	public String getCommandName() {
		return "Add platform for stop '"+this.platformRecord.getStop().getName()+"'";
	}
	
	private Platform findPlatform(Project project) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		return stop.getPlatform(this.platformRecord.getNumber());
	}
	
	private void attach(Platform platform, Project project) {
		TrackObject to = platform.getTrackObject();
		to.getTrack().removeTrackObject(platform);
		Track newTrack = project.getWorld().findTrack(this.trackId);
		newTrack.addTrackObject(to);
		to.setPosition(this.newPosition);
	}
}
