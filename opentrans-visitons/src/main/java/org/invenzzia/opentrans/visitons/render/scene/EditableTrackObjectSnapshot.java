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

package org.invenzzia.opentrans.visitons.render.scene;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject.TrackObjectRecord;

/**
 * Snapshot of the track objects on edited tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public class EditableTrackObjectSnapshot extends AbstractTrackObjectSnapshot<TrackRecord> {
	/**
	 * Adds a new track object to the buffer.
	 * 
	 * @param track The track that owns this object.
	 * @param object The track object to render.
	 */
	public void addTrackObject(TrackRecord track, TrackObjectRecord object) {
		this.addTrackObjectInt(object, track.getPointCharacteristics(object.getPosition()));
	}
	
	/**
	 * This method allows adding a track object to render without the need to import the
	 * whole track record. However, the track object must have the location characteristics imported.
	 * 
	 * @param characteristics
	 * @param object 
	 */
	public void addTrackObject(TrackObjectRecord object) {
		Preconditions.checkNotNull(object.getLocation());
		this.addTrackObjectInt(object, object.getLocation());
	}
}
