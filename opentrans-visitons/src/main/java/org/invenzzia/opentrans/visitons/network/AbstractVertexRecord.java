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

/**
 * Some common code unrelated to the movement and transformations.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractVertexRecord implements IVertexRecord {
	/**
	 * The unique ID of the vertex. Allows proper mapping to the actual vertices. 
	 */
	private long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * The X location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	protected double x;
	/**
	 * The Y location of the vertex. <strong>This is an absolute coordinate!</strong>
	 */
	protected double y;
	
	/**
	 * Returns the vertex ID. The value <tt>IIdentifiable.NEUTRAL_ID</tt> is returned, if the vertex is not exported
	 * to the network model, and thus - no ID is given to it yet.
	 * 
	 * @return Vertex ID.
	 */
	@Override
	public long getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of this vertex. The method shall be called only by the import/export code.
	 * Once set, the ID cannot be changed, as it uniquely identifies this vertex.
	 * 
	 * @param id Vertex ID
	 */
	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("The vertex record ID cannot be changed.");
		}
		this.id = id;
	}
	
	/**
	 * Returns <strong>true</strong>, if this vertex has a destination ID, not temporary one. It means
	 * that we need to update an existing vertex, not create a new one.
	 * 
	 * @return True, if this vertex is persisted.
	 */
	@Override
	public boolean isPersisted() {
		return this.id > IIdentifiable.NEUTRAL_ID;
	}
	
	@Override
	public double x() {
		return this.x;
	}
	
	@Override
	public double y() {
		return this.y;
	}
}
