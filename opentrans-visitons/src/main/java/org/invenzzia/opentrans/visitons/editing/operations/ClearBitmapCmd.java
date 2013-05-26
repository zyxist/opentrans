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

package org.invenzzia.opentrans.visitons.editing.operations;

import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.Segment;
import org.invenzzia.opentrans.visitons.network.WorldRecord;

/**
 * Clears the bitmap set for a segment.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Clear segment bitmap")
public class ClearBitmapCmd implements ICommand {
	/**
	 * Coordinates of the segment to modify.
	 */
	private final int x;
	/**
	 * Coordinates of the segment to modify.
	 */
	private final int y;
	/**
	 * Our memento.
	 */
	private String oldBitmapPath;
	
	public ClearBitmapCmd(int x, int y) {
		this.x = x;
		this.y = y;
		this.oldBitmapPath = null;
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		this.oldBitmapPath = segment.getImagePath();
		segment.setImagePath(null);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		segment.setImagePath(this.oldBitmapPath);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		this.oldBitmapPath = segment.getImagePath();
		segment.setImagePath(null);
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}
}
