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
package org.invenzzia.opentrans.visitons.world;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.infrastructure.Vertex;

/**
 * Description here.
 *
 * @author Tomasz Jędrzejewski
 */
public class World {

	/**
	 * A helper enumerator to specify the vertical directions.
	 */
	public static enum VerticalDir {

		UP, DOWN;
	} // end VerticalDir;

	/**
	 * A helper enumerator to specify the horizontal directions.
	 */
	public static enum HorizontalDir {

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
	 * The iterator for creating unique vertex names.
	 */
	protected long vertexIterator;
	/**
	 * The iterator for creating unique edge names.
	 */
	protected long edgeIterator;

	/**
	 * Initializes an empty world with the dimensions 1x1.
	 */
	public World() {
		this.dimX = 1;
		this.dimY = 1;
		this.createWorld();
		this.setIterators(0, 0);
	} // end World();

	/**
	 * Returns the number of segments on the horizontal axis.
	 *
	 * @return
	 */
	public int getX() {
		return this.dimX;
	} // end getX();

	/**
	 * Returns the number of segments on the vertical axis.
	 *
	 * @return
	 */
	public int getY() {
		return this.dimY;
	} // end getY();

	/**
	 * Populates the iterators that are used to give the vertices and edges the unique names.
	 *
	 * @param vertexIterator The value of the vertex iterator.
	 * @param edgeIterator The value of the edge iterator.
	 */
	public void setIterators(long vertexIterator, long edgeIterator) {
		this.vertexIterator = vertexIterator;
		this.edgeIterator = edgeIterator;
	} // end setIterators();

	/**
	 * Initializes an empty world with the dimensions provided in the arguments. The old data is lost.
	 *
	 * @param x
	 * @param y
	 */
	public void construct(int x, int y) {
		Preconditions.checkState(x >= 0);
		Preconditions.checkState(y >= 0);
		this.dimX = x;
		this.dimY = y;
		this.createWorld();
	} // end construct();

	/**
	 * Performs the actual world initialization.
	 */
	final protected void createWorld() {
		this.segments = new Segment[this.dimX][this.dimY];
		for(int x = 0; x < this.dimX; x++) {
			for(int y = 0; y < this.dimY; y++) {
				this.segments[x][y] = new Segment().setPosition(x, y);
			}
		}
	}

	public World extendHorizontally(HorizontalDir where) {
		Segment[][] newSegments = new Segment[dimX + 1][dimY];
		if(where == HorizontalDir.LEFT) {
			this.copySegmentsExt(newSegments, 1, 0);
			for(int i = 0; i < dimY; i++) {
				newSegments[0][i] = new Segment().setPosition(0, i);
			}
		} else {
			this.copySegmentsExt(newSegments, 0, 0);
			for(int i = 0; i < dimY; i++) {
				newSegments[dimX][i] = new Segment().setPosition(dimX, i);
			}
		}
		this.segments = newSegments;
		this.dimX++;
		return this;
	}

	/**
	 * Extends the world vertically. The parameter indicates whether the new segments are added from the top or the bottom side.
	 *
	 * @param where
	 * @return Fluent interface
	 */
	public World extendVertically(VerticalDir where) {
		Segment[][] newSegments = new Segment[dimX][dimY + 1];
		if(where == VerticalDir.UP) {
			this.copySegmentsExt(newSegments, 0, 1);
			for(int i = 0; i < dimX; i++) {
				newSegments[i][0] = new Segment().setPosition(i, 0);
			}
		} else {
			this.copySegmentsExt(newSegments, 0, 0);
			for(int i = 0; i < dimX; i++) {
				newSegments[i][dimY] = new Segment().setPosition(i, dimY);
			}
		}
		this.segments = newSegments;
		this.dimY++;
		return this;
	}
	
	/**
	 * Shrinks the world. The conditions for performing this operation are:
	 * 
	 * <ul>
	 *  <li>the world does not have a horizontal size equal to 1.</li>
	 *  <li>all segments from the reduced side are unused.</li>
	 * </ul>
	 * 
	 * @param where Exact resizing direction.
	 * @return Fluent interface.
	 * @throws WorldException If the conditions are not met.
	 */
	public World shrinkHorizontally(HorizontalDir where) throws WorldException {
		if(this.dimX < 2) {
			throw new WorldException("The minimal horizontal world size has been reached.", this);
		}
		int y;
		int offset;
		if(HorizontalDir.LEFT == where) {
			y = 0;
			offset = 1;
		} else {
			y = this.dimY - 1;
			offset = 0;
		}
		for(int i = 0; i < this.dimX; i++) {
			if(this.segments[i][y].isUsed()) {
				throw new WorldException("There are segments in use in the reduced side of the world.", this);
			}
		}
		// OK, now actual resize
		Segment[][] newSegments = new Segment[dimX - 1][dimY];
		this.copySegmentsShr(newSegments, offset, 0, dimX - 1, dimY);
		this.segments = newSegments;
		this.dimX--;
		return this;
	}
	
	/**
	 * Shrinks the world. The conditions for performing this operation are:
	 * 
	 * <ul>
	 *  <li>the world does not have a vertical size equal to 1.</li>
	 *  <li>all segments from the reduced side are unused.</li>
	 * </ul>
	 * 
	 * @param where Exact resizing direction.
	 * @return Fluent interface.
	 * @throws WorldException If the conditions are not met.
	 */
	public World shrinkVertically(VerticalDir where) throws WorldException {
		if(this.dimY < 2) {
			throw new WorldException("The minimal horizontal world size has been reached.", this);
		}
		int x;
		int offset;
		if(VerticalDir.UP == where) {
			x = 0;
			offset = 1;
		} else {
			x = this.dimX - 1;
			offset = 0;
		}
		for(int i = 0; i < this.dimY; i++) {
			if(this.segments[x][i].isUsed()) {
				throw new WorldException("There are segments in use in the reduced side of the world.", this);
			}
		}
		// OK, now actual resize
		Segment[][] newSegments = new Segment[dimX][dimY - 1];
		this.copySegmentsShr(newSegments, 0, offset, dimX, dimY - 1);
		this.segments = newSegments;
		this.dimY--;
		return this;
	}

	/**
	 * A helper method used to copy the contents of the segment array to a new one.
	 *
	 * @param newArray The new array
	 * @param deltaX Optional shift in the X axis
	 * @param deltaY Optional shift in the Y axis
	 */
	protected void copySegmentsExt(Segment[][] newArray, int deltaX, int deltaY) {
		for(int x = 0; x < this.dimX; x++) {
			for(int y = 0; y < this.dimY; y++) {
				int dx = x + deltaX;
				int dy = y + deltaY;
				newArray[dx][dy] = this.segments[x][y].setPosition(dx, dy);
			}
		}
	}
	
	/**
	 * A helper method used to copy the contents of the segment array to a new one.
	 *
	 * @param newArray The new array
	 * @param deltaX Optional shift in the X axis
	 * @param deltaY Optional shift in the Y axis
	 */
	protected void copySegmentsShr(Segment[][] newArray, int deltaX, int deltaY, int newX, int newY) {
		for(int x = 0; x < newX; x++) {
			for(int y = 0; y < newY; y++) {
				int dx = x + deltaX;
				int dy = y + deltaY;
				newArray[x][y] = this.segments[dx][dy].setPosition(x, y);
			}
		}
	}
	
	/**
	 * Returns the segment object with the given coordinates or NULL, if the segment does not
	 * exist.
	 * 
	 * @param x
	 * @param y
	 * @return Segment object or NULL
	 */
	public Segment findSegment(int x, int y) {
		if(x >= 0 && x < this.dimX && y >= 0 && y < this.dimY) {
			return this.segments[x][y];
		}
		return null;
	}

	/**
	 * Returns the segment, where the vertex belongs to.
	 *
	 * @param vertex The network vertex.
	 * @return The owning segment.
	 */
	public Segment segmentFor(Vertex vertex) {
		int x = (int) Math.ceil(vertex.getX() / Segment.SIZE);
		int y = (int) Math.ceil(vertex.getY() / Segment.SIZE);

		return this.segments[x][y];
	}

	/**
	 * Adds a vertex to the world visualization, placing it in the proper segment.
	 *
	 * @param vertex The vertex to add.
	 * @return Fluent interface.
	 */
	public World addVertex(Vertex vertex) {
		this.segmentFor(vertex).addVertex(vertex);
		return this;
	}

	/**
	 * Removes the vertex from the world, updating the proper segment.
	 *
	 * @param vertex
	 * @return Fluent interface.
	 */
	public World removeVertex(Vertex vertex) {
		return this;
	}
	
	/**
	 * Prepares a map of segment usage. Each cell in the returned array represents
	 * a segment. The 'true' flag indicates that the segment contains some visible
	 * infrastructure data, and 'false' that the segment is unused.
	 * 
	 * @return Segment usage map.
	 */
	public boolean[][] exportSegmentUsage() {
		boolean map[][] = new boolean[this.dimX][this.dimY];
		
		for(int i = 0; i < this.dimX; i++) {
			for(int j = 0; j < this.dimY; j++) {
				map[i][j] = this.segments[i][j].isUsed();
			}
		}
		
		return map;
	}
} // end World;
