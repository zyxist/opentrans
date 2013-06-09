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

package org.invenzzia.opentrans.visitons.network;

import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * Some common code for the vertex implementation.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractVertex implements IVertex {
	/**
	 * Unique numerical identifier of this vertex.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Where the vertex is located?
	 */
	protected SegmentCoordinate pos;
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the ID of the vertex.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the information about the position of this vertex on the world.
	 * 
	 * @return 
	 */
	@Override
	public SegmentCoordinate pos() {
		return this.pos;
	}
}
