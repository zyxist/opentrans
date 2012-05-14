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
import com.vividsolutions.jts.math.Vector2D;
import java.util.Arrays;
import java.util.List;
import org.invenzzia.opentrans.visitons.exception.GraphException;

/**
 * This class represents a free vertex which can be moved etc. by us.
 *
 * @author zyxist
 */
public class Vertex implements ITransit {

	/**
	 * Contains the references to edges coming out from this vertex.
	 */
	protected Edge[] edges;
	/**
	 * Location of the vertex.
	 */
	protected Vector2D vector;

	/**
	 * Creates a vertex at the given coordinates.
	 *
	 * @param edgeNum
	 * @param x
	 * @param y
	 */
	public Vertex(int edgeNum, double x, double y) {
		this.edges = new Edge[edgeNum];
		this.vector = new Vector2D(x, y);
	}

	/**
	 * Binds an edge to the vertex under the given index. The index must not exceed the vertex degree.
	 *
	 * @param edgeId The edge index.
	 * @param edge The edge to bind.
	 * @return Fluent interface.
	 */
	public Vertex setEdge(int edgeId, Edge edge) {
		Preconditions.checkElementIndex(edgeId, this.edges.length);
		this.edges[edgeId] = edge;
		return this;
	}

	/**
	 * Returns the given edge connected to this vertex.
	 *
	 * @param edgeId The edge number [0, vertex degree)
	 * @return The edge object
	 */
	public Edge getEdge(int edgeId) {
		Preconditions.checkElementIndex(edgeId, this.edges.length);
		return this.edges[edgeId];
	}

	/**
	 * Increases the vertex degree.
	 *
	 * @return Fluent interface
	 */
	public Vertex expandVertex() {
		Edge newEdges[] = new Edge[this.edges.length + 1];
		System.arraycopy(this.edges, 0, newEdges, 0, this.edges.length);
		this.edges = newEdges;
		return this;
	}

	/**
	 * Decreases the vertex degree.
	 *
	 * @return Fluent interface.
	 * @throws GraphException If the degree is equal 1.
	 */
	public Vertex degradeVertex() throws GraphException {
		if(this.edges.length < 2) {
			throw new GraphException("Cannot degrade a vertex of degree 1.");
		}
		Edge newEdges[] = new Edge[this.edges.length - 1];
		System.arraycopy(this.edges, 0, newEdges, 0, newEdges.length);
		this.edges = newEdges;
		return this;
	}

	/**
	 * Returns the vertex degree.
	 *
	 * @return Vertex degree.
	 */
	public int getDegree() {
		return this.edges.length;
	}

	public List<Edge> getEdges() {
		return Arrays.asList(this.edges);
	}

	public Vertex setVector(Vector2D vector) {
		this.vector = vector;
		return this;
	}

	public double getX() {
		return this.vector.getX();
	}

	public double getY() {
		return this.vector.getY();
	}
	
	public Vector2D getVector() {
		return this.vector;
	}

	/**
	 * @see ITransit
	 */
	@Override
	public List<Edge> whereCanWeGoFrom(Edge edge) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
