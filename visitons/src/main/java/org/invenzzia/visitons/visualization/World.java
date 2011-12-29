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

import org.invenzzia.visitons.network.Vertex;

/**
 * This is a segment container. It keeps the world dimensions and
 * provides a centralized access for all the segments, as well as
 * performs all the necessary data updates.
 *
 * @todo Add vertex coordinate update once the segments are moved somehow.
 * @author zyxist
 */
public class World
{
	/**
	 * A helper enumerator to specify the vertical directions.
	 */
	public static enum VerticalDir
	{
		UP, DOWN;
	} // end VerticalDir;
	
	/**
	 * A helper enumerator to specify the horizontal directions.
	 */
	public static enum HorizontalDir
	{
		LEFT, RIGHT;
	} // end HorizontalDir;
	
	/**
	 * World dimension X
	 */
	protected int dimX;
	/**
	 * World dimension Y
	 */
	protected int dimY;
	/**
	 * Segment database.
	 */
	protected Segment segments[][];
	
	/**
	 * Initializes an empty world with the dimensions 1x1.
	 */
	public World()
	{
		this.dimX = 1;
		this.dimY = 1;
		this.createWorld();
	} // end World();
	
	/**
	 * Returns the number of segments on the horizontal axis.
	 * @return 
	 */
	public int getX()
	{
		return this.dimX;
	} // end getX();
	
	/**
	 * Returns the number of segments on the vertical axis.
	 * @return 
	 */
	public int getY()
	{
		return this.dimY;
	} // end getY();
	
	/**
	 * Initializes an empty world with the dimensions provided
	 * in the arguments.
	 * 
	 * @param x
	 * @param y 
	 */
	public World(int x, int y)
	{
		if(x < 1 || y < 1)
		{
			throw new IllegalArgumentException("The world size must not be negative.");
		}
		this.dimX = x;
		this.dimY = y;
		this.createWorld();
	} // end World();
	
	/**
	 * Performs the actual world initialization.
	 */
	final protected void createWorld()
	{
		this.segments = new Segment[this.dimX][this.dimY];
		for(int x = 0; x < this.dimX; x++)
		{
			for(int y = 0; y < this.dimY; y++)
			{
				this.segments[x][y] = new Segment().setPosition(x, y);
			}
		}
	} // end createWorld();
	
	public World extendHorizontally(HorizontalDir where)
	{
		Segment[][] newSegments = new Segment[dimX+1][dimY];
		if(where == HorizontalDir.LEFT)
		{
			this.copySegments(newSegments, 1, 0);
			for(int i = 0; i < dimY; i++)
			{
				newSegments[0][i] = new Segment().setPosition(0, i);
			}
		}
		else
		{
			this.copySegments(newSegments, 0, 0);
			for(int i = 0; i < dimY; i++)
			{
				newSegments[dimX][i] = new Segment().setPosition(dimX, i);
			}
		}
		this.segments = newSegments;
		this.dimX++;
		return this;
	} // end extendHorizontally();
	
	/**
	 * Extends the world vertically. The parameter indicates whether the
	 * new segments are added from the top or the bottom side.
	 * 
	 * @param where
	 * @return Fluent interface
	 */
	public World extendVertically(VerticalDir where)
	{
		Segment[][] newSegments = new Segment[dimX][dimY+1];
		if(where == VerticalDir.UP)
		{
			this.copySegments(newSegments, 0, 1);
			for(int i = 0; i < dimX; i++)
			{
				newSegments[i][0] = new Segment().setPosition(i, 0);
			}
		}
		else
		{
			this.copySegments(newSegments, 0, 0);
			for(int i = 0; i < dimX; i++)
			{
				newSegments[i][dimY] = new Segment().setPosition(i, dimY);
			}
		}
		this.segments = newSegments;
		this.dimY++;
		return this;
	} // end extendVertically();
	
	/**
	 * A helper method used to copy the contents of the segment array to a
	 * new one.
	 * 
	 * @param newArray The new array
	 * @param deltaX Optional shift in the X axis
	 * @param deltaY Optional shift in the Y axis
	 */
	protected void copySegments(Segment[][] newArray, int deltaX, int deltaY)
	{
		for(int x = 0; x < this.dimX; x++)
		{
			for(int y = 0; y < this.dimY; y++)
			{
				newArray[x+deltaX][y+deltaY] = this.segments[x][y].setPosition(x+deltaX, y+deltaY);
			}
		}
	} // end copySegments();
	
	/**
	 * Returns the segment, where the vertex belongs to.
	 * 
	 * @param vertex The network vertex.
	 * @return The owning segment.
	 */
	public Segment segmentFor(Vertex vertex)
	{
		int x = (int)Math.ceil(vertex.getX() / Segment.SIZE);
		int y = (int)Math.ceil(vertex.getY() / Segment.SIZE);
		
		return this.segments[x][y];
	} // end segmentFor();
	
	/**
	 * Adds a vertex to the world visualization, placing it in
	 * the proper segment.
	 * 
	 * @param vertex The vertex to add.
	 * @return Fluent interface.
	 */
	public World addVertex(Vertex vertex)
	{
		this.segmentFor(vertex).addVertex(vertex);
		return this;
	} // end addVertex();
	
	/**
	 * Removes the vertex from the world, updating the proper
	 * segment.
	 * 
	 * @param vertex
	 * @return 
	 */
	public World removeVertex(Vertex vertex)
	{
		return this;
	} // end removeVertex();
} // end World;

