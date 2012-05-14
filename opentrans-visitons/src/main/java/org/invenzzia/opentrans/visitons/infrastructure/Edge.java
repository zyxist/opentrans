/*
 * Visitons - transportation network simulation and visualization library.
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
 * Represents an edge that connects two vertices on a network.
 *
 * @author zyxist
 */
public class Edge {
	protected Vertex vertices[] = new Vertex[2];
	
	public Edge() {
	}
	
	public Edge setVertex(byte idx, Vertex vertex) {
		Preconditions.checkArgument(idx == 0 || idx == 1, "The edge end-points can have only 0 or 1 as an index.");
		this.vertices[idx] = vertex;
		return this;
	}
	
	public Vertex getVertex(byte idx) {
		Preconditions.checkArgument(idx == 0 || idx == 1, "The edge end-points can have only 0 or 1 as an index.");
		return this.vertices[idx];
	}
}

