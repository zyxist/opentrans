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

/**
 * Common interface for track object primitives.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ITrack<T extends ITrack> extends ICopiable<T> {
	/**
	 * @return Unique track ID.
	 */
	public long getId();
	/**
	 * Sets the unique track ID. This method shall be used by the {@link Graph} internal code.
	 * @param id 
	 */
	public void setId(long id);
	
	public IVertex getVertex(int id);
	
	public void setVertex(int id, IVertex vertex);
	/**
	 * Returns the opposite vertex to the specified one.
	 * 
	 * @param vertex The vertex we have.
	 * @return The opposite vertex this track is bound to.
	 */
	public IVertex getOppositeVertex(IVertex vertex);
	
	public boolean isVertexChangeAllowed(IVertex vertex, double x, double y);
	/**
	 * Notification from the vertex that its coordinates have been changed. It is guaranteed that the new vertex coordinates
	 * have been passed to {@link isVertexChangeAllowed} method, so that the track can vote against the change.
	 */
	public void verticesUpdated();
	/**
	 * Notification from the vertex that we cannot change the coordinates. It is guaranteed that the new vertex coordinates
	 * have been passed to {@link isVertexChangeAllowed} method, so that the track can vote against the change.
	 */
	public void verticesNotUpdated();
	
	public void markAsDeleted();
	
	public boolean isDeleted();
	
	public void getTangentInVertex(int vertex, int from, double tan[]);
}
