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
import org.invenzzia.opentrans.visitons.geometry.LineOps;
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
	private static final double EPSILON = 0.0000000001;
	private final NetworkUnitOfWork unitOfWork;
	
	public Transformations(NetworkUnitOfWork unitOfWork) {
		this.unitOfWork = Preconditions.checkNotNull(unitOfWork);
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
		if(v1.getTrackNum() == 0 && v2.getTrackNum() == 0) {
			TrackRecord track = new TrackRecord();
			track.setFreeVertex(v1);
			track.setFreeVertex(v2);
			track.setType(NetworkConst.TRACK_STRAIGHT);
			v1.addTrack(track);
			v2.addTrack(track);
			
			double tangent = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
			
			v1.setTangent(tangent);
			v2.setTangent(tangent);
			
			track.setMetadata(new double[] { v1.x(), v1.y(), v2.x(), v2.y() } );
			this.unitOfWork.addTrack(track);
		} else {
			if(v1.getTrackNum() == 0) {
				VertexRecord temp = v2;
				v2 = v1;
				v1 = temp;
			} else if(v2.getTrackNum() == 0) {
				// It's okay.
			} else {
				if(Math.abs(v1.tangent() - v2.tangent()) > EPSILON) {
					// It's impossible - not a chance to lie in the same line.
					return false;
				}
			}
			double lineTangent = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
			if(Math.abs(v1.tangent() - lineTangent) > EPSILON) {
				return false;
			}
			
			TrackRecord track = new TrackRecord();
			track.setFreeVertex(v1);
			track.setFreeVertex(v2);
			track.setType(NetworkConst.TRACK_STRAIGHT);
			v1.addTrack(track);
			v2.addTrack(track);
			
			v1.setTangent(lineTangent);
			v2.setTangent(lineTangent);
			
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
		if(v1.getTrackNum() != 0 && v2.getTrackNum() != 0) {
			if(v2.getTrackNum() != 1) {
				// We can't move the more complex vertices right now.
				return false;
			}
			this.unitOfWork.importAllMissingNeighbors(v2);
			TrackRecord track = (TrackRecord) v2.getTrack(0);
			switch(track.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					this.createCurvedToStraigtConnection(v1, v2, track);
					break;
				case NetworkConst.TRACK_CURVED:
					break;
				case NetworkConst.TRACK_FREE:
					break;
			}
		}
		
		return false;
	}
	
	public boolean createFreeTrack(VertexRecord v1, VertexRecord v2) {
		return false;
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
	 * @param v1
	 * @param v2
	 * @param v3 
	 */
	private void adjustJoiningVertexOnCircle(VertexRecord v1, VertexRecord v2, VertexRecord v3) {
		double buf[] = new double[12];
		TrackRecord curve = v2.getTrackTo(v3);
		TrackRecord straight = v2.getTrackTo(v1);
		
		LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
		LineOps.toGeneral(v3.x(), v3.y(), v3.tangent(), 3, buf);
		LineOps.intersection(0, 3, 6, buf);
		
		
	}
	
	private void createCurvedToStraigtConnection(VertexRecord v1, VertexRecord v3, TrackRecord track) {
		VertexRecord v4 = track.getOppositeVertex(v3);
		VertexRecord v2 = ((TrackRecord) v1.getTrack(0)).getOppositeVertex(v1);
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
			tr.setMetadata(this.prepareCurveMetadata(x1, y1, v1.x(), v1.y(), x2, y2));
		} else {
			tr.setMetadata(this.prepareCurveMetadata(v1.x(), v1.y(), x1, y1, x2, y2));
		}
		
		this.unitOfWork.addTrack(tr);
	}
	
	/**
	 * Prepares the metadata for the curves. The points are: first vertex, second vertex, the center
	 * of the arc. The arc is always drawn from the first vertex to the second vertex.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return Arc metadata
	 */
	private double[] prepareCurveMetadata(double x1, double y1, double x2, double y2, double x3, double y3) {
		double angle1 = -Math.atan2(y1 - y3, x1 - x3);
		if(angle1 < 0.0) {
			angle1 += 2* Math.PI;
		}
		double angle2 = -Math.atan2(y2 - y3, x2 - x3);
		if(angle2 < 0.0) {
			angle2 += 2* Math.PI;
		}
		double diff = 0.0;
		if(angle1 < angle2) {
			diff = angle2 - angle1;
		} else {
			diff = angle2 + (2 * Math.PI - angle1);
		}
		double radius = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
		
		return new double[] {
			x3 - radius,
			y3 - radius,
			2 * radius,
			2 * radius,
			Math.toDegrees(angle1),
			Math.toDegrees(diff),
			x3, y3
		};
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
}
