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
package org.invenzzia.opentrans.visitons.geometry;

/**
 * Analytical geometry operations on a straight line.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LineOps {
	private LineOps() {
	}
	
	public static void toGeneral(double x1, double y1, double x2, double y2, int from, double general[]) {
		general[from] = y2 - y1;
		general[from + 1] = - (x2 - x1);
		general[from + 2] = - general[from] * x1 - general[from + 1] * y1;
	}
	
	/**
	 * Calculates the general parameters of the line orthogonal to the line that crosses P1 and P2 points
	 * given by x1, y1, x2, y2 coordinates. The orthogonal line itself crosses P2 point.
	 * 
	 * @param x1 X coordinate in P1
	 * @param y1 Y coordinate in P1
	 * @param x2 X coordinate in P2
	 * @param y2 Y coordinate in P2
	 * @param from Initial index in the orthogonal array, where we shall start saving.
	 * @param orthogonal The array for storing the coordinates. It must provide at least <code>3</code> spare indices, starting from <code>from</code>.
	 */
	public static void toOrthogonal(double x1, double y1, double x2, double y2, int from, double orthogonal[]) {
		orthogonal[from] = x2 - x1;
		orthogonal[from + 1] = y2 - y1;
		orthogonal[from + 2] = - orthogonal[from] * x1 - orthogonal[from + 1] * y1;
	}
	
	public static void toOrthogonal(int f1, double general[], int f2, double orthogonal[]) {
		orthogonal[f2] = - general[f1 + 1];
		orthogonal[f2 + 1] = general[f1];
		orthogonal[f2 + 2] = general[f1 + 2];
	}
	
	/**
	 * Changes the line to the orthogonal line crossing <code>(x,y)</code> point, in place.
	 * 
	 * @param from
	 * @param data
	 * @param x
	 * @param y 
	 */
	public static void toOrthogonal(int from, double data[], double x, double y) {
		double A = data[from];
		double B = data[from+1];
		data[from] = - B;
		data[from + 1] = A;
		data[from + 2] = - data[from] * x - data[from + 1] * y;
	}
	
	/**
	 * Calculates a determinant which tells, on which side of the line AB (A = (x1, y1), B = (x2, y2))
	 * lies the point C (x3, y3).
	 * 
	 * @param x1 X coordinate - first point of the line.
	 * @param y1 Y coordinate - first point of the line.
	 * @param x2 X coordinate - second point of the line.
	 * @param y2 Y coordinate - second point of the line.
	 * @param x3 Tested point
	 * @param y3 Tested point
	 * @return The determinant value
	 */
	public static byte onWhichSide(double x1, double y1, double x2, double y2, double x3, double y3) {
		double result = x1 * y2 + y1 * x3 + x2 * y3 - y2 * x3 - x1 * y3 - y1 * x2;
		if(result < 0.0) {
			return -1;
		} else if(result > 0.0) {
			return 1;
		}
		return 0;
	}
}
