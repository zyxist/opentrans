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
import org.invenzzia.opentrans.visitons.world.Segment;

/**
 * Used for editable graph logic. Cannot be edited, wraps actual vertex
 * in the editable graph.
 * 
 * @author Tomasz Jędrzejewski
 */
public class GhostVertex implements IVertex<GhostVertex> {
	private IVertex delegate;
	
	public GhostVertex(IVertex vertex) {
		this.delegate = Preconditions.checkNotNull(vertex);
	}

	@Override
	public long getId() {
		return this.delegate.getId();
	}
	
	public IVertex getDelegate() {
		return this.delegate;
	}
	
	@Override
	public void setId(long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void registerUpdate(Segment segment, double x, double y) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isUpdatePossible() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void applyUpdate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void rollbackUpdate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void markAsDeleted() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isDeleted() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public GhostVertex fork() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void copyFrom(GhostVertex copy) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public void expand(int by) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTrackCount() {
		return this.delegate.getTrackCount();
	}

	@Override
	public ITrack[] getTracks() {
		return this.delegate.getTracks();
	}

	@Override
	public void setTrack(int id, ITrack track) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double x() {
		return this.delegate.x();
	}

	@Override
	public double y() {
		return this.delegate.y();
	}

	@Override
	public Segment getSegment() {
		return this.delegate.getSegment();
	}
	
	@Override
	public void getTangent(int from, double tangent[]) {
		this.delegate.getTangent(from, tangent);
	}
}
