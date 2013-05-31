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
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.opentrans.visitons.bindings.ActualImporter;
import org.invenzzia.opentrans.visitons.geometry.ArcOps;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.transform.ops.IOperation;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.DebugPointSnapshot;

/**
 * New transformation engine that automates much of condition verification. The engine provides
 * only the basic transformations in a form of API for the operations: {@link ITransformAPI}.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TransformEngine {
	/**
	 * Epsilon for double value comparisons.
	 */
	private static final double EPSILON = 0.0000000001;
	/**
	 * How to import the data from the world model?
	 */
	private final IRecordImporter recordImporter;
	/**
	 * The internal transformation API;
	 */
	private final TransformAPI api;
	/**
	 * Where do we apply the changes?
	 */
	private NetworkUnitOfWork unitOfWork;
	/**
	 * Additional information about the edited world.
	 */
	private WorldRecord world;
	/**
	 * Drawing hint lines, etc.
	 */
	private SceneManager sceneManager;
	/**
	 * Map of all the possible operations.
	 */
	private Map<Class<? extends IOperation>, IOperation> operations;
	
	@Inject
	public TransformEngine(@ActualImporter IRecordImporter recordImporter, SceneManager sceneManager) {
		this.recordImporter = Preconditions.checkNotNull(recordImporter);
		this.operations = new LinkedHashMap<>();
		this.sceneManager = sceneManager;
		this.api = new TransformAPI();
	}
	
	/**
	 * Registers a new transformation operation.
	 * 
	 * @param operation 
	 */
	public void addOperation(IOperation operation) {
		Preconditions.checkNotNull(operation);
		this.operations.put(operation.getClass(), operation);
		operation.setTransformAPI(this.api);
	}
	
	/**
	 * Passes the current unit of work that should be updated by the transformation engine.
	 * 
	 * @param unitOfWork 
	 */
	public void setUnitOfWork(NetworkUnitOfWork unitOfWork) {
		this.unitOfWork = unitOfWork;
	}
	
	/**
	 * Injects the current record of the world.
	 * 
	 * @param world 
	 */
	public void setWorld(WorldRecord world) {
		this.world = Preconditions.checkNotNull(world);
	}
	
	public <T extends IOperation> T op(Class<T> opType) {
		T operation = (T) this.operations.get(opType);
		if(null == operation) {
			throw new IllegalArgumentException("No such operation: "+opType.getSimpleName());
		}
		return operation;
	}

	public ITransformAPI getAPI() {
		return this.api;
	}
	
	/**
	 * Implementation of the API with all the transformations that the operations can
	 * use to do their job.
	 */
	class TransformAPI implements ITransformAPI {

		@Override
		public WorldRecord getWorld() {
			return world;
		}

		@Override
		public IRecordImporter getRecordImporter() {
			return recordImporter;
		}
		
		@Override
		public NetworkUnitOfWork getUnitOfWork() {
			return unitOfWork;
		}

		@Override
		public void calculateStraightLine(TrackRecord tr) {
			VertexRecord v1 = tr.getFirstVertex();
			VertexRecord v2 = tr.getSecondVertex();
			double tangent = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
			v1.setTangentFor(tr, tangent);
			v2.setTangentFor(tr, Geometry.normalizeAngle(tangent + Math.PI));
			tr.setMetadata(new double[] { v1.x(), v1.y(), v2.x(), v2.y() } );
		}

		@Override
		public void calculateCurve(TrackRecord tr) {
			VertexRecord v1 = tr.getFirstVertex();
			VertexRecord v2 = tr.getSecondVertex();
			
			double buf[] = new double[3];
			this.prepareCurveFreeMovement(v2, v1.x(), v1.y(), 0, buf);
			this.findCurveDirection(tr, buf[0], buf[1]);
		}

		@Override
		public void calculateFreeCurve(TrackRecord tr) {
			Preconditions.checkArgument(tr.getType() == NetworkConst.TRACK_FREE, "Invalid track type: TRACK_FREE expected.");
			VertexRecord v1 = tr.getFirstVertex();
			VertexRecord v2 = tr.getSecondVertex();
			int v1Loc = 0;
			int v2Loc = 8;
			if(v1.hasOneTrack()) {
				VertexRecord tmp = v2;
				v2 = v1;
				v1 = tmp;
				v1Loc = 8;
				v2Loc = 0;
			}
			
			double tv1 = Geometry.normalizeAngle(v1.oppositeTangentFor(tr) + Math.PI);
			double tv2;
			if(v2.hasOneTrack()) {
				tv2 = LineOps.getTangent(v2.x(), v2.y(), v1.x(), v1.y());
			} else {
				tv2 = Geometry.normalizeAngle(v2.oppositeTangentFor(tr) + Math.PI);
			}
			
			// Here we should write the results of our calculation: center points of both circles and the middle point.
			double c1x, c1y, c2x, c2y, mx, my;
			
			double metadata[] = new double[16];
			if(LineOps.areParallel(tv1, tv2)) {
				// If the lines are parallel, we need a special handling.
				double buf[] = new double[22];
				LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
				LineOps.toGeneral(v2.x(), v2.y(), v2.tangent(), 3, buf);
				LineOps.toOrthogonal(0, 6, buf, v1.x(), v1.y());
				LineOps.toOrthogonal(3, 9, buf, v2.x(), v2.y());

				LineOps.middlePoint(v1.x(), v1.y(), v2.x(), v2.y(), 13, buf); // I'm G: 13
				mx = buf[13];
				my = buf[14];
				c1x = buf[15];
				c1y = buf[16];
				c2x = buf[17];
				c2y = buf[18];
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
				sceneManager.updateResource(DebugPointSnapshot.class, new DebugPointSnapshot(
					new double[] { buf[35], buf[36], buf[37], buf[38], buf[39], buf[40], buf[41], buf[42],  }
				));
				int selectedPt;
				if(LineOps.isBetween(buf[39], buf[40], buf[35], buf[36], buf[41], buf[42])) {
					selectedPt = 35;
				} else {
					selectedPt = 37;
				}			
				LineOps.middlePoint(v1.x(), v1.y(), selectedPt, 41, buf);	// K
				LineOps.middlePoint(v2.x(), v2.y(), selectedPt, 43, buf);	// L
				
				LineOps.toOrthogonal(v1.x(), v1.y(), 41, 50, buf);
				LineOps.toOrthogonal(v2.x(), v2.y(), 43, 53, buf);
				LineOps.intersection(6, 50, 56, buf); // M point - center of the first arc
				LineOps.intersection(9, 53, 58, buf); // N point - center of the second arc
				
				c1x = buf[56];
				c1y = buf[57];
				c2x = buf[58];
				c2y = buf[59];
				mx = buf[selectedPt];
				my = buf[selectedPt+1];
			}

			// Calculations for the arrangement of the curves.
			double tan = v1.tangentFor(tr);
			double towards = LineOps.getTangent(v1.x(), v1.y(), mx, my);
			double diff = Math.atan2(Math.sin(towards-tan), Math.cos(towards-tan));
			if(diff > 0.0) {
				this.prepareCurveMetadata(mx, my, v1.x(), v1.y(), c1x, c1y, v1Loc, metadata);
			} else {
				this.prepareCurveMetadata(v1.x(), v1.y(), mx, my, c1x, c1y, v1Loc, metadata);
			}
			metadata[v1Loc + 3] = diff;
			// The second arc...
			tan = v2.tangentFor(tr);
			towards = LineOps.getTangent(v2.x(), v2.y(), mx, my);
			diff = Math.atan2(Math.sin(towards-tan), Math.cos(towards-tan));
			
			if(diff > 0.0) {
				this.prepareCurveMetadata(mx, my, v2.x(), v2.y(), c2x, c2y, v2Loc, metadata);
			} else {
				this.prepareCurveMetadata(v2.x(), v2.y(), mx, my, c2x, c2y, v2Loc, metadata);
			}
			metadata[v2Loc + 3] = diff;
			v1.setTangentFor(tr, tv1);
			v2.setTangentFor(tr, tv2);
			tr.setMetadata(metadata);
		}

		@Override
		public void curveFollowsStraightTrack(VertexRecord v1, VertexRecord v2, VertexRecord v3) {
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

			this.calculateStraightLine(straight);
			this.prepareCurveFreeMovement(v3, v2.x(), v2.y(), 0, buf);
			this.findCurveDirection(curve, buf[0], buf[1]);
		}

		@Override
		public void curveFollowsPoint(TrackRecord curvedTrack, VertexRecord boundVertex) {
			// If bound vertex does not have one track, be sure that you know what you are doing.
			// This state is necessary for handling curve-free connections.
			VertexRecord v2 = curvedTrack.getOppositeVertex(boundVertex);
			Preconditions.checkState(v2.hasAllTracks());
			
			double buf[] = new double[3];
			this.prepareCurveFreeMovement(v2, boundVertex.x(), boundVertex.y(), 0, buf);
			this.findCurveDirection(curvedTrack, boundVertex, buf[0], buf[1]);
		}

		// hint: v1 - stationary
		// v3 - can be adjusted
		@Override
		public boolean matchStraightTrackAndCurve(TrackRecord curvedTrack, TrackRecord straightTrack, VertexRecord v1, VertexRecord v3) {
			Preconditions.checkArgument(straightTrack.hasVertex(v3) && curvedTrack.hasVertex(v3) && curvedTrack.hasVertex(v1), "Conditions not satisfied.");
			VertexRecord v4 = straightTrack.getOppositeVertex(v3);

			double buf[] = new double[12];
			// First line: Dx + Ey + F = 0
			LineOps.toGeneral(v3.x(), v3.y(), v4.x(), v4.y(), 0, buf);
			// Second line - we only need the orthogonal: A2x + B2y + C2 = 0
		//	LineOps.toGeneral(v1.x(), v1.y(), v2.x(), v2.y(), 3, buf);
			LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 3, buf);

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

			if(!world.isWithinWorld(x1, y1)) {
				return false;
			}
		
			v3.setPosition(x1, y1);
			straightTrack.setMetadata(new double[] { v3.x(), v3.y(), v4.x(), v4.y() });
			this.findCurveDirection(curvedTrack, x2, y2);
			
			return true;
		}

		@Override
		public void castFreePositionToTangent(VertexRecord v1, double x, double y) {
			double buf[] = new double[8];
			LineOps.toGeneral(v1.x(), v1.y(), v1.tangent(), 0, buf);
			LineOps.toOrthogonal(0, 3, buf, x, y);
			LineOps.intersection(0, 3, 6, buf);
			v1.setPosition(buf[6], buf[7]);
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
			buf[from+3] = 0.0;	// filled later
			buf[from+4] = angle1;
			buf[from+5] = diff;
			buf[from+6] = x3;
			buf[from+7] = y3;
			return buf;
		}

		/**
		 * These calculations are used for drawing curve, where one of the vertices has only one track connected:
		 * the curve we are currently drawing. In this case, we must apply a bit different transformation to search
		 * the curve centre point. Exported data to the buffer are: centre X, centre Y, tangent in the second point.
		 * 
		 * @param v1 Stationary vertex, connected somewhere.
		 * @param x2 Second point, that is open.
		 * @param y2 Second point, that is open.
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
		 * Applies the proper direction to the curve.
		 * 
		 * @param tr The curved track.
		 * @param v1 First vertex connected to this track.
		 * @param v2 Second vertex connected to this track.
		 * @param cx Center of the arc: X
		 * @param cy Center of the arc: Y
		 * @return Direction: negative if tracks are replaced.
		 */
		private void findCurveDirection(TrackRecord tr, double cx, double cy) {
			VertexRecord v1 = tr.getFirstVertex();
			VertexRecord v2 = tr.getSecondVertex();
			if(v1.hasOneTrack()) {
				VertexRecord tmp = v2;
				v2 = v1;
				v1 = tmp;
			}
			
			double tan = v1.tangentFor(tr);
			double towards = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
			double diff = Math.atan2(Math.sin(towards-tan), Math.cos(towards-tan));
			double metadata[];
			if(diff > 0.0) {
				metadata = this.prepareCurveMetadata(v2.x(), v2.y(), v1.x(), v1.y(), cx, cy, 0, null);
				tan = LineOps.getTangent(cx, cy, v2.x(), v2.y());
				v2.setTangentFor(tr, Geometry.normalizeAngle(tan - Math.PI / 2.0));
			} else {
				metadata = this.prepareCurveMetadata(v1.x(), v1.y(), v2.x(), v2.y(), cx, cy, 0, null);
				tan = LineOps.getTangent(cx, cy, v2.x(), v2.y());
				v2.setTangentFor(tr, Geometry.normalizeAngle(tan + Math.PI / 2.0));
			}
			metadata[8] = tr.getFirstVertex().x();
			metadata[9] = tr.getFirstVertex().y();
			metadata[10] = tr.getSecondVertex().x();
			metadata[11] = tr.getSecondVertex().y();
			metadata[3] = tr.getFirstVertex().tangentFor(tr);
			
			tr.setMetadata(metadata);
			
			v1.setTangentFor(tr, Geometry.normalizeAngle(v1.oppositeTangentFor(tr) + Math.PI));
		}
		
		/**
		 * Applies the proper direction to the curve, but allows choosing the bound vertex.
		 * 
		 * @param tr The curved track.
		 * @param v2 Bound vertex.
		 * @param cx Center of the arc: X
		 * @param cy Center of the arc: Y
		 * @return Direction: negative if tracks are replaced.
		 */
		private void findCurveDirection(TrackRecord tr, VertexRecord v2, double cx, double cy) {
			VertexRecord v1 = tr.getOppositeVertex(v2);
			double tan = v1.tangentFor(tr);
			double towards = LineOps.getTangent(v1.x(), v1.y(), v2.x(), v2.y());
			double diff = Math.atan2(Math.sin(towards-tan), Math.cos(towards-tan));
			double metadata[];

			if(diff > 0.0) {
				metadata = this.prepareCurveMetadata(v2.x(), v2.y(), v1.x(), v1.y(), cx, cy, 0, null);
				tan = LineOps.getTangent(cx, cy, v2.x(), v2.y());
				v2.setTangentFor(tr, Geometry.normalizeAngle(tan - Math.PI / 2.0));
			} else {
				metadata = this.prepareCurveMetadata(v1.x(), v1.y(), v2.x(), v2.y(), cx, cy, 0, null);
				tan = LineOps.getTangent(cx, cy, v2.x(), v2.y());
				v2.setTangentFor(tr, Geometry.normalizeAngle(tan + Math.PI / 2.0));
			}
			metadata[8] = tr.getFirstVertex().x();
			metadata[9] = tr.getFirstVertex().y();
			metadata[10] = tr.getSecondVertex().x();
			metadata[11] = tr.getSecondVertex().y();
			metadata[3] = tr.getFirstVertex().tangentFor(tr);

			tr.setMetadata(metadata);
			v1.setTangentFor(tr, Geometry.normalizeAngle(v1.oppositeTangentFor(tr) + Math.PI));
		}
	}
}
