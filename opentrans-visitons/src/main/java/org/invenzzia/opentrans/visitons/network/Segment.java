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
package org.invenzzia.opentrans.visitons.network;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a single world segment, a mechanism of partitioning the space in order to speed up rendering and finding objects. A segment
 * represents a square area of 1 km^2. It contains the references to all the vertices located inside it.
 *
 * @author Tomasz Jędrzejewski
 */
public final class Segment {

	/**
	 * Segment size in meters
	 */
	public static final int SIZE = 1000;
	/**
	 * Segment position on the world map.
	 */
	private int positionX;
	/**
	 * Segment position on the world map.
	 */
	private int positionY;
	/**
	 * The path to the background bitmap.
	 */
	private String imagePath;
	/**
	 * Is the segment in the visible area of the camera?
	 */
	private boolean isDisplayed;
	/**
	 * Vertices within this segment.
	 */
	private Set<Vertex> vertices = new LinkedHashSet<>();
	
	public Segment() {
	}
	
	/**
	 * Constructor that automatically sets the segment position.
	 * 
	 * @param x
	 * @param y 
	 */
	public Segment(int x, int y) {
		this.setPosition(x, y);
	}

	/**
	 * Sets the position coordinates of the segment.
	 *
	 * @param x
	 * @param y
	 * @return Fluent interface.
	 */
	public Segment setPosition(int x, int y) {
		this.positionX = x;
		this.positionY = y;
		return this;
	}

	/**
	 * @return Returns the X-location of the segment.
	 */
	public int getX() {
		return this.positionX;
	}

	/**
	 * @return Returns the Y-location of the segment.
	 */
	public int getY() {
		return this.positionY;
	}
	
	/**
	 * @return True, if the segment contains some infrastructural data or 'false' if it is empty.
	 */
	public boolean isUsed() {
		return !this.vertices.isEmpty();
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	/**
	 * @return Path to the bitmap image.
	 */
	public String getImagePath() {
		return this.imagePath;
	}
	
	/**
	 * Adds a new vertex to the segment. This is an internal method that is not a part of the public API.
	 *
	 * @param vertex
	 * @return Fluent interface.
	 */
	void addVertex(Vertex vertex) {
		Preconditions.checkNotNull(vertex, "Attempt to add an empty vertex to the segment.");
		this.vertices.add(vertex);
	}

	/**
	 * Removes the vertex from the segment. This is an internal method that is not a part of the public API.
	 * 
	 * @param vertex 
	 */
	void removeVertex(Vertex vertex) {
		Preconditions.checkNotNull(vertex, "Attempt to add an empty vertex to the segment.");
		this.vertices.remove(vertex);
	}
	
	/**
	 * Returns the number of vertices within this segment.
	 * 
	 * @return Number of vertices.
	 */
	int getVertexNum() {
		return this.vertices.size();
	}

	/**
	 * @return Iterable for the vertices within this segment.
	 */
	Iterable<Vertex> getVertices() {
		return this.vertices;
	}
}