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
 * One of three primitives for building tracks: a regular curve, part
 * of the arc.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CurvedTrack extends AbstractTrack<CurvedTrack> {
	
	public CurvedTrack(long id) {
		super(id);
	}
	
	@Override
	public boolean isVertexChangeAllowed(IVertex vertex, double x, double y) {
		return false;
	}
	
	@Override
	public void verticesUpdated() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public CurvedTrack fork() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void copyFrom(CurvedTrack copy) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}