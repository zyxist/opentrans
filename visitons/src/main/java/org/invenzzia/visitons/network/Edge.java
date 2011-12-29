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

/**
 * Represents an edge that connects two vertices on a network.
 *
 * @author zyxist
 */
public class Edge
{
	protected Vertex vertices[];
	protected ControlPoint controlPoint;
	
	public Edge()
	{
		this.vertices = new Vertex[2];
	} // end Edge();
	
	public Edge setVertex(byte idx, Vertex vertex)
	{
		if(idx < 0 || idx > 1)
		{
			throw new IllegalArgumentException("Invalid vertex index: "+Byte.toString(idx));
		}
		this.vertices[idx] = vertex;
		return this;
	} // end setVertex();
	
	public Vertex getVertex(byte idx)
	{
		if(idx < 0 || idx > 1)
		{
			throw new IllegalArgumentException("Invalid vertex index: "+Byte.toString(idx));
		}
		return this.vertices[idx];
	} // end getVertex();
	
	public boolean isStraight()
	{
		return this.controlPoint != null;
	} // end isStraight();
} // end Edge;

