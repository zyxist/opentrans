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
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.data.Platform;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;
import org.invenzzia.opentrans.visitons.render.AbstractCameraModelFoundation;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.painters.CurvedTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.FreeTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.StraightTrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackObjectSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.StopSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot.SegmentInfo;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final Logger logger = LoggerFactory.getLogger(World.class);
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
	private long nextVertexId = IIdentifiable.INCREMENTATION_START;
	/**
	 * Incrementator for generating the track ID.
	 */
	private long nextTrackId = IIdentifiable.INCREMENTATION_START;
	/**
	 * All the vertices managed by the project.
	 */
	private Map<Long, Vertex> vertices;
	/**
	 * All the tracks managed by the project.
	 */
	private Map<Long, Track> tracks;
	/**
	 * Buffer for pre-loaded segment bitmaps.
	 */
	private Map<String, Image> loadedSegmentBitmaps;

	/**
	 * Initializes an empty world with the dimensions 1x1.
	 */
	public World() {
		this.dimX = 1;
		this.dimY = 1;
		this.vertices = new LinkedHashMap<>();
		this.tracks = new LinkedHashMap<>();
		this.loadedSegmentBitmaps = new LinkedHashMap<>();
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
		Preconditions.checkNotNull(vertex, "The vertex to remove cannot be empty!");
		if(vertex.getFirstTrack() != null) {
			this.removeTrack(vertex.getFirstTrack());
		}
		if(vertex.getSecondTrack() != null) {
			this.removeTrack(vertex.getSecondTrack());
		}
		this.vertices.remove(Long.valueOf(vertex.getId()));
		vertex.pos().getSegment().removeVertex(vertex);
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
	 * Removes the track from the world model.
	 * 
	 * @param track The track to remove.
	 * @return Fluent interface.
	 */
	public World removeTrack(Track track) {
		Preconditions.checkNotNull(track, "The specified track to remove must not be NULL!");
		Vertex firstVertex = track.getFirstVertex();
		Vertex secondVertex = track.getSecondVertex();
		track.removeFromVertices();
		
		if(firstVertex.hasNoTracks()) {
			this.vertices.remove(firstVertex.getId());
			firstVertex.pos().getSegment().removeVertex(firstVertex);
		}
		if(secondVertex.hasNoTracks()) {
			this.vertices.remove(secondVertex.getId());
			secondVertex.pos().getSegment().removeVertex(secondVertex);
		}
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
	
	/**
	 * Produces the list of visible segments, using the information from the camera model. The method
	 * must be called within the model thread.
	 * 
	 * @param camera Camera information.
	 * @return List of segments visible in this camera.
	 */
	public Collection<Segment> getVisibleSegments(AbstractCameraModelFoundation camera) {
		Collection<Segment> collection = new LinkedList<>();
		int whereStartsX = (int) Math.round(Math.floor(camera.getPosX() / Segment.SIZE_D));
		int whereStartsY = (int) Math.round(Math.floor(camera.getPosY() / Segment.SIZE_D));
		
		int whereEndsX = (int) Math.round(Math.floor((camera.getPosX() + camera.getViewportWidth()) / Segment.SIZE_D));
		int whereEndsY = (int) Math.round(Math.floor((camera.getPosY() + camera.getViewportHeight()) / Segment.SIZE_D));
		
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
	 * Searches for all tracks that are within the given area. The track is considered to be
	 * in the area, if it has both vertices in it.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Set of matching tracks.
	 */
	public Set<Track> findTracksInArea(double x1, double y1, double x2, double y2) {
		if(x1 > x2) {
			double tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if(y1 > y2) {
			double tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		LinkedHashSet<Track> selectedTracks = new LinkedHashSet<>();
		int whereStartsX = (int) Math.round(Math.floor(x1 / Segment.SIZE_D));
		int whereStartsY = (int) Math.round(Math.floor(y1 / Segment.SIZE_D));
		
		int whereEndsX = (int) Math.round(Math.floor(x2 / Segment.SIZE_D));
		int whereEndsY = (int) Math.round(Math.floor(y2 / Segment.SIZE_D));

		for(int x = whereStartsX; x <= whereEndsX; x++) {
			for(int y = whereStartsY; y <= whereEndsY; y++) {
				Segment s = this.findSegment(x, y);
				if(null != s) {
					for(Vertex vertex: s.getVertices()) {
						double ox = vertex.pos().getAbsoluteX();
						double oy = vertex.pos().getAbsoluteY();
						
						if(!(x1 <= ox && ox <= x2 && y1 <= oy && oy <= y2)) {
							continue;
						} else {
						}
						
						Track t1 = vertex.getFirstTrack();
						Track t2 = vertex.getSecondTrack();
						if(null != t1) {
							Vertex opposite = t1.getOppositeVertex(vertex);
							ox = opposite.pos().getAbsoluteX();
							oy = opposite.pos().getAbsoluteY();
							if(x1 <= ox && ox <= x2 && y1 <= oy && oy <= y2) {
								selectedTracks.add(t1);
							}
						}
						if(null != t2) {
							Vertex opposite = t2.getOppositeVertex(vertex);
							ox = opposite.pos().getAbsoluteX();
							oy = opposite.pos().getAbsoluteY();
							if(x1 <= ox && ox <= x2 && y1 <= oy && oy <= y2) {
								selectedTracks.add(t2);
							}
						}
					}
				}
			}
		}
		return selectedTracks;
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
		VisibleSegmentSnapshot vss = new VisibleSegmentSnapshot();
		for(Segment segment: visibleSegments) {
			// TODO: Add some smarter buffering that recovers the unused segment image memory.
			vertexNum += segment.getVertexNum();
			Image img = null;
			if(null != segment.getImagePath()) {
				img = this.loadedSegmentBitmaps.get(segment.getImagePath());
				if(null == img) {
					try {
						img = ImageIO.read(new File(segment.getImagePath()));
						this.loadedSegmentBitmaps.put(segment.getImagePath(), img);
					} catch(IOException exception) {
						logger.error("Exception occurred while loading the segment bitmap.", exception);
					}
				}
			}
			vss.addSegmentInfo(new SegmentInfo(segment, img));
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
				if(null != vertex.getFirstTrack()) {
					visibleTracks.add(vertex.getFirstTrack());
				}
				if(null != vertex.getSecondTrack()) {
					visibleTracks.add(vertex.getSecondTrack());
				}
			}
		}
		CommittedTrackSnapshot snap = new CommittedTrackSnapshot(visibleTracks.size());
		CommittedTrackObjectSnapshot trackObjectSnap = null;
		Set<Stop> stops = new HashSet<>();
		snap.setVertexArray(points, ids);
		i = 0;
		for(Track track: visibleTracks) {
			// calculate deltas for the metadata - here, they are given in the relative coordinates.
			Segment segment = track.getFirstVertex().pos().getSegment();
			double dx = track.getFirstVertex().pos().getAbsoluteX();
			double dy = track.getFirstVertex().pos().getAbsoluteY();
			switch(track.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					snap.setTrackPainter(i++, new StraightTrackPainter(track.getId(), track.getMetadata(), dx, dy));
					break;
				case NetworkConst.TRACK_CURVED:
					snap.setTrackPainter(i++, new CurvedTrackPainter(track.getId(), track.getMetadata(), dx, dy));
					break;
				case NetworkConst.TRACK_FREE:
					snap.setTrackPainter(i++, new FreeTrackPainter(track.getId(), track.getMetadata(), dx, dy));
					break;
			}
			if(track.hasTrackObjects()) {
				if(null == trackObjectSnap) {
					trackObjectSnap = new CommittedTrackObjectSnapshot();
				}
				for(TrackObject to: track.getTrackObjects()) {
					if(to.getObject() instanceof Platform) {
						stops.add(((Platform)to.getObject()).getStop());
					}
					trackObjectSnap.addTrackObject(track, to);
				}
			}
		}
		if(!batch) {
			sm.guard();
		}
		try {
			sm.batchUpdateResource(VisibleSegmentSnapshot.class, vss);
			sm.batchUpdateResource(CommittedTrackSnapshot.class, snap);
			sm.batchUpdateResource(CommittedTrackObjectSnapshot.class, trackObjectSnap);
			if(stops.size() > 0) {
				sm.batchUpdateResource(StopSnapshot.class, new StopSnapshot().addStops(stops));
			} else {
				sm.batchUpdateResource(StopSnapshot.class, null);
			}
		} finally {
			if(!batch) {
				sm.unguard();
			}
		}
	}
}
