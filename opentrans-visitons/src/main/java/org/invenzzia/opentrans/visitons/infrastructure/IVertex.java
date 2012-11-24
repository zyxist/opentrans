/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.infrastructure;

import java.util.Map;
import org.invenzzia.opentrans.visitons.world.Segment;

/**
 * Description here.
 *
 * @author Tomasz Jędrzejewski
 */
public interface IVertex<T extends IVertex> extends ICopiable<T> {
	/**
	 * @return Unique vertex ID.
	 */
	public long getId();
	/**
	 * Sets the unique vertex ID. This method shall be used by the {@link Graph} internal code.
	 * @param id 
	 */
	public void setId(long id);
	/**
	 * @return The segment of the vertex.
	 */
	public Segment getSegment();
	/**
	 * @return X coordinate in the segment units.
	 */
	public double x();
	/**
	 * @return Y coordinate in the segment units.
	 */
	public double y();
	/**
	 * Increates the vertex degree, so that more tracks can be connected.
	 */
	public void expand(int by);
	/**
	 * Returns the number of tracks bound to this vertex.
	 * 
	 * @return Number of tracks bound to this vertex.
	 */
	public int getTrackCount();
	/**
	 * Returns an array containing all the tracks bound to this vertex.
	 * 
	 * @return The bound tracks.
	 */
	public ITrack[] getTracks();
	/**
	 * Returns the specified track bound to this vertex. The track must exist.
	 * 
	 * @param id The ID of the track.
	 * @return The track object.
	 */
	public ITrack getTrack(int id);
	/**
	 * Binds the specified track to this vertex at the given position. The position
	 * must exist. If it does not exist, expand the vertex first.
	 * 
	 * @param id The Id of the vertex.
	 * @param track The track to bind.
	 */
	public void setTrack(int id, ITrack track);
	/**
	 * Registers new coordinates of this vertex that we wish to apply. In order
	 * to verify the correctness of our intents, we shall call {@link isUpdatePossible},
	 * and then either {@link applyUpdate} or {@link rollbackUpdate} if the new
	 * position is incorrect.
	 * 
	 * @param x
	 * @param y 
	 */
	public void registerUpdate(Segment segment, double x, double y);
	/**
	 * The implementation shall vertify the new intended position of the vertex, whether
	 * it is correct and mathematically valid for all the interested tracks. If no,
	 * this method must return false.
	 * 
	 * @return True, if the moving to the indented position is mathematically correct. 
	 */
	public boolean isUpdatePossible();
	/**
	 * Applies the update of the vertex coordinates. It also recalculates all the
	 * interested tracks.
	 */
	public void applyUpdate();
	/**
	 * Removes the information about the suggested update from the vertex.
	 */
	public void rollbackUpdate();
	
	public void markAsDeleted();
	
	public boolean isDeleted();
	/**
	 * Tangent in this point.
	 * 
	 * @param tangent 
	 */
	public void getTangent(int from, double tangent[]);
}
