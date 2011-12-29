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
package org.invenzzia.visitons.network;
import java.util.List;

/**
 * This interface handles vehicle transition between the edges entering
 * the node.
 * 
 * @author zyxist
 */
public interface ITransit
{
	/**
	 * The track objects can use this method to ask, what edges are reachable
	 * through this vertex from the given edge. It is the implementor responsibility
	 * to generate choices that make sense (i.e. not to return an edge that is
	 * unreachable on a junction).
	 * 
	 * @param edge The edge we come from
	 * @return The array of edges we can go to
	 */
	public List<Edge> whereCanWeGoFrom(Edge edge);
} // end ITransit;
