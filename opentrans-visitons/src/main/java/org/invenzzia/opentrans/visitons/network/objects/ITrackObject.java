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

import org.invenzzia.opentrans.visitons.network.Track;

/**
 * Represents an object that can be put on a track.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ITrackObject {
	/**
	 * Track location.
	 * 
	 * @return 
	 */
	public Track getTrack();
	/**
	 * Returns the position of this object on the track. The returned value
	 * shall be within the range <tt>0.0 ... 1.0</tt>.
	 * 
	 * @return Position of this object on a track.
	 */
	public double getTrackPosition();
	/**
	 * Sets the position of this object on the track.
	 * 
	 * @param track
	 * @param position Position on this track: from ranges 0.0 to 1.0
	 */
	public void setPosition(Track track, double position);
}
