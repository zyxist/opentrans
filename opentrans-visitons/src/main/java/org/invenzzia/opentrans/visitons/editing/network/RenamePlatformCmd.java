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
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.WorldRecord;

/**
 * This command changes the name of the existing platform.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RenamePlatformCmd implements ICommand {
	private final String newName;
	private final PlatformRecord platformRecord;
	private String oldName;
	
	public RenamePlatformCmd(String name, PlatformRecord record) {
		this.newName = name;
		this.platformRecord = Preconditions.checkNotNull(record);
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		Platform thePlatform = stop.getPlatform(this.platformRecord.getNumber());
		this.oldName = thePlatform.getName();
		thePlatform.setName(this.newName);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		Platform thePlatform = stop.getPlatform(this.platformRecord.getNumber());
		thePlatform.setName(this.oldName);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Stop stop = project.getStopManager().findById(this.platformRecord.getStop().getId());
		Platform thePlatform = stop.getPlatform(this.platformRecord.getNumber());
		thePlatform.setName(this.newName);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}
}
