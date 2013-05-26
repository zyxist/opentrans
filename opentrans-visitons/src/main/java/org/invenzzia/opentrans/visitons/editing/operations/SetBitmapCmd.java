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
import org.invenzzia.opentrans.visitons.network.Segment;

/**
 * This command sets the bitmap for the segment.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Set background bitmap")
public class SetBitmapCmd implements ICommand {
	/**
	 * Coordinates of the segment to modify.
	 */
	private final int x;
	/**
	 * Coordinates of the segment to modify.
	 */
	private final int y;
	/**
	 * The bitmap to set.
	 */
	private final String bitmapPath;
	/**
	 * Our memento.
	 */
	private String oldBitmapPath;
	
	public SetBitmapCmd(int x, int y, String bitmapPath) {
		this.x = x;
		this.y = y;
		this.bitmapPath = bitmapPath;
		this.oldBitmapPath = null;
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		this.oldBitmapPath = segment.getImagePath();
		segment.setImagePath(this.bitmapPath);
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		segment.setImagePath(this.oldBitmapPath);
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		Segment segment = project.getWorld().findSegment(this.x, this.y);
		this.oldBitmapPath = segment.getImagePath();
		segment.setImagePath(this.bitmapPath);
	}
}
