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

package org.invenzzia.opentrans.visitons.editing.common;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.history.ICommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.objects.ITrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Base command for changing the orientation of track objects. It advances the orientation
 * value by 1 until a certain condition passes.
 * 
 * @param R The supported record type of some entity that implements {@link ITrackObject}
 * @param E The actual entity type.
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractReorientObjectCmd<R, E extends ITrackObject> implements ICommand, ICommandDetails {
	protected final R record;
	private final String title;
	private byte oldOrientation;
	
	public AbstractReorientObjectCmd(R record, String commandTitle) {
		this.record = Preconditions.checkNotNull(record);
		this.title = commandTitle;
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		E entity = this.getEntity(project);
		TrackObject object = entity.getTrackObject();
		this.oldOrientation = object.getOrientation();
		object.setOrientation(this.advanceOrientation(this.oldOrientation));
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		E entity = this.getEntity(project);
		TrackObject object = entity.getTrackObject();
		object.setOrientation(this.oldOrientation);
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		E entity = this.getEntity(project);
		TrackObject object = entity.getTrackObject();
		object.setOrientation(this.advanceOrientation(this.oldOrientation));
		
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(project.getWorld())));
	}

	@Override
	public String getCommandName() {
		return this.title;
	}

	/**
	 * The method shall be used for specifying the orientation advancing rule. The method
	 * shall be deterministic and pure - for a given orientation it shall always produce
	 * the same result.
	 * 
	 * @param oldOrientation
	 * @return 
	 */
	protected abstract byte advanceOrientation(byte oldOrientation);
	/**
	 * Extracts the entity from the project.
	 * 
	 * @param project
	 * @return Extracted entity.
	 */
	protected abstract E getEntity(Project project);
}
