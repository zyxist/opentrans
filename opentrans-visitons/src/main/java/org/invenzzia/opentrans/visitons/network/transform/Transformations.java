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
import org.invenzzia.opentrans.visitons.geometry.ArcOps;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.geometry.Point;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;

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
	 * Epsilon for double value comparisons.
	 */
	private static final double EPSILON = 0.0000000001;
	/**
	 * Where do we apply the changes?
	 */
	private final NetworkUnitOfWork unitOfWork;
	
	public Transformations(NetworkUnitOfWork unitOfWork) {
		this.unitOfWork = Preconditions.checkNotNull(unitOfWork);
	}
	
	/**
	 * Updates the meta-data of the straight track.
	 * @param tr 
	 */
	public boolean updateStraightTrack(TrackRecord tr, VertexRecord boundVertex, double x, double y, byte mode) {
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
						this.calculateFreeCurve(tr, v1, v2);
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
	 * @param boundVertex
	 * @param x
	 * @param y 
	 */
	public void updateCurvedTrack(TrackRecord tr, VertexRecord boundVertex, double x, double y) {
		VertexRecord v1 = tr.getFirstVertex();
		VertexRecord v2 = tr.getSecondVertex();
		if(boundVertex == v1) {
			VertexRecord tmp = v1;
			v1 = v2;
			v2 = tmp;
		} else if(boundVertex != v2) {
			throw new IllegalArgumentException("Bound vertex does not belong to the specified track.");
		}
		
		v2.setPosition(x, y);
		this.calculateCurve(tr, v1, v2);
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
		if(v1.hasOneTrack() && v2.hasOneTrack()) {
			this.unitOfWork.importAllMissingNeighbors(v2);
			TrackRecord track = v2.getTrackTo(v1);
			switch(track.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					this.createCurvedToStraigtConnection(track, v1, v2);
					break;
				case NetworkConst.TRACK_CURVED:
					break;
				case NetworkConst.TRACK_FREE:
					break;
			}
		} else if(v1.hasOneTrack() && v2.hasNoTracks()) {
			this.unitOfWork.importAllMissingNeighbors(v1);
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
		this.findCurveDirection(curve, v2, v3, buf);
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
		this.findCurveDirection(tr, v1, v2, buf);
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
			int selectedPt = 37;	// temporarily hardcoded - TODO
			LineOps.middlePoint(v1.x(), v1.y(), selectedPt, 41, buf);	// K
			LineOps.middlePoint(v2.x(), v2.y(), selectedPt, 43, buf);	// L

			LineOps.toOrthogonal(v1.x(), v1.y(), 41, 50, buf);
			LineOps.toOrthogonal(v2.x(), v2.y(), 43, 53, buf);
			LineOps.intersection(6, 50, 56, buf); // M point - center of the first arc
			LineOps.intersection(9, 53, 58, buf); // N point - center of the second arc

			
			this.prepareCurveMetadata(buf[selectedPt], buf[selectedPt+1], v1.x(), v1.y(), buf[56], buf[57], 0, metadata);
			this.prepareCurveMetadata(buf[selectedPt], buf[selectedPt+1], v2.x(), v2.y(), buf[58], buf[59], 12, metadata);
		}
		tr.setMetadata(metadata);
	}
	
	private void createCurvedToStraigtConnection(TrackRecord track, VertexRecord v1, VertexRecord v3) {
		VertexRecord v4 = track.getOppositeVertex(v3);
		VertexRecord v2 = ((TrackRecord) v1.getTrack()).getOppositeVertex(v1);
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
		
		v3.setPosition(x1, y1);
		TrackRecord tr = new TrackRecord();
		tr.setFreeVertex(v1);
		tr.setFreeVertex(v3);
		v1.addTrack(tr);
		v3.addTrack(tr);
		tr.setType(NetworkConst.TRACK_CURVED);
		track.setMetadata(new double[] { v3.x(), v3.y(), v4.x(), v4.y() });
		if(this.orientationOf(track, v4)) {
			tr.setMetadata(this.prepareCurveMetadata(x1, y1, v1.x(), v1.y(), x2, y2, 0, null));
		} else {
			tr.setMetadata(this.prepareCurveMetadata(v1.x(), v1.y(), x1, y1, x2, y2, 0, null));
		}
		
		this.unitOfWork.addTrack(tr);
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

	private void findCurveDirection(TrackRecord tr, VertexRecord v1, VertexRecord v2, double[] buf) {
		Point c = v1.getOppositeTrack(tr).controlPoint(v1);
		if(LineOps.onWhichSide(c.x(), c.y(), v1.x(), v1.y(), v2.x(), v2.y()) < 0) {
			double metadata[] = this.prepareCurveMetadata(v1.x(), v1.y(), v2.x(), v2.y(), buf[0], buf[1], 0, null);
			this.prepareCurveControlPoint(v1.x(), v1.y(), v1.tangent(), v2.x(), v2.y(), buf[0], buf[1], 8, metadata);
			this.prepareCurveControlPoint(v2.x(), v2.y(), v2.tangent(), v1.x(), v1.y(), buf[0], buf[1], 10, metadata);
			tr.setMetadata(metadata);
		} else {
			double metadata[] = this.prepareCurveMetadata(v2.x(), v2.y(), v1.x(), v1.y(), buf[0], buf[1], 0, null);
			this.prepareCurveControlPoint(v1.x(), v1.y(), v1.tangent(), v2.x(), v2.y(), buf[0], buf[1], 10, metadata);
			this.prepareCurveControlPoint(v2.x(), v2.y(), v2.tangent(), v1.x(), v1.y(), buf[0], buf[1], 8, metadata);
			tr.setMetadata(metadata);
		}
	}
}
