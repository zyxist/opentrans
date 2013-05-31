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

import com.google.common.collect.BiMap;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * Common interface for junctions and vertices. Allows treating them as
 * the same entities by most operations. Vertex is an entity that has a
 * defined position in the world, a tangent, and up to two tracks connected
 * to it. The tracks must be continuous in the vertex.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IVertex extends IIdentifiable {
	/**
	 * Returns the information about the position of this vertex on the world.
	 * 
	 * @return Position of the vertex in the world.
	 */
	public SegmentCoordinate pos();
	/**
	 * Tangent specifies the slope of the tangent line given in this vertex. It is calculated
	 * from the tracks connected to it.
	 * 
	 * @return Tangent in this point.
	 */
	public double tangent();
	/**
	 * Returns the tangent towards the given track.
	 * 
	 * @param tr Track
	 * @return Tangent towards the given track.
	 */
	public double tangentFor(Track tr);
	/**
	 * @return True, if there are two tracks connected to this vertex.
	 */
	public boolean hasAllTracks();
	/**
	 * @return True, if there is exactly one track connected to this vertex.
	 */
	public boolean hasOneTrack();
	/**
	 * @return True, if there are no tracks connected to this vertex.
	 */
	public boolean hasNoTracks();
	/**
	 * If only one track is connected, the method returns it.
	 */
	public Track getTrack();
	/**
	 * Returns the first track bound to this vertex. The method may return
	 * null, if the first slot is not occupied.
	 * 
	 * @return 
	 */
	public Track getFirstTrack();
	/**
	 * Returns the second track bound to this vertex. The method may return
	 * null, if the second slot is not occupied.
	 * 
	 * @return 
	 */
	public Track getSecondTrack();
	/**
	 * Tracks must know, how to remove themselves from the vertex.
	 * 
	 * @param track 
	 */
	public void removeTrack(Track track);
	/**
	 * Creates an approriate vertex record.
	 * 
	 * @return New vertex record instance with the copied state of this vertex.
	 */
	public IVertexRecord createRecord();
	/**
	 * Imports the vertex data from the vertex record.
	 * 
	 * @param vr
	 * @param world
	 * @param trackMapping 
	 */
	public void importConnections(IVertexRecord vr, World world, BiMap<Long, Long> trackMapping);
}
