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

package org.invenzzia.opentrans.visitons.network.objects;

import org.invenzzia.helium.data.interfaces.IIdentifiable;

/**
 * This interface shall be implemented by the entities that can be placed
 * on the tracks.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ITrackObject {
	/**
	 * Returns the possessed track object.
	 * 
	 * @return 
	 */
	public TrackObject getTrackObject();
	/**
	 * Identifies the type of this object. The value returned by this method
	 * shall be unique for all the track object types.
	 * 
	 * @return 
	 */
	public int getType();
}
