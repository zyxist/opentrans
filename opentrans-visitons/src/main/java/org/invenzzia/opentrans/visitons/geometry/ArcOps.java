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

package org.invenzzia.opentrans.visitons.geometry;

import com.google.common.base.Preconditions;

/**
 * Utility class for performing mathematical operations on cubic Bezier curves
 * with two control points.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ArcOps {
	public static double arcPoint(double t, double angle, double r) {
		return 0.0;
	}

	/**
	 * Calculates the arc length.
	 * 
	 * @param angle Arc angle in radians.
	 * @param r Arc radius.
	 * @return Arc length.
	 */
	public static double arcLength(double angle, double r) {
		return angle * r;
	}

	/**
	 * Finds a circle with center in point {@link #p1} that crosses through points
	 * {@link #p2}.
	 * 
	 * @param p1 Center of a circle.
	 * @param p2 Control point.
	 * @param out Where to save the parameters of a circle.
	 * @param buf Data buffer.
	 */
	public static void circleThroughPoint(int p1, int p2, int out, double buf[]) {
		buf[out] = Math.pow(buf[p1] - buf[p2], 2) + Math.pow(buf[p1+1] - buf[p2+1], 2);
	}
	
	/**
	 * Finds the intersection of the line with a circle. The line crosses the center
	 * of the circle. The result are four numbers that represent the two intersection
	 * points.
	 * 
	 * @param c1 Circle equation.
	 * @param l1 Line equation.
	 * @param out Where to save the coordinates.
	 * @param buf Data buffer.
	 */
	public static void circleLineIntersection(int c1, int l1, int out, double buf[]) {
		// If the line is vertical, we must use somehow different equations. What a pity.
		// Note that buf[c1+2] = r^2, so we do not have to power it.
		if(Geometry.isZero(buf[l1+1])) {
			double x = - buf[l1+2] / buf[l1];
			// Now we have a nice quadratic equation which we know by definition
			// that has two solutions.
			double A = 1.0;
			double B = -2 * buf[c1+1];
			double C = Math.pow(buf[c1+1], 2) - buf[c1+2] + Math.pow(x - buf[c1], 2);
			double delta = Math.pow(B, 2) - 4 * A * C;
			Preconditions.checkState(delta > 0.0, "The line does not cross the center of the circle.");			
			buf[out] = x;
			buf[out+1] = (- B + Math.sqrt(delta)) / (2 * A);
			buf[out+2] = x;
			buf[out+3] = (- B - Math.sqrt(delta)) / (2 * A);
		} else {
			double ci = -buf[l1] / buf[l1+1];
			double si = buf[l1+2] / buf[l1+1] + buf[c1+1];
			double A = 1.0 + Math.pow(ci, 2);
			double B = - 2 * ci * si - 2 * buf[c1];
			double C = Math.pow(buf[c1], 2) + Math.pow(si, 2) - buf[c1+2];
			double delta = Math.pow(B, 2) - 4 * A * C;
			buf[out] = (- B + Math.sqrt(delta)) / (2 * A);
			buf[out+1] = (-buf[l1] * buf[out] - buf[l1+2]) / buf[l1+1];
			buf[out+2] = (- B - Math.sqrt(delta)) / (2 * A);
			buf[out+3] = (-buf[l1] * buf[out+2] - buf[l1+2]) / buf[l1+1];
		}
	}
	
	/**
	 * Finds a tangent angle of a given point on a circle. The method requires three
	 * temporary cells in the data buffer to store its internal calculation data.
	 * 
	 * @param pos Circle equation position in the data buffer.
	 * @param tmpLoc Temporary data location in the data buffer.
	 * @param buf Data buffer.
	 * @param x Point on a circle
	 * @param y Point on a circle
	 * @return Tangent in this point.
	 */
	public static double getTangent(int pos, int tmpLoc, double buf[], double x, double y) {
		LineOps.toGeneral(buf[pos], buf[pos+1], x, y, tmpLoc, buf);
		LineOps.toOrthogonal(tmpLoc, buf, x, y);
		if(Geometry.isZero(buf[tmpLoc+1])) {
			return Math.PI / 2.0;
		}
		return Math.atan(- buf[tmpLoc] / buf[tmpLoc+1]);
	}
	
	/**
	 * Finds the angle difference. 
	 *
	 * @param x1 P1 point
	 * @param y1 P1 point
	 * @param x2 P2 point
	 * @param y2 P2 point
	 * @param x3 Circle centre
	 * @param y3 Circle centre.
	 * @return Angle difference from P1 to P2.
	 */
	public static double getAngleDifference(double x1, double y1, double x2, double y2, double x3, double y3) {
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
			return angle2 - angle1;
		} else {
			return angle2 + (2 * Math.PI - angle1);
		}
	}

	/**
	 * The parametric equation of a directed arc. To calculate it, we must know the starting
	 * point, the direction of arc, and the centre of it. px and py are the point, for whose
	 * we want to calculate the distance from this point, which can be used to get the <tt>t in [0.0, 1.0]</tt>
	 * parameter.
	 */
	public static double coord2Param(double cx, double cy, double ax, double ay, double tangent, double px, double py) {
		double a1 = LineOps.getTangent(cx, cy, ax, ay);
		boolean differentSigns = Math.signum(a1) != Math.signum(tangent);
		double a3 = LineOps.getTangent(cx, cy, px, py);
		boolean grows;
		if(differentSigns) {
			grows = Geometry.inSecondQuarter(a1) || Geometry.inFourthQuarter(a1);
		} else {
			grows = Geometry.inFirstQuarter(a1) || Geometry.inThirdQuarter(a1);
		}
		if(grows) {
			if(a3 < a1) {
				a3 += Geometry.PI_2;
			}
			return a3 - a1;
		} else {
			if(a3 > a1) {
				a3 -= Geometry.PI_2;
			}
			return a1 - a3;
		}
	}
	
	/**
	 * The parametric equation of a directed arc. We give the parameter <tt>t in [0.0, 1.0]</tt> and we
	 * get the actual angle of the point in relation to the starting point of the arc.
	 * 
	 * @param t
	 * @param cx Center point of arc: X
	 * @param cy Center point of arc: Y
	 * @param ax Starting point of arc: X
	 * @param ay Starting point of arc: Y
	 * @param tangent Direction of the arc
	 * @param length Length of the arc in radians
	 * @return Actual angle of the point given by <tt>t</tt> parameter. 
	 */
	public static double param2Angle(double t, double cx, double cy, double ax, double ay, double tangent, double length) {
		double a = LineOps.getTangent(cx, cy, ax, ay);
		boolean differentSigns = Math.signum(a) != Math.signum(tangent);
		boolean grows;
		if(differentSigns) {
			grows = Geometry.inSecondQuarter(a) || Geometry.inFourthQuarter(a);
		} else {
			grows = Geometry.inFirstQuarter(a) || Geometry.inThirdQuarter(a);
		}
		double k = t * length;
		if(grows) {
			return a + k;
		} else {
			return a - k;
		}
	}
}
