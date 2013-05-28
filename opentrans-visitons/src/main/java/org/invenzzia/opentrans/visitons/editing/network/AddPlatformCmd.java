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
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * This command places a new platform for the given stop.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AddPlatformCmd implements ICommand, ICommandDetails {
	/**
	 * Where to install the platform?
	 */
	private final TrackRecord tr;
	/**
	 * The position of the platform on a track.
	 */
	private final double position;
	/**
	 * The stop, where the platform should be added to.
	 */
	private final StopRecord stop;
	/**
	 * Form of memento.
	 */
	private int platformNumber;
	
	public AddPlatformCmd(TrackRecord tr, double position, StopRecord stop) {
		Preconditions.checkArgument(position >= 0.0 && position <= 1.0, "Platform position on track is not within range [0.0, 1.0]");
		this.tr = Preconditions.checkNotNull(tr);
		this.position = position;
		this.stop = Preconditions.checkNotNull(stop);
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Stop theStop = project.getStopManager().findById(this.stop.getId());
		
		TrackObject trackObject = new TrackObject();
		trackObject.setPosition(this.position);
		Platform platform = new Platform(theStop, trackObject);
		platform.setName("Platform "+platform.getNumber());
		Track theTrack = project.getWorld().findTrack(this.tr.getId());
		theTrack.addTrackObject(trackObject);
		this.platformNumber = platform.getNumber();
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
	}

	@Override
	public String getCommandName() {
		return "Add platform for stop '"+this.stop.getName()+"'";
	}
}
