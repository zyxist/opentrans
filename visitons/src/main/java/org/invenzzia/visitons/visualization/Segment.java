/*
 * Visitons - transportation network simulation and visualization library.
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.invenzzia.visitons.visualization;

import java.awt.Image;
import org.invenzzia.visitons.network.Vertex;

/**
 * Represents a single world segment, a mechanism of partitioning the space
 * in order to speed up rendering and finding objects. A segment represents
 * a square area of 1 km^2. It contains the references to all the vertices
 * located inside it.
 * 
 * @author zyxist
 */
public class Segment
{
	/**
	 * Segment size in meters
	 */
	public static final int SIZE = 1000;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Segment position on the world map.
	 */
	private int positionX;
	/**
	 * Segment position on the world map.
	 */
	private int positionY;
	
	/**
	 * Stores a background bitmap, but only if the image is already displayed.
	 */
	private Image image;
	/**
	 * The path to the image.
	 */
	private String imagePath;
	
	/**
	 * Is the segment in the visible area of the camera?
	 */
	private boolean isDisplayed;
	
	/**
	 * Sets the position coordinates of the segment.
	 * @param x
	 * @param y
	 * @return Fluent interface.
	 */
	public Segment setPosition(int x, int y)
	{
		this.positionX = x;
		this.positionY = y;
		return this;
	} // end setPosition();
	
	/**
	 * @return Returns the X-location of the segment.
	 */
	public int getX()
	{
		return this.positionX;
	} // end getX();
	
	/**
	 * @return Returns the Y-location of the segment. 
	 */
	public int getY()
	{
		return this.positionY;
	} // end getY();
	
	/**
	 * Adds a new vertex to the segment. Avoid using this method
	 * in favour of @link{World.addVertex}.
	 * 
	 * @param vertex
	 * @return Fluent interface.
	 */
	public Segment addVertex(Vertex vertex)
	{
		return this;
	} // end addVertex();
} // end Segment;