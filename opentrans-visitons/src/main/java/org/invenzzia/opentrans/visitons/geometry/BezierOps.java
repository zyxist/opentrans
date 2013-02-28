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
public class BezierOps {
	private static final int INTEGRATION_STEPS = 100;
	
	/**
	 * Calculate parametric value of x or y given t and the four point
	 * coordinates of a cubic bezier curve. This is a separate function
	 * because we need it for both x and y values.
	 * 
	 * @param t Position
	 * @param start Starting coordinate
	 * @param control1 First control point coordinate
	 * @param control2 Second control point coordinate
	 * @param end End point coordinate
	 * @return The coordinate of the point on the Bezier curve defined by <tt>t</tt> parameter.
	 */
	public static double bezierPoint(double t, double start, double control1, double control2, double end) {
		/* Formula from Wikipedia article on Bezier curves. */
		return            start * (1.0 - t) * (1.0 - t) * (1.0 - t) 
		   + 3.0 * control1 * (1.0 - t) * (1.0 - t) * t 
		   + 3.0 * control2 * (1.0 - t) * t         * t
		   +            end * t         * t         * t;
	}
	
	/**
	 * Calculates the length of the Bezier curve using the numerical integration technique. The method
	 * requires an array of 8 data points:
	 * 
	 * <ul>
	 *  <li>X and Y coordinates of the starting point</li>
	 *  <li>X and Y coordinates of the control point 1</li>
	 *  <li>X and Y coordinates of the control point 2</li>
	 *  <li>X and Y coordinates of the end point</li>
	 * </ul>
	 * 
	 * @param data Array of 8 data points.
	 * @return Approximation of the Bezier curve length.
	 */
	public static double bezierLength(double data[]) {
		Preconditions.checkArgument(data.length == 8, "We need 8 data points to calculate Bezier curve length.");
		
		// The points forming the section moved across the Bezier curve.
		double dots[] = new double[4];
		double length = 0.0;
		for(int i = 0; i < INTEGRATION_STEPS; i++) {
			double t = (double) i / (double) INTEGRATION_STEPS;
			dots[0] = bezierPoint(t, data[0], data[2], data[4], data[6]);
			dots[1] = bezierPoint(t, data[1], data[3], data[5], data[7]);
			
			double xDiff = dots[0] - dots[2];
			double yDiff = dots[1] - dots[3];
			length += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		}
		return length;
	}

}
