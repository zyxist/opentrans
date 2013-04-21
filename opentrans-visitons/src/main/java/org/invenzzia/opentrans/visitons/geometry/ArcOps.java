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
	 * Calculates the center of the arc.
	 * 
	 * @param inputData Array of the input data:
	 * @param idx Starting index to write out the result in the output data.
	 * @param outputData Output data array.
	 */
	public static void arcCenter(double inputData[], int idx, double outputData[]) {
		
	}
}
