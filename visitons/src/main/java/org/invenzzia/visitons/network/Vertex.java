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
import java.util.Arrays;
import org.invenzzia.visitons.exception.GraphException;
import org.invenzzia.utils.geometry.IMutablePoint;

/**
 * This class represents a free vertex which can be moved
 * etc. by us.
 *
 * @author zyxist
 */
public class Vertex implements IMutablePoint, ITransit
{
	/**
	 * Contains the references to edges coming out from this vertex.
	 */
	protected Edge[] edges;
	/**
	 * X coordinate in metres
	 */
	protected double x;
	/**
	 * Y coordinate in metres
	 */
	protected double y;

	/**
	 * Creates a vertex at the given coordinates.
	 * 
	 * @param edgeNum
	 * @param x
	 * @param y 
	 */
	public Vertex(int edgeNum, double x, double y)
	{
		this.edges = new Edge[edgeNum];
		this.x = x;
		this.y = y;
	} // end Vertex();
	
	public Vertex setEdge(int edgeId, Edge edge)
	{
		if(edgeId < 0 || edgeId > this.edges.length)
		{
			throw new IllegalArgumentException("The edge ID exceeds the available value.");
		}
		this.edges[edgeId] = edge;
		return this;
	} // end setEdge();
	
	public Edge getEdge(int edgeId)
	{
		if(edgeId < 0 || edgeId > this.edges.length)
		{
			throw new IllegalArgumentException("The edge ID exceeds the available value.");
		}
		return this.edges[edgeId];
	} // end getEdge();
	
	/**
	 * Increases the vertex degree.
	 * 
	 * @return Fluent interface 
	 */
	public Vertex expandVertex()
	{
		Edge newEdges[] = new Edge[this.edges.length + 1];
		System.arraycopy(this.edges, 0, newEdges, 0, this.edges.length);
		this.edges = newEdges;
		return this;
	} // end expandVertex();
	
	/**
	 * Decreases the vertex degree.
	 * 
	 * @return Fluent interface.
	 * @throws GraphException If the degree is equal 1.
	 */
	public Vertex degradeVertex() throws GraphException
	{
		if(this.edges.length < 2)
		{
			throw new GraphException("Cannot degrade a vertex of degree 1.");
		}
		Edge newEdges[] = new Edge[this.edges.length - 1];
		System.arraycopy(this.edges, 0, newEdges, 0, newEdges.length);
		this.edges = newEdges;
		return this;
	} // end degradeVertex();
	
	/**
	 * Returns the vertex degree.
	 * 
	 * @return Vertex degree. 
	 */
	public int getDegree()
	{
		return this.edges.length;
	} // end getDegree();
	

	public List<Edge> getEdges()
	{
		return Arrays.asList(this.edges);
	} // end getEdges();

	@Override
	public IMutablePoint setX(double x)
	{
		this.x = x;
		return this;
	} // end setX();

	@Override
	public IMutablePoint setY(double y)
	{
		this.y = y;
		return this;
	} // end setY();

	@Override
	public double getX()
	{
		return this.x;
	} // end getX();

	@Override
	public double getY()
	{
		return this.y;
	} // end getY();

	/**
	 * @see ITransit
	 */
	@Override
	public List<Edge> whereCanWeGoFrom(Edge edge)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end whereCanWeGoFrom();
} // end Vertex;
