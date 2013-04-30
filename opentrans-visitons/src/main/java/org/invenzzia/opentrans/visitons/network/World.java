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
package org.invenzzia.opentrans.visitons.network;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.render.AbstractCameraModelFoundation;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.painters.CurvedTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.FreeTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.StraightTrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackSnapshot;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;

/**
 * Keeps all the information about the transportation network infrastructure
 * and its geographical distribution. The world is divided into segments
 * representing the area of 1x1 km. Each vertex is assigned to one of the
 * segments and moved with it. The vertex coordinates are the combination of
 * the segment reference and the relative position within that segment, so
 * that resizing the world does not require from us updating everything.
 * 
 * <p>In addition, the object of this class keeps the counters for generating
 * the ID of tracks and vertices.
 *
 * @author Tomasz Jędrzejewski
 */
public class World {
	/**
	 * A helper enumerator to specify the vertical directions.
	 */
	public static enum VerticalDir {
		UP, DOWN;
	}

	/**
	 * A helper enumerator to specify the horizontal directions.
	 */
	public static enum HorizontalDir {
		LEFT, RIGHT;
	}
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
	 * Incrementator for generating the vertex ID.
	 */
	private long nextVertexId = 0;
	/**
	 * Incrementator for generating the track ID.
	 */
	private long nextTrackId = 0;
	/**
	 * All the vertices managed by the project.
	 */
	private Map<Long, Vertex> vertices;
	/**
	 * All the tracks managed by the project.
	 */
	private Map<Long, Track> tracks;

	/**
	 * Initializes an empty world with the dimensions 1x1.
	 */
	public World() {
		this.dimX = 1;
		this.dimY = 1;
		this.vertices = new LinkedHashMap<>();
		this.tracks = new LinkedHashMap<>();
		this.createWorld();
	}

	/**
	 * Returns the number of segments on the horizontal axis.
	 *
	 * @return
	 */
	public int getX() {
		return this.dimX;
	}

	/**
	 * Returns the number of segments on the vertical axis.
	 *
	 * @return
	 */
	public int getY() {
		return this.dimY;
	}

	/**
	 * Initializes an empty world with the dimensions provided in the arguments. The old data is lost.
	 * The method shall be used by the I/O reader.
	 *
	 * @param x Width in segments.
	 * @param y Height in segments.
	 */
	public void construct(int x, int y) {
		Preconditions.checkState(x >= 0);
		Preconditions.checkState(y >= 0);
		this.dimX = x;
		this.dimY = y;
		this.createWorld();
	}

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
	
	/**
	 * Sets the next track ID for automatic generation. The method shall be used only when reading
	 * the state from the disk.
	 * 
	 * @param nextId 
	 */
	public final void setNextTrackId(long nextId) {
		this.nextTrackId = nextId;
	}

	/**
	 * Retrieves the current value of the next track ID. The method shall be used only when
	 * writing the state to the disk.
	 * 
	 * @return Next ID value.
	 */
	public final long getNextTrackId() {
		return this.nextTrackId;
	}

	/**
	 * Sets the next vertex ID for automatic generation. The method shall be used only when reading
	 * the state from the disk.
	 * 
	 * @param nextId 
	 */
	public final void setNextVertexId(long nextId) {
		this.nextVertexId = nextId;
	}

	/**
	 * Returns the current and increments the track ID.
	 * 
	 * @return Current track ID, before the incrementation.
	 */
	public final long nextTrackId() {
		return this.nextTrackId++;
	}

	/**
	 * Returns the current and increments the vertex ID.
	 * 
	 * @return Current vertex ID, before the incrementation.
	 */
	public final long nextVertexId() {
		return this.nextVertexId++;
	}

	/**
	 * Retrieves the current value of the next vertex ID. The method shall be used only when
	 * writing the state to the disk.
	 * 
	 * @return Next ID value.
	 */
	public final long getNextVertexId() {
		return this.nextVertexId;
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
	 * Translates the absolute world coordinates into the relative <tt>(segment,x,y)</tt>
	 * used in the world data model.
	 * 
	 * @param x
	 * @param y
	 * @return 
	 */
	public SegmentCoordinate findPosition(double x, double y) {
		int sx = (int) Math.floor(x / ((double) Segment.SIZE));
		int sy = (int) Math.floor(y / ((double) Segment.SIZE));
		
		if(sx >= 0 && sx < this.dimX && sy >= 0 && sy < this.dimY) {
			x -= (sx * Segment.SIZE);
			y -= (sy * Segment.SIZE);
			return new SegmentCoordinate(this.segments[sx][sy], x, y);
		}
		throw new IllegalArgumentException("The coordinates '"+x+"', '"+y+"' are outside the world (sx: "+sx+", sy: "+sy+").");
	}

	/**
	 * Adds a vertex to the world model, and assigns an ID to it, if necessary.
	 *
	 * @param vertex The vertex to add.
	 * @return Fluent interface.
	 */
	public World addVertex(Vertex vertex) {
		if(vertex.getId() == IIdentifiable.NEUTRAL_ID) {
			vertex.setId(this.nextVertexId++);
		}
		this.vertices.put(Long.valueOf(vertex.getId()), vertex);
		return this;
	}
	
	/**
	 * Finds a vertex with the given ID.
	 * 
	 * @param id Vertex ID.
	 * @return Vertex.
	 */
	public Vertex findVertex(long id) {
		return this.vertices.get(id);
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
	 * Adds a track to the world model, and assigns the ID, if necessary.
	 * 
	 * @param track
	 * @return Fluent interface.
	 */
	public World addTrack(Track track) {
		if(track.getId() == IIdentifiable.NEUTRAL_ID) {
			track.setId(this.nextTrackId++);
		}
		this.tracks.put(Long.valueOf(track.getId()), track);
		return this;
	}
	
	/**
	 * Finds a track with the specified ID.
	 * 
	 * @param id Track ID.
	 * @return The track with this ID or null.
	 */
	public Track findTrack(long id) {
		return this.tracks.get(id);
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
	
	/**
	 * Produces the list of visible segments, using the information from the camera model. The method
	 * must be called within the model thread.
	 * 
	 * @param camera Camera information.
	 * @return List of segments visible in this camera.
	 */
	public Collection<Segment> getVisibleSegments(AbstractCameraModelFoundation camera) {
		Collection<Segment> collection = new LinkedList<>();
		int whereStartsX = (int) Math.round(Math.floor(camera.getPosX() / 1000.0));
		int whereStartsY = (int) Math.round(Math.floor(camera.getPosY() / 1000.0));
		
		int whereEndsX = (int) Math.round(Math.floor((camera.getPosX() + camera.getViewportWidth()) / 1000.0));
		int whereEndsY = (int) Math.round(Math.floor((camera.getPosY() + camera.getViewportHeight()) / 1000.0));
		
		int size = (int)((whereEndsX - whereStartsX + 1) * (whereEndsY - whereStartsY + 1));
		
		for(int x = whereStartsX; x <= whereEndsX; x++) {
			for(int y = whereStartsY; y <= whereEndsY; y++) {
				Segment s = this.findSegment(x, y);
				if(null != s) {
					collection.add(s);
				}
			}
		}
		return collection;
	}
	
	/**
	 * Exports the editable part of the world to the scene manager.
	 * 
	 * @param sm Scene manager.
	 * @param camera Camera model is needed to find the visible vertices.
	 * @param batch Are we in the batch mode?
	 */
	public void exportScene(SceneManager sm, AbstractCameraModelFoundation camera, boolean batch) {
		Collection<Segment> visibleSegments = this.getVisibleSegments(camera);
		
		int vertexNum = 0;
		for(Segment segment: visibleSegments) {
			vertexNum += segment.getVertexNum();
		}
		
		double points[] = new double[vertexNum * 2];
		long ids[] = new long[vertexNum];
		int i = 0;
		int j = 0;
		Set<Track> visibleTracks = new HashSet<>();
		for(Segment segment: visibleSegments) {
			for(Vertex vertex: segment.getVertices()) {
				ids[j++] = vertex.getId();
				points[i++] = vertex.pos().getAbsoluteX();
				points[i++] = vertex.pos().getAbsoluteY();
				visibleTracks.add(vertex.getFirstTrack());
				visibleTracks.add(vertex.getSecondTrack());
			}
		}
		CommittedTrackSnapshot snap = new CommittedTrackSnapshot(visibleTracks.size());
		snap.setVertexArray(points, ids);
		i = 0;
		for(Track track: visibleTracks) {
			switch(track.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					snap.setTrackPainter(i++, new StraightTrackPainter(track.getId(), track.getMetadata()));
					break;
				case NetworkConst.TRACK_CURVED:
					snap.setTrackPainter(i++, new CurvedTrackPainter(track.getId(), track.getMetadata()));
					break;
				case NetworkConst.TRACK_FREE:
					snap.setTrackPainter(i++, new FreeTrackPainter(track.getId(), track.getMetadata()));
					break;
			}
		}
		if(batch) {
			sm.batchUpdateResource(CommittedTrackSnapshot.class, snap);
		} else {
			sm.updateResource(CommittedTrackSnapshot.class, snap);
		}
	}
}
