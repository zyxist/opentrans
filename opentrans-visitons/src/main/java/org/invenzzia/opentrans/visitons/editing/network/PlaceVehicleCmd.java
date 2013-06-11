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
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Places the vehicle on a track.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Place vehicle on track")
public class PlaceVehicleCmd implements ICommand {
	private final long trackId;
	private final double position;
	private final long vehicleId;
	
	public PlaceVehicleCmd(TrackRecord tr, double position, VehicleRecord vehicle) {
		Preconditions.checkArgument(position >= 0.0 && position <= 1.0, "Vehicle position on track is not within range [0.0, 1.0]");
		this.trackId = Preconditions.checkNotNull(tr).getId();
		this.position = position;
		this.vehicleId = Preconditions.checkNotNull(vehicle).getId();
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		Track track = project.getWorld().findTrack(this.trackId);
		
		TrackObject<Vehicle> trackObject = new TrackObject<>();
		trackObject.setPosition(this.position);
		vehicle.setTrackObject(trackObject);
		track.addTrackObject(trackObject);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		TrackObject to = vehicle.getTrackObject();
		vehicle.setTrackObject(null);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		Track track = project.getWorld().findTrack(this.trackId);
		
		TrackObject<Vehicle> trackObject = new TrackObject<>();
		trackObject.setPosition(this.position);
		vehicle.setTrackObject(trackObject);
		track.addTrackObject(trackObject);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));	}

}
