/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.network.transform;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.util.Set;
import org.invenzzia.opentrans.visitons.bindings.ActualImporter;
import org.invenzzia.opentrans.visitons.geometry.ArcOps;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.geometry.Point;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.DebugPointSnapshot;

/**
 * Here we keep all the geometrical transformations of tracks and vertices
 * that can be performed on the network unit of work.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Transformations {
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * only lenghtening or shortening the straight track along the tangent line.
	 */
	public static final byte STR_MODE_LENGHTEN = 0;
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * free movement allowed, the curve is adjusted to match the tangent.
	 */
	public static final byte STR_MODE_FREE = 1;
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * free movement allowed, the curve is adjusted to match the tangent (alternative
	 * scenario).
	 */
	public static final byte STR_MODE_FREE_2 = 2;
	/**
	 * Epsilon for double value comparisons.
	 */
	private static final double EPSILON = 0.0000000001;
	/**
	 * Where do we apply the changes?
	 */
	private final NetworkUnitOfWork unitOfWork;
	/**
	 * How to import the data from the world model?
	 */
	private final IRecordImporter recordImporter;
	/**
	 * Additional information about the edited world.
	 */
	private WorldRecord world;
	/**
	 * Drawing hint lines, etc.
	 */
	private SceneManager sceneManager;
	
	@Inject
	public Transformations(NetworkUnitOfWork unitOfWork, @ActualImporter IRecordImporter recordImporter, WorldRecord worldRecord, SceneManager sceneManager) {
		this.unitOfWork = Preconditions.checkNotNull(unitOfWork);
		this.recordImporter = Preconditions.checkNotNull(recordImporter);
		this.world = Preconditions.checkNotNull(worldRecord);
		this.sceneManager = Preconditions.checkNotNull(sceneManager);
	}

	/**
	 * Updates the meta-data of the straight track. The actual algorithm depends on the selected mode.
	 * 
	 * <ul>
	 *  <li><tt>STR_MODE_LENGHTEN</tt> - the straight track is simply lenghtened. It does not affect
	 *	any other tracks, but the movement options are limited.</li>
	 *  <li><tt>STR_MODE_FREE</tt> - here the bound vertex can be freely moved, but this may require
	 *	change the position of other vertices as well.</li>
	 * </ul>
	 * 
	 * @param tr The track to update.
	 * @param boundVertex The vertex that is being moved.
	 * @param x New position of the bound vertex.
	 * @param y New position of the bound vertex.
	 * @param mode The way of adjusting the straight track.
	 */
	public boolean updateStraightTrack(TrackRecord tr, VertexRecord boundVertex, double x, double y, byte mode) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		
		Preconditions.checkNotNull(tr, "Track record cannot be empty.");
		Preconditions.checkNotNull(boundVertex, "Bound vertex cannot be empty.");
		VertexRecord v1 = tr.getFirstVertex();
		VertexRecord v2 = tr.getSecondVertex();
		if(boundVertex == v1) {
			VertexRecord tmp = v1;
			v1 = v2;
			v2 = tmp;
		} else if(boundVertex != v2) {
			throw new IllegalArgumentException("Bound vertex does not belong to the specified track.");
		}
		if(v1.hasOneTrack() && v2.hasOneTrack()) {
			v2.setPosition(x, y);
			this.calculateStraightLine(tr, v1, v2);
		} else if(boundVertex.hasOneTrack()) {
			if(mode == STR_MODE_LENGHTEN) {
				VertexRecord opposite = tr.getOppositeVertex(boundVertex);

				double buf[] = new double[8];
				LineOps.toGeneral(opposite.x(), opposite.y(), boundVertex.x(), boundVertex.y(), 0, buf);
				LineOps.toOrthogonal(0, 3, buf, x, y);
				LineOps.intersection(0, 3, 6, buf);
				boundVertex.setPosition(buf[6], buf[7]);
				tr.setMetadata(new double[] { v1.x(), v1.y(), v2.x(), v2.y() } );
			} else {
				TrackRecord previousTrack = v1.getOppositeTrack(tr);
				switch(previousTrack.getType()) {
					case NetworkConst.TRACK_STRAIGHT:
						return false;
					case NetworkConst.TRACK_CURVED:
						v2.setPosition(x, y);
						this.adjustJoiningVertexOnCircle(v2, v1, previousTrack.getOppositeVertex(v1));
						break;
					case NetworkConst.TRACK_FREE:
						v2.setPosition(x, y);
						this.calculateStraightLine(tr, v1, v2);
						this.calculateFreeCurve(previousTrack, v1, previousTrack.getOppositeVertex(v1));
						break;
				}
			}
		}
		return true;
	}
	
	/**
	 * Updates the metadata of the curved track.
	 * 
	 * @param tr
	 * @param boundVertex The vertex that is being moved.
	 * @param x
	 * @param y 
	 * @return False, if the update cannot be made for some reason.
	 */
	public boolean updateCurvedTrack(TrackRecord tr, VertexRecord boundVertex, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		VertexRecord v1 = tr.getFirstVertex();
		VertexRecord v2 = tr.getSecondVertex();
		if(boundVertex == v1) {
			VertexRecord tmp = v1;
			v1 = v2;
			v2 = tmp;
		} else if(boundVertex != v2) {
			throw new IllegalArgumentException("Bound vertex does not belong to the specified track.");
		}
		if(v1.hasOneTrack()) {
			return false;
		}
		
		v2.setPosition(x, y);
		this.calculateCurve(tr, v1, v2);
		return true;
	}
	
	/**
	 * Creates a straight track between the two points. This generally works well for
	 * 0-degree points, but for the rest of the cases, the two points must satisfy
	 * certain conditions in order to succeed.
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	public boolean createStraightTrack(VertexRecord v1, VertexRecord v2) {
		Preconditions.checkState(!v1.hasAllTracks(), "Cannot create a straight track to a full vertex.");
		Preconditions.checkState(!v2.hasAllTracks(), "Cannot create a straight track to a full vertex.");
		this.recordImporter.importAllMissingNeighbors(this.unitOfWork, v1, v2);
		if(v1.hasNoTracks() && v2.hasNoTracks()) {
			TrackRecord track = new TrackRecord();
			track.setFreeVertex(v1);
			track.setFreeVertex(v2);
			track.setType(NetworkConst.TRACK_STRAIGHT);
			v1.addTrack(track);
			v2.addTrack(track);
			
			this.calculateStraightLine(track, v1, v2);
			this.unitOfWork.addTrack(track);
		} else if(v1.hasOneTrack() && v2.hasOneTrack()) {
			if(Math.abs(v1.tangent() - v2.tangent()) > EPSILON) {
				// It's impossible - not a chance to lie in the same line.
				return false;
			}
			TrackRecord track = new TrackRecord();
			track.setFreeVertex(v1);
			track.setFreeVertex(v2);
			track.setType(NetworkConst.TRACK_STRAIGHT);
			v1.addTrack(track);
			v2.addTrack(track);
			this.calculateStraightLine(track, v1, v2);
			this.unitOfWork.addTrack(track);
		} else {
			if(v1.hasNoTracks()) {
				VertexRecord temp = v2;
				v2 = v1;
				v1 = temp;
			}
			TrackRecord track = new TrackRecord();
			track.setFreeVertex(v1);
			track.setFreeVertex(v2);
			track.setType(NetworkConst.TRACK_STRAIGHT);
			v1.addTrack(track);
			v2.addTrack(track);
			v2.setTangent(v1.tangent());
			
			double buf[] = new double[8];
			LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
			LineOps.toOrthogonal(0, 3, buf, v2.x(), v2.y());
			LineOps.intersection(0, 3, 6, buf);
			v2.setPosition(buf[6], buf[7]);
			track.setMetadata(new double[] { v1.x(), v1.y(), v2.x(), v2.y() } );
			this.unitOfWork.addTrack(track);
		}
		
		return false;
	}
	
	/**
	 * Creates a curved track between the two points. 
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	public boolean createCurvedTrack(VertexRecord v1, VertexRecord v2) {
		this.recordImporter.importAllMissingNeighbors(this.unitOfWork, v1, v2);
		if(v1.hasOneTrack() && v2.hasOneTrack()) {
			TrackRecord track = v2.getTrackTo(v1);
			switch(track.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					this.createCurvedToStraigtConnection(track, v1, v2, null);
					break;
				case NetworkConst.TRACK_CURVED:
					break;
				case NetworkConst.TRACK_FREE:
					break;
			}
		} else if(v1.hasOneTrack() && v2.hasNoTracks()) {
			TrackRecord tr = new TrackRecord();
			tr.setType(NetworkConst.TRACK_CURVED);
			tr.setFreeVertex(v1);
			tr.setFreeVertex(v2);
			v1.addTrack(tr);
			v2.addTrack(tr);
			
			this.calculateCurve(tr, v1, v2);
			this.unitOfWork.addTrack(tr);
		}
		
		return false;
	}
	
	/**
	 * Connects two tracks with a double curve, which is a universal way
	 * of connecting them in case we can't produce a single curve.
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	public boolean createFreeTrack(VertexRecord v1, VertexRecord v2) {
		TrackRecord tr = new TrackRecord();
		tr.setFreeVertex(v1);
		tr.setFreeVertex(v2);
		v1.addTrack(tr);
		v2.addTrack(tr);
		tr.setType(NetworkConst.TRACK_FREE);
		this.calculateFreeCurve(tr, v1, v2);
		this.unitOfWork.addTrack(tr);
		return true;
	}
	
	/**
	 * Connects two vertices with a curve or double curve. Each of the vertices must
	 * be connected to exactly one track.
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	public boolean connectTwoVertices(VertexRecord v1, VertexRecord v2) {
		Preconditions.checkState(v1.hasOneTrack());
		Preconditions.checkState(v2.hasOneTrack());
		this.recordImporter.importAllMissingNeighbors(this.unitOfWork, v1, v2);
		
		TrackRecord ct = v1.getTrack();
		if(ct.getType() != NetworkConst.TRACK_STRAIGHT) {
			return false;
		}
		
		TrackRecord track = v2.getTrack();
		switch(track.getType()) {
			case NetworkConst.TRACK_STRAIGHT:
				this.createCurvedToStraigtConnection(track, v1, v2, null);
				return true;
			case NetworkConst.TRACK_CURVED:
				break;
			case NetworkConst.TRACK_FREE:
				break;
		}
		return false;
	}
	
	/**
	 * Moves a set of tracks by the given delta. All the connected tracks that are not a subject
	 * of the movement are either adjusted, or changed into a double curve. Before making an actual
	 * movement, the new positions of each affected vertex are checked against world boundaries - 
	 * we cannot move anything outside them!
	 * 
	 * @param tracks Set of tracks to move.
	 * @param deltaX Movement delta: X
	 * @param deltaY Movement delta: Y
	 * @return False, if at least one of the affected vertices is outside the world.
	 */
	public boolean moveTracksByDelta(Set<TrackRecord> tracks, double deltaX, double deltaY) {
		return false;
	}
	
	/**
	 * Moves a single vertex by the given delta. This is the dispositor method, which checks the state
	 * of the vertex being moved and delegates the actual task to other transformation methods. If the
	 * method returns <strong>false</strong>, no data are updated.
	 * 
	 * @param vertex Vertex to move.
	 * @param deltaX New position X
	 * @param deltaY New position Y
	 * @return True, if the movement is possible.
	 */
	public boolean moveVertexToPosition(VertexRecord vertex, double posX, double posY, byte mode) {
		this.recordImporter.importAllMissingNeighbors(this.unitOfWork, vertex);
		if(vertex.hasNoTracks()) {
			throw new IllegalStateException("Vertex with no tracks exists on the map!");
		} else if(vertex.hasOneTrack()) {
			// This is simple.
			TrackRecord tr = vertex.getTrack();
			this.recordImporter.importAllMissingNeighbors(this.unitOfWork, tr.getOppositeVertex(vertex));
			switch(tr.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					return this.updateStraightTrack(tr, vertex, posX, posY, mode);
				case NetworkConst.TRACK_CURVED:
					return this.updateCurvedTrack(tr, vertex, posX, posY);
				case NetworkConst.TRACK_FREE:
					if(!this.world.isWithinWorld(posX, posY)) {
						return false;
					}
					vertex.setPosition(posX, posY);
					this.calculateFreeCurve(tr, vertex, tr.getOppositeVertex(vertex));
					return true;
			}
		} else {
			// This is a hardcore. Prepare for a mind ride! We might affect up to 4 tracks here!
			// In order not to spam the model thread (which the application probably uses), we
			// create a smarter record importer which is not so stupid to import just these vertices
			// that we request. By 'smart' we mean that it analyzes the type of the imported tracks
			// and can decide, whether to import more or not, just to satisfy the code below. And
			// everything in one, atomic model thread operation.
			// In other words, we delegate part of the logic, which should be here, somewhere else, and
			// this is not elegant according to some guys, including me :).
			// Hmmm... I could use multiline comments here.
			
			this.recordImporter.importMissingNeighboursSmarter(this.unitOfWork, vertex);
			
			// Now, which one of you, tracks, is straight?
			TrackRecord straightTrack;
			TrackRecord curvedTrack;
			if(vertex.getFirstTrack().getType() == NetworkConst.TRACK_STRAIGHT) {
				straightTrack = vertex.getFirstTrack();
				curvedTrack = vertex.getSecondTrack();
			} else {
				straightTrack = vertex.getSecondTrack();
				curvedTrack = vertex.getFirstTrack();
			}
			VertexRecord curvedTrVert2 = curvedTrack.getOppositeVertex(vertex);
			VertexRecord straightTrVert2 = straightTrack.getOppositeVertex(vertex);
			if(curvedTrVert2.hasAllTracks()) {
				if(mode == Transformations.STR_MODE_LENGHTEN) {
					if(curvedTrack.getType() == NetworkConst.TRACK_CURVED) {
						return this.moveIntVertexLenghteningFullCurvedTrack(curvedTrack, vertex, curvedTrVert2, posX, posY);
					} else {
						return this.moveIntVertexLenghteningFreeCurvedTrack(curvedTrack, straightTrack, vertex, curvedTrVert2, posX, posY);
					}
				} else if(straightTrVert2.hasAllTracks()) {
					if(mode == Transformations.STR_MODE_FREE && curvedTrack.getType() == NetworkConst.TRACK_CURVED) {
						// Most complex case: 4 tracks affected.
						return this.moveIntVertexMostComplexCase(vertex, straightTrack, curvedTrack, posX, posY);
					} else {
						// In this scenario we allow switching between curved track types.
						return this.moveIntVertexMostComplexCaseAllowFreeTracks(vertex, straightTrack, curvedTrack, posX, posY);
					}
				} else {
					// A bit simpler, but still complex: 3 tracks affected.
					return this.moveIntVertexSimplerVersionOfPreviousWithoutSecondCurvedTrack(vertex, straightTrack, curvedTrack, posX, posY);
				}
			} else {
				if(mode == Transformations.STR_MODE_LENGHTEN) {
					if(curvedTrack.getType() == NetworkConst.TRACK_CURVED) {
						return this.moveIntVertexLenghteningPartialCurvedTrack(curvedTrack, straightTrack, vertex, curvedTrVert2, posX, posY);
					} else {
						
					}
				} else if(straightTrVert2.hasAllTracks()) {
					return this.moveIntVertexAdjustingSecondCurve(vertex, straightTrack, curvedTrack, posX, posY);
				} else {
					return this.moveIntVertexOnlyStraightAndCurvedTrack(vertex, straightTrack, curvedTrack, posX, posY);
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * First case of moving the internal vertex: a lenghtening a straight track, which affects the straight
	 * track on the opposite side of the curve. Both tracks are lenghtened by the same distance, changing the
	 * curve radius. The curve centre moves along a imaginary straight line.
	 * 
	 * @param curvedTrack Affected curved track.
	 * @param v1 The vertex being moved.
	 * @param v2 The affected vertex on the opposite side of the curve.
	 * @param x New position of v1: X
	 * @param y New position of v1: Y
	 */
	private boolean moveIntVertexLenghteningFullCurvedTrack(TrackRecord curvedTrack, VertexRecord v1, VertexRecord v2, double x, double y) {
		TrackRecord closerStraightTrack = v1.getOppositeTrack(curvedTrack);
		TrackRecord furtherStraightTrack = v2.getOppositeTrack(curvedTrack);
		VertexRecord clStVert2 = closerStraightTrack.getOppositeVertex(v1);
		
		double buf[] = new double[8];
		double mov = LineOps.vectorLengtheningDistance(clStVert2.x(), clStVert2.y(), v1.x(), v1.y(), x, y, 0, buf);
		if(furtherStraightTrack.computeLength() + mov < 0.0 || closerStraightTrack.computeLength() + mov < 0.0) {
			// Impossible!
			return false;
		}
		VertexRecord ftStVert2 = furtherStraightTrack.getOppositeVertex(v2);
		LineOps.lenghtenVector(ftStVert2.x(), ftStVert2.y(), v2.x(), v2.y(), mov, 0, buf);
		
		if(!this.world.isWithinWorld(buf[0], buf[1]) || !this.world.isWithinWorld(buf[6], buf[7])) {
			return false;
		}
		v2.setPosition(buf[0], buf[1]);
		v1.setPosition(buf[6], buf[7]);

	
		this.calculateStraightLine(furtherStraightTrack, furtherStraightTrack.getFirstVertex(), furtherStraightTrack.getSecondVertex());
		this.calculateStraightLine(closerStraightTrack, closerStraightTrack.getFirstVertex(), closerStraightTrack.getSecondVertex());
		this.createCurvedToStraigtConnection(furtherStraightTrack, v1, v2, curvedTrack);
		return true;
	}
	
	/**
	 * Alternative case of the method above: when the curved track is a free (double curve).
	 * 
	 * @param curvedTrack Affected doubly curved track.
	 * @param v1 The vertex being moved.
	 * @param v2 The affected vertex on the opposite side of the curve.
	 * @param x New position of v1: X
	 * @param y New position of v1: Y
	 * @return True, if the movement is possible.
	 */
	private boolean moveIntVertexLenghteningFreeCurvedTrack(TrackRecord curvedTrack, TrackRecord straightTrack, VertexRecord v1, VertexRecord v2, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		
		double buf[] = new double[8];
		VertexRecord opposite = straightTrack.getOppositeVertex(v1);
		LineOps.toGeneral(opposite.x(), opposite.y(), v1.x(), v1.y(), 0, buf);
		LineOps.toOrthogonal(0, 3, buf, x, y);
		LineOps.intersection(0, 3, 6, buf);
		v1.setPosition(buf[6], buf[7]);
		straightTrack.setMetadata(new double[] { v1.x(), v1.y(), opposite.x(), opposite.y() } );
		this.calculateFreeCurve(curvedTrack, v1, v2);
		return true;
	}
	
	/**
	 * The most complex vertex movement case: 4 tracks are affected at a time. This a combination of 
	 * {@link #adjustJoiningVertexOnCircle()} and {@link #moveIntVertexLenghteningFullCurvedTrack()}.
	 * 
	 * @return True, if the operation is possible.
	 */
	private boolean moveIntVertexMostComplexCase(VertexRecord v1, TrackRecord closerStraightTrack, TrackRecord closerCurvedTrack, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		v1.setPosition(x, y);
		
		VertexRecord v2 = closerStraightTrack.getOppositeVertex(v1);
		TrackRecord furtherStraightTrack = closerCurvedTrack.getOppositeVertex(v1).getOppositeTrack(closerCurvedTrack);
		this.adjustJoiningVertexOnCircle(v1, v2, v2.getOppositeTrack(closerStraightTrack).getOppositeVertex(v2));
		this.createCurvedToStraigtConnection(furtherStraightTrack, v1, closerCurvedTrack.getOppositeVertex(v1), closerCurvedTrack);
		// Most complex case... and most shortest code! :D
		// Unfortunately... some refactoring will be needed, if we want to disallow certain invalid situations.
		return true;
	}
	
	/**
	 * This is the simpler version of the previous variant, where the further curved track does not exist, so instead adjusting
	 * it with {@link #adjustJoiningVertexOnCircle()}, we can simply calculate the straight track.
	 * 
	 * @param v1
	 * @param closerStraightTrack
	 * @param closerCurvedTrack
	 * @param x
	 * @param y
	 * @return True, if the operation is possible.
	 */
	private boolean moveIntVertexSimplerVersionOfPreviousWithoutSecondCurvedTrack(VertexRecord v1, TrackRecord closerStraightTrack, TrackRecord closerCurvedTrack, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		v1.setPosition(x, y);
		
		VertexRecord v2 = closerStraightTrack.getOppositeVertex(v1);
		this.calculateStraightLine(closerStraightTrack, v1, v2);
		if(closerCurvedTrack.getType() == NetworkConst.TRACK_CURVED) {
			TrackRecord furtherStraightTrack = closerCurvedTrack.getOppositeVertex(v1).getOppositeTrack(closerCurvedTrack);
			this.createCurvedToStraigtConnection(furtherStraightTrack, v1, closerCurvedTrack.getOppositeVertex(v1), closerCurvedTrack);
		} else {
			this.calculateFreeCurve(closerCurvedTrack, closerCurvedTrack.getFirstVertex(), closerCurvedTrack.getSecondVertex());
		}
		return true;
	}
	
	/**
	 * Alternative version of {@link #moveIntVertexMostComplexCase()}, where the curved track can be free track. Here
	 * we can affect only three tracks, but we allow switching between a doubly-curved and single-curved track.
	 * 
	 * @param v1
	 * @param closerStraightTrack
	 * @param curvedTrack
	 * @param x
	 * @param y
	 * @return 
	 */
	private boolean moveIntVertexMostComplexCaseAllowFreeTracks(VertexRecord v1, TrackRecord closerStraightTrack, TrackRecord curvedTrack, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		v1.setPosition(x, y);
		
		VertexRecord v2 = closerStraightTrack.getOppositeVertex(v1);
		VertexRecord v3 = curvedTrack.getOppositeVertex(v1);
		this.adjustJoiningVertexOnCircle(v1, v2, v2.getOppositeTrack(closerStraightTrack).getOppositeVertex(v2));
	//	if(this.useSingleCurve(v1, v3)) {
	//		curvedTrack.setType(NetworkConst.TRACK_CURVED);
	//		this.calculateCurve(curvedTrack, v1, v2);
	//	} else {
			curvedTrack.setType(NetworkConst.TRACK_FREE);
			this.calculateFreeCurve(curvedTrack, v1, v3);
	//	}
		
		return true;
	}
	
	/**
	 * Similar to {@link #moveIntVertexLenghteningFullCurvedTrack()}, but the modified curved track is not connected
	 * to anything on the opposite side.
	 * 
	 * @param curvedTrack Affected curved track.
	 * @param v1 The vertex being moved.
	 * @param v2 The affected vertex on the opposite side of the curve.
	 * @param x New position of v1: X
	 * @param y New position of v1: Y
	 * @return True, if the operation is possible.
	 */
	private boolean moveIntVertexLenghteningPartialCurvedTrack(TrackRecord curvedTrack, TrackRecord straightTrack, VertexRecord v1, VertexRecord v2, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		
		double buf[] = new double[8];
		VertexRecord opposite = straightTrack.getOppositeVertex(v1);
		LineOps.toGeneral(opposite.x(), opposite.y(), v1.x(), v1.y(), 0, buf);
		LineOps.toOrthogonal(0, 3, buf, x, y);
		LineOps.intersection(0, 3, 6, buf);
		v1.setPosition(buf[6], buf[7]);
		straightTrack.setMetadata(new double[] { v1.x(), v1.y(), opposite.x(), opposite.y() } );
		this.calculateCurve(curvedTrack, v1, v2);
		
		return true;
	}
	
	/**
	 * Similar to {@link #moveIntVertexMostComplexCase()}, but without a track on the opposite side of the curved
	 * track.
	 * 
	 * @param v1 Vertex being moved.
	 * @param closerStraightTrack
	 * @param closerCurvedTrack
	 * @param x New position of v1: X
	 * @param y New position of v1: Y
	 * @return True, if the operation is possible.
	 */
	private boolean moveIntVertexAdjustingSecondCurve(VertexRecord v1, TrackRecord closerStraightTrack, TrackRecord closerCurvedTrack, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		v1.setPosition(x, y);
		
		VertexRecord v2 = closerStraightTrack.getOppositeVertex(v1);
		VertexRecord v3 = closerCurvedTrack.getOppositeVertex(v1);
		TrackRecord furtherStraightTrack = closerCurvedTrack.getOppositeVertex(v1).getOppositeTrack(closerCurvedTrack);
		this.adjustJoiningVertexOnCircle(v1, v2, v2.getOppositeTrack(closerStraightTrack).getOppositeVertex(v2));
		this.calculateCurve(closerCurvedTrack, v1, v3);
		
		return true;
	}
	
	/**
	 * Similar to {@link #moveIntVertexMostComplexCase()}, but without a track on the opposite side of the curved
	 * track.
	 * 
	 * @param v1 Vertex being moved.
	 * @param straightTrack
	 * @param curvedTrack
	 * @param x New position of v1: X
	 * @param y New position of v1: Y
	 * @return True, if the operation is possible.
	 */
	private boolean moveIntVertexOnlyStraightAndCurvedTrack(VertexRecord v1, TrackRecord straightTrack, TrackRecord curvedTrack, double x, double y) {
		if(!this.world.isWithinWorld(x, y)) {
			return false;
		}
		v1.setPosition(x, y);
		
		this.calculateStraightLine(straightTrack, v1, straightTrack.getOppositeVertex(v1));
		this.calculateCurve(curvedTrack, v1, curvedTrack.getOppositeVertex(v1));
		
		return true;
	}
	
	/**
	 * This operation shall be applied to three vertices connected by:
	 * 
	 * <ul>
	 *  <li>(v1, v2) - straight line</li>
	 *  <li>(v2, v3) - curved line</li>
	 * </ul>
	 * 
	 * We assume that v1 has been freely moved and we must adjust the location of v2 vertex
	 * to match the constraints of both straight line and a curved line. We do this by putting
	 * v2 and v3 on a virtual circle, with the middle in the intersection point of (v3 tangent line,
	 * v1 tangent line) and move v2 along this circle.
	 * 
	 * @param v1 Moved vertex connected to a straight line.
	 * @param v2 Adjusted vertex connecting the curve and the straight line.
	 * @param v3 Stationary point that begins the curve.
	 */
	private void adjustJoiningVertexOnCircle(VertexRecord v1, VertexRecord v2, VertexRecord v3) {
		double buf[] = new double[12];
		TrackRecord curve = v2.getTrackTo(v3);
		TrackRecord straight = v2.getTrackTo(v1);
		Preconditions.checkState(curve.getType() == NetworkConst.TRACK_CURVED, "(v2,v3) do not create a curve.");
		Preconditions.checkState(straight.getType() == NetworkConst.TRACK_STRAIGHT, "(v1,v2) do not create a straight line.");
		
		LineOps.toGeneral(v2.x(), v2.y(), v2.tangent(), 0, buf);
		LineOps.toGeneral(v3.x(), v3.y(), v3.tangent(), 3, buf);
		LineOps.intersection(0, 3, 6, buf);
		
		double t = LineOps.getTangent(buf[6], buf[7], v1.x(), v1.y());
		double r = LineOps.distance(buf[6], buf[7], v3.x(), v3.y());

		v2.setPosition(
			buf[6] + r * Math.cos(t),
			buf[7] + r * Math.sin(t)
		);
		
		this.calculateStraightLine(straight, v1, v2);
		this.prepareCurveFreeMovement(v3, v2.x(), v2.y(), 0, buf);
		this.findCurveDirection(curve, v2, v3, buf[0], buf[1]);
	}
	
	/**
	 * Calculates the parameters and metadata of the given straight track. Forces the tangents
	 * in the two vertices to match the straight tracks. Neighbouring tracks must be adjusted
	 * to these values.
	 * 
	 * @param tr Modified track record.
	 * @param v1 First vertex.
	 * @param v2 Second vertex.
	 */
	private void calculateStraightLine(TrackRecord tr, VertexRecord v1, VertexRecord v2) {
		double tangent = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
		v1.setTangent(tangent);
		v2.setTangent(tangent);
		tr.setMetadata(new double[] { v1.x(), v1.y(), v2.x(), v2.y() } );
	}
	
	/**
	 * Finds the single curve that connects the two vertices. Although in general case constructing
	 * of such a curve is not always possible, here we assume that we can modify the tangent of the
	 * second vertex, so this operation always succeeds. After applying this method, the opposite
	 * track connected to <tt>v2</tt> should be adjusted to match the new tangent, if necessary.
	 * 
	 * @param tr
	 * @param v1
	 * @param v2 
	 */
	private void calculateCurve(TrackRecord tr, VertexRecord v1, VertexRecord v2) {
		double buf[] = new double[3];
		this.prepareCurveFreeMovement(v1, v2.x(), v2.y(), 0, buf);
		v2.setTangent(buf[2]);
		this.findCurveDirection(tr, v1, v2, buf[0], buf[1]);
	}
	
	/**
	 * Match the curve to the new positions of two vertices, without changing the direction.
	 * 
	 * @param tr
	 * @param v1
	 * @param v2 
	 */
	private void refreshCurve(TrackRecord tr) {
		VertexRecord v1 = tr.getFirstVertex();
		VertexRecord v2 = tr.getSecondVertex();
		
		double buf[] = new double[8];
		LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
		LineOps.toGeneral(v2.x(), v2.y(), v2.tangent(), 3, buf);
		LineOps.toOrthogonal(0, buf, v1.x(), v1.y());
		LineOps.toOrthogonal(3, buf, v1.x(), v1.y());
		LineOps.intersection(0, 3, 6, buf);
		
		tr.setMetadata(this.prepareCurveMetadata(v1.x(), v1.y(), v2.x(), v2.y(), buf[6], buf[7], 0, null));
	}
	
	/**
	 * Performs the calculations that find the parameters of the free (doubly-curved) track
	 * that matches the tangents in vertices v1 and v2. The calculations do not change the
	 * vertex tangents.
	 * 
	 * @param tr Updated track record.
	 * @param v1 First vertex
	 * @param v2 Second vertex.
	 */
	private void calculateFreeCurve(TrackRecord tr, VertexRecord v1, VertexRecord v2) {
		Preconditions.checkArgument(tr.getType() == NetworkConst.TRACK_FREE, "Invalid track type: TRACK_FREE expected.");
		double metadata[] = new double[24];
		if(Math.abs(v1.tangent() - v2.tangent()) < EPSILON) {
			// If the lines are parallel, we need a special handling.
			double buf[] = new double[22];
			LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
			LineOps.toGeneral(v2.x(), v2.y(), v2.tangent(), 3, buf);
			LineOps.toOrthogonal(0, 6, buf, v1.x(), v1.y());
			LineOps.toOrthogonal(3, 9, buf, v2.x(), v2.y());
			
			LineOps.middlePoint(v1.x(), v1.y(), v2.x(), v2.y(), 13, buf); // I'm G: 13
			LineOps.toParallel(0, 15, buf, buf[13], buf[14]);
			LineOps.intersection(15, 6, 18, buf);
			LineOps.intersection(15, 9, 20, buf);
			
			this.prepareCurveMetadata(buf[13], buf[14], v1.x(), v1.y(), buf[18], buf[19], 0, metadata);
			this.prepareCurveMetadata(buf[13], buf[14], v2.x(), v2.y(), buf[20], buf[21], 12, metadata);
		} else {
			double buf[] = new double[60];
			// Find points E and F
			LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
			LineOps.toGeneral(v2.x(), v2.y(), v2.tangent(), 3, buf);
			LineOps.toOrthogonal(0, 6, buf, v1.x(), v1.y());
			LineOps.toOrthogonal(3, 9, buf, v2.x(), v2.y());
			LineOps.intersection(0, 3, 12, buf); // I'm E: 12 (generals)
			LineOps.intersection(6, 9, 14, buf); // I'm F: 14 (orthogonals)

			// Find G point
			LineOps.middlePoint(v1.x(), v1.y(), v2.x(), v2.y(), 16, buf); // I'm G: 16

			// Find angle bisections in point E
			LineOps.angleBisector(0, 3, 18, 21, buf); // bisections in point E

			// Find I and H point
			LineOps.toOrthogonal(v1.x(), v1.y(), buf[16], buf[17], 24, buf); // this is 'h' line (blue)
			LineOps.intersection(21, 24, 27, buf); // I'm I: 27 - this is the middle of a circle.
			LineOps.intersection(18, 24, 30, buf); // I'm H: 30 - this is the middle of a circle.
			buf[33] = v1.x();
			buf[34] = v1.y();
			buf[35] = v2.x();
			buf[36] = v2.y();

			// Wir haben diese Circlen
			ArcOps.circleThroughPoint(27, 33, 29, buf);
			ArcOps.circleThroughPoint(30, 35, 32, buf);

			// Find intersection of these circles with the blue line
			ArcOps.circleLineIntersection(27, 24, 35, buf);
			ArcOps.circleLineIntersection(30, 24, 39, buf);

			// Choose one of these intersections.
			LineOps.reorder(buf, 35, 37, 39, 41);
			this.sceneManager.updateResource(DebugPointSnapshot.class, new DebugPointSnapshot(
				new double[] { buf[35], buf[36], buf[37], buf[38], buf[39], buf[40], buf[41], buf[42],  }
			));
			int selectedPt = (v1.y() > v2.y() ? (v1.x() > v2.x() ? 39 : 37) : (v1.x() > v2.x() ? 37 : 39));
			LineOps.middlePoint(v1.x(), v1.y(), selectedPt, 41, buf);	// K
			LineOps.middlePoint(v2.x(), v2.y(), selectedPt, 43, buf);	// L

			LineOps.toOrthogonal(v1.x(), v1.y(), 41, 50, buf);
			LineOps.toOrthogonal(v2.x(), v2.y(), 43, 53, buf);
			LineOps.intersection(6, 50, 56, buf); // M point - center of the first arc
			LineOps.intersection(9, 53, 58, buf); // N point - center of the second arc
		

			if(LineOps.onWhichSide(v1.x(), v1.y(), v1.tangent(), buf[selectedPt], buf[selectedPt + 1]) == (v1.x() > v2.x() ? 1 : -1)) {
				this.prepareCurveMetadata(buf[selectedPt], buf[selectedPt+1], v1.x(), v1.y(), buf[56], buf[57], 0, metadata);
			} else {
				this.prepareCurveMetadata(v1.x(), v1.y(), buf[selectedPt], buf[selectedPt+1], buf[56], buf[57], 0, metadata);
			}
			if(LineOps.onWhichSide(v2.x(), v2.y(), v2.tangent(), buf[selectedPt], buf[selectedPt + 1]) == (v1.x() > v2.x() ? 1 : -1)) {
				this.prepareCurveMetadata(v2.x(), v2.y(), buf[selectedPt], buf[selectedPt+1], buf[58], buf[59], 12, metadata);
				
			} else {
				this.prepareCurveMetadata(buf[selectedPt], buf[selectedPt+1], v2.x(), v2.y(), buf[58], buf[59], 12, metadata);
			}
		}
		tr.setMetadata(metadata);
	}
	
	/**
	 * Creates a curve between two vertices. The second of them must be connected to a straight
	 * track and its location may be adjusted to match the drawn curve constraints. If the last
	 * argument is null, a new track is constructed. Otherwise, we update the parameters of that
	 * track.
	 * 
	 * @param track Track connected to vertex V3
	 * @param v1 First vertex (stationary)
	 * @param v3 Second vertex (can be adjusted)
	 */
	private boolean createCurvedToStraigtConnection(TrackRecord track, VertexRecord v1, VertexRecord v3, TrackRecord curvedTrack) {
		VertexRecord v4 = track.getOppositeVertex(v3);
		VertexRecord v2;
		if(null == curvedTrack) {
			v2 = ((TrackRecord) v1.getTrack()).getOppositeVertex(v1);
		} else {
			v2 = v1.getOppositeTrack(curvedTrack).getOppositeVertex(v1);
		}
		double buf[] = new double[12];
		// First line: Dx + Ey + F = 0
		LineOps.toGeneral(v3.x(), v3.y(), v4.x(), v4.y(), 0, buf);
		// Second line - we only need the orthogonal: A2x + B2y + C2 = 0
		LineOps.toGeneral(v1.x(), v1.y(), v2.x(), v2.y(), 3, buf);
	//	LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 3, buf);
		
		// Their angle bisector: Mx + Ny + P = 0
		double p = Math.sqrt(Math.pow(buf[0], 2) + Math.pow(buf[1], 2));
		double q = Math.sqrt(Math.pow(buf[3], 2) + Math.pow(buf[4], 2));
		buf[6] = (buf[0] * q + buf[3] * p);
		buf[7] = (buf[1] * q + buf[4] * p);
		buf[8] = (buf[2] * q + buf[5] * p);
		
		if(Math.signum(buf[6] * v1.x() + buf[7] * v1.y() + buf[8]) == Math.signum(buf[6] * v3.x() + buf[7] * v3.y() + buf[8])) {
			buf[6] = (buf[0] * q - buf[3] * p);
			buf[7] = (buf[1] * q - buf[4] * p);
			buf[8] = (buf[2] * q - buf[5] * p);
		}
		
		// Generate the orthogonal of the second line.
		LineOps.toOrthogonal(3, buf, v1.x(), v1.y());
		// Last part - the partial vector of the second orthogonal: A1x + B1y + ? = 0
		buf[9] = -buf[1];
		buf[10] = buf[0];
		
		// Looking for points: X1 - the new point on the first line (new coordinates of v3)
		// X2 - the center of the curve, lies on the angle bisector.
		/*
		 * Equations:
		 * A1x1 + B1y1 = A1x2 + B1y2 (1)
		 * A2x2 + B2y2 + C2 = 0
		 * Dx1 + Ey1 + F = 0
		 * Mx2 + Ny2 + P = 0
		 */
		double x1, y1, x2, y2;
		
		x2 = (buf[4] * buf[8] / buf[7] - buf[5]) / (buf[3] - buf[4] * buf[6] / buf[7]);
		y2 = - ((buf[6] * x2 + buf[8]) / buf[7]);
		
		// temporary variable to solve the right part of (1)
		buf[11] = buf[9] * x2 + buf[10] * y2;
		x1 = (buf[11] + buf[10] * buf[2] / buf[1]) / (buf[9] - buf[10] * buf[0] / buf[1]);
		y1 = - ((buf[0] * x1 + buf[2]) / buf[1]);
		
		if(!this.world.isWithinWorld(x1, y1)) {
			return false;
		}
		
		v3.setPosition(x1, y1);
		if(null == curvedTrack) {
			TrackRecord tr = new TrackRecord();
			tr.setFreeVertex(v1);
			tr.setFreeVertex(v3);
			v1.addTrack(tr);
			v3.addTrack(tr);
			tr.setType(NetworkConst.TRACK_CURVED);
			track.setMetadata(new double[] { v3.x(), v3.y(), v4.x(), v4.y() });
			this.findCurveDirection(tr, v1, v3, x2, y2);
			this.unitOfWork.addTrack(tr);
		} else {
			track.setMetadata(new double[] { v3.x(), v3.y(), v4.x(), v4.y() });
			this.findCurveDirection(curvedTrack, v1, v3, x2, y2);
		}
		return true;
	}
	
	/**
	 * Prepares the metadata for the curves. The points are: first vertex, second vertex, the center
	 * of the arc. The arc is always drawn from the first vertex to the second vertex. If the buffer
	 * is not provided, it is automatically created with 12 slots. 8 are occupied by the results of
	 * this methods, and the next 4 are for the control points, that must be calculated with a separate
	 * method.
	 * 
	 * @param x1 X coordinate of the first point on a circle.
	 * @param y1 Y coordinate of the first point on a circle.
	 * @param x2 X coordinate of the second point on a circle.
	 * @param y2 Y coordinate of the second point on a circle.
	 * @param x3 Circle centre: X
	 * @param y3 Circle centre: Y
	 * @return Arc metadata
	 */
	private double[] prepareCurveMetadata(double x1, double y1, double x2, double y2, double x3, double y3, int from, double buf[]) {
		if(null == buf) {
			buf = new double[12];
			from = 0;
		}
		double angle1 = -Math.atan2(y1 - y3, x1 - x3);
		if(angle1 < 0.0) {
			angle1 += 2* Math.PI;
		}
		double angle2 = -Math.atan2(y2 - y3, x2 - x3);
		if(angle2 < 0.0) {
			angle2 += 2* Math.PI;
		}
		double diff;
		if(angle1 < angle2) {
			diff = angle2 - angle1;
		} else {
			diff = angle2 + (2 * Math.PI - angle1);
		}
		double radius = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
		
		buf[from] = x3 - radius;
		buf[from+1] = y3 - radius;
		buf[from+2] = 2 * radius;
		buf[from+3] = 2 * radius;
		buf[from+4] = Math.toDegrees(angle1);
		buf[from+5] = Math.toDegrees(diff);
		buf[from+6] = x3;
		buf[from+7] = y3;
		return buf;
	}
	
	/**
	 * These calculations are used for drawing curve, where one of the vertices has only one track connected:
	 * the curve we are currently drawing. In this case, we must apply a bit different transformation to search
	 * the curve centre point.
	 * 
	 * @param x1 First point
	 * @param y1 First point
	 * @param x2 Second point, that is free.
	 * @param y2 Second point, that is free.
	 * @param to
	 * @param buf 
	 */
	private void prepareCurveFreeMovement(VertexRecord v1, double x2, double y2, int to, double buf[]) {
		double tmp[] = new double[5];
		LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, tmp);
		LineOps.toOrthogonal(0, tmp, v1.x(), v1.y());
		double A2 = 2 * (v1.x() - x2);
		double B2 = 2 * (v1.y() - y2);
		double C2 = -(Math.pow(v1.x(), 2) - Math.pow(x2, 2) + Math.pow(v1.y(), 2) - Math.pow(y2, 2));
		tmp[3] = buf[to] = (tmp[1] * C2 - tmp[2] * B2) / (B2 * tmp[0] - tmp[1] * A2);
		tmp[4] = buf[to+1] = - ((tmp[2] + tmp[0] * buf[to]) / tmp[1]);
		// Find tangent
		buf[to+2] = ArcOps.getTangent(3, 0, tmp, x2, y2);
	}
	
	/**
	 * Calculates the arc control point for the given vertex position. This is necessary for proper orientation
	 * calculations.
	 * 
	 * @param x1 Point on a circle.
	 * @param y1 Point on a circle.
	 * @param tangent Tangent in that point.
	 * @param x3 Circle centre.
	 * @param y3 Circle centre.
	 * @param to Where to store output values?
	 * @param buf Output data buffer.
	 */
	private void prepareCurveControlPoint(double x1, double y1, double tangent, double x2, double y2, double x3, double y3, int to, double buf[]) {
		double angle1 = -Math.atan2(y1 - y3, x1 - x3);
		if(angle1 < 0.0) {
			angle1 += 2* Math.PI;
		}
		double angle2 = -Math.atan2(y2 - y3, x2 - x3);
		if(angle2 < 0.0) {
			angle2 += 2* Math.PI;
		}
		double diff;
		if(angle1 < angle2) {
			diff = -0.01;
		} else {
			diff = 0.01;
		}
		double tmp[] = new double[8];
		LineOps.toGeneral(x1, y1, tangent, 0, tmp);
		LineOps.toGeneral(x3, y3, Math.toRadians(angle1+diff), 3, tmp);
		LineOps.intersection(0, 3, 6, tmp);
		buf[to] = tmp[6];
		buf[to+1] = tmp[7];
	}
	
	/**
	 * To perform proper calculations, we often need to know the direction, which the given track
	 * comes to the vertex from. We use a horizontal line as a marker that delimits the orientation
	 * of the track relative to one of its vertex.
	 *  
	 * @param tr Track to analyze
	 * @param distinguisher One of the vertices of this track.
	 * @return True, if the track comes to the vertex from the bottom.
	 */
	private boolean orientationOf(TrackRecord tr, VertexRecord distinguisher) {
		switch(tr.getType()) {
			case NetworkConst.TRACK_STRAIGHT:
				return distinguisher.y() < tr.getOppositeVertex(distinguisher).y();
			case NetworkConst.TRACK_CURVED:
				return false;
			case NetworkConst.TRACK_FREE:
				return false;
		}
		throw new IllegalArgumentException("Invalid track type: "+tr.getType());
	}
	
	/**
	 * Determines whether two vertices can be connected by a single curve and
	 * (optional) adjusting the position of one of the vertices, or by a double
	 * curve.
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	private boolean useSingleCurve(VertexRecord v1, VertexRecord v2) {
		double sl1 = Math.tan(v1.tangent());
		double sl2 = Math.tan(v2.tangent());
		double sl3;
		
		double tangentBetweenLines;
		double tangentBetweenPositions;
		if(Math.abs(v1.x() - v2.x()) < EPSILON) {
			sl3 = Double.POSITIVE_INFINITY;
		} else {
			sl3 = (v2.y() - v1.y()) / (v2.x() - v1.x());
		}
		if(sl1 == Double.POSITIVE_INFINITY) {
			if(sl2 == Double.POSITIVE_INFINITY) {
				tangentBetweenLines = Math.PI / 2.0;
			} else {
				tangentBetweenLines = Math.atan(Math.abs(1 / sl2));
			}
			if(sl3 == Double.POSITIVE_INFINITY) {
				tangentBetweenPositions = Math.PI / 2.0;
			} else {
				tangentBetweenPositions = Math.atan(Math.abs(1 / sl3));
			}
		} else {
			if(sl2 == Double.POSITIVE_INFINITY) {
				tangentBetweenLines = Math.atan(Math.abs(1 / sl1));
			} else {
				tangentBetweenLines = Math.atan(
					Math.abs(
						(sl1 - sl2) /
						(1.0 + sl1 * sl2)
					)
				);
			}
			if(sl3 == Double.POSITIVE_INFINITY) {
				tangentBetweenPositions = Math.atan(Math.abs(1 / sl1));
			} else {
				tangentBetweenPositions = Math.atan(
					Math.abs(
						(sl1 - sl3) /
						(1.0 + sl1 * sl3)
					)
				);
			}
		}
		double diff = tangentBetweenLines - tangentBetweenPositions;
		if(diff > 0.0) {
			return true;
		}
		return false;
	}

	/**
	 * Applies the proper direction to the curve.
	 * 
	 * @param tr The curved track.
	 * @param v1 First vertex connected to this track.
	 * @param v2 Second vertex connected to this track.
	 * @param cx Center of the arc: X
	 * @param cy Center of the arc: Y
	 * @return Direction: negative if tracks are replaced.
	 */
	private int findCurveDirection(TrackRecord tr, VertexRecord v1, VertexRecord v2, double cx, double cy) {
		Point c = v1.getOppositeTrack(tr).controlPoint(v1);
		if(LineOps.onWhichSide(c.x(), c.y(), v1.x(), v1.y(), v2.x(), v2.y()) < 0) {
			double metadata[] = this.prepareCurveMetadata(v1.x(), v1.y(), v2.x(), v2.y(), cx, cy, 0, null);
			this.prepareCurveControlPoint(v1.x(), v1.y(), v1.tangent(), v2.x(), v2.y(), cx, cy, 8, metadata);
			this.prepareCurveControlPoint(v2.x(), v2.y(), v2.tangent(), v1.x(), v1.y(), cx, cy, 10, metadata);
			tr.setMetadata(metadata);
			return 1;
		} else {
			double metadata[] = this.prepareCurveMetadata(v2.x(), v2.y(), v1.x(), v1.y(), cx, cy, 0, null);
			this.prepareCurveControlPoint(v1.x(), v1.y(), v1.tangent(), v2.x(), v2.y(), cx, cy, 10, metadata);
			this.prepareCurveControlPoint(v2.x(), v2.y(), v2.tangent(), v1.x(), v1.y(), cx, cy, 8, metadata);
			tr.setMetadata(metadata);
			return -1;
		}
	}
}
