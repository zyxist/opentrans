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

/**
 * Vertex is a distinguished point in the network infrastructure. It is identified by its coordinates
 * in the world, and an unique numeric ID.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IVertex {
	/**
	 * Returns the X coordinate of the vertex.
	 * @return X coordinate in world units, since the beginning of the segment.
	 */
	public double x();
	/**
	 * Returns the Y coordinate of the vertex.
	 * @return Y coordinate in world units, since the beginning of the segment.
	 */
	public double y();
	/**
	 * Returns the segment, where the vertex is located.
	 * @return Segment this vertex is assigned to.
	 */
	public Segment getSegment();
}
