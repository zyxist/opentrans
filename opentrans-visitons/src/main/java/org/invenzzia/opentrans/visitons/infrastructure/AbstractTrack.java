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

import com.google.common.base.Preconditions;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractTrack<T extends ITrack> implements ITrack<T> {
	private long id;
	
	private boolean deleted = false;
	
	protected IVertex vertices[];
	
	public AbstractTrack(long id) {
		this.id = id;
		this.vertices = new IVertex[2];
	}
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public void markAsDeleted() {
		this.deleted = true;
	}
	
	@Override
	public boolean isDeleted() {
		return this.deleted;
	}
	
	@Override
	public IVertex getVertex(int id) {
		Preconditions.checkArgument(0 == id || 1 == id, "The vertex ID in the track can be either 0 or 1.");
		return this.vertices[id];
	}
	
	@Override
	public IVertex getOppositeVertex(IVertex vertex) {
		if(vertex == this.vertices[0]) {
			return this.vertices[1];
		}
		return this.vertices[0];
	}

	@Override
	public void setVertex(int id, IVertex vertex) {
		Preconditions.checkArgument(0 == id || 1 == id, "The vertex ID in the track can be either 0 or 1.");
		this.vertices[id] = Preconditions.checkNotNull(vertex, "Attempt to register a NULL vertex in the edge.");
	}
	
	@Override
	public void verticesNotUpdated() {
	}
}
