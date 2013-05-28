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

/**
 * Some track objects may contain names. This interface allows the whole
 * infrastructure to retrieve them.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface INamedTrackObject extends ITrackObject {
	/**
	 * Returns the name of this track object (may be used for rendering).
	 * 
	 * @return Track object name.
	 */
	public String getTrackObjectName();
}
