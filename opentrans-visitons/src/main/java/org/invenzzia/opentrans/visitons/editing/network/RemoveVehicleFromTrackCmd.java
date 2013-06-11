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

import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.VehicleRemovedEvent;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Allows removing a vehicle from the track.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Remove vehicle from track")
public class RemoveVehicleFromTrackCmd implements ICommand {
	private final long vehicleId;
	private Object memento;
	
	public RemoveVehicleFromTrackCmd(VehicleRecord vehicle) {
		this.vehicleId = vehicle.getId();
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		TrackObject to = vehicle.getTrackObject();
		this.memento = to.getMemento(project);
		vehicle.setTrackObject(null);
		eventBus.post(new VehicleRemovedEvent(vehicle));
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		
		TrackObject trackObject = new TrackObject();
		trackObject.restoreMemento(memento, project);
		vehicle.setTrackObject(trackObject);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Vehicle vehicle = project.getVehicleManager().findById(this.vehicleId);
		TrackObject to = vehicle.getTrackObject();
		vehicle.setTrackObject(null);
		eventBus.post(new VehicleRemovedEvent(vehicle));
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}
}
