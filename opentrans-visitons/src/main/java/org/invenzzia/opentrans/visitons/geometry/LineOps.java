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

/**
 * Analytical geometry operations on a straight line.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LineOps {
	private LineOps() {
	}
	
	public static void toGeneral(double x1, double y1, double x2, double y2, int from, double general[]) {
		general[from] = y1 - y2;
		general[from + 1] = x2 - x1;
		general[from + 2] = y2 * x1 - y1 * x2;
	}
	
	/**
	 * Calculates the general parameters of the line from the tangent and a single point.
	 * 
	 * @param x
	 * @param y
	 * @param tangent Line tangent.
	 * @param from
	 * @param general 
	 */
	public static void toGeneral(double x, double y, double tangent, int from, double general[]) {
		if(Math.abs(tangent - Math.PI / 2) < Geometry.EPSILON || Math.abs(tangent - (Math.PI + Math.PI / 2)) < Geometry.EPSILON) {
			general[from] = x;
			general[from + 1] = 0.0;
			general[from + 2] = 0.0;
		} else {
			general[from] = Math.tan(tangent);
			general[from + 1] = -1.0;
			general[from + 2] = y - general[from] * x;
		}
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
		orthogonal[from + 2] = - orthogonal[from] * x2 - orthogonal[from + 1] * y2;
	}
	
	/**
	 * Calculates the general parameters of the line orthogonal to the line that crosses P1 and P2 points
	 * given by x1, y1, x2, y2 coordinates. The orthogonal line itself crosses P2 point, and P2 point is
	 * taken from the data buffer
	 * 
	 * @param x1 X coordinate in P1
	 * @param y1 Y coordinate in P1
	 * @param p2 P2 point position in the data buffer
	 * @param from Initial index in the orthogonal array, where we shall start saving.
	 * @param orthogonal The array for storing the coordinates. It must provide at least <code>3</code> spare indices, starting from <code>from</code>.
	 */
	public static void toOrthogonal(double x1, double y1, int p2, int from, double orthogonal[]) {
		orthogonal[from] = orthogonal[p2] - x1;
		orthogonal[from + 1] = orthogonal[p2+1] - y1;
		orthogonal[from + 2] = - orthogonal[from] * orthogonal[p2] - orthogonal[from + 1] * orthogonal[p2+1];
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
	 * Changes the line to the orthogonal line crossing <code>(x, y)</code>. The new line is stored
	 * in the new place.
	 * 
	 * @param from Where to read the initial data from?
	 * @param to Where to save the result.
	 * @param data Data buffer
	 * @param x Crossing point
	 * @param y Crossing point
	 */
	public static void toOrthogonal(int from, int to, double data[], double x, double y) {
		data[to] = - data[from+1];
		data[to + 1] = data[from];
		data[to + 2] = - data[to] * x - data[to + 1] * y;
	}
	
	/**
	 * Finds a parallel line to <tt>from</tt>, that crosses point P
	 * @param from
	 * @param to
	 * @param data
	 * @param x P point: X
	 * @param y P point: Y
	 */
	public static void toParallel(int from, int to, double data[], double x, double y) {
		data[to] = data[from];
		data[to + 1] = data[from+1];
		data[to + 2] = - data[to] * x - data[to + 1] * y;
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
		x2 -= x1;
		y2 -= y1;
		x3 -= x1;
		y3 -= y1;
		double result = x2*y3 - y2*x3;
		
	//	double result = x1 * y2 + y1 * x3 + x2 * y3 - y2 * x3 - x1 * y3 - y1 * x2;
		if(result < 0.0) {
			return -1;
		} else if(result > 0.0) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * Checks if the second point lies between the other two ones. We assume that the tested
	 * point lies on the line.
	 * 
	 * @param x1 X coordinate - first point of the line.
	 * @param y1 Y coordinate - first point of the line.
	 * @param x2 X coordinate - tested point.
	 * @param y2 Y coordinate - tested point.
	 * @param x3 X coordinate - second point of the line.
	 * @param y3 Y coordinate - second point of the line.
	 * @return The determinant value
	 */
	public static boolean isBetween(double x1, double y1, double x2, double y2, double x3, double y3) {
		double t;
		if(Geometry.isZero(x3 - x1)) {
			// Perform calculations on Y axis
			t = (y2 - y1) / (y3 - y1); 
		} else {
			t = (x2 - x1) / (x3 - x1);
		}
		return (t > 0.0 && t < 1.0);
	}
	
	/**
	 * Checks, on which side of the line given by the point A and tangent t, the point C lies.
	 * 
	 * @param x1 A point: X
	 * @param y1 A point: Y
	 * @param tangent Tangent of the line l.
	 * @param x3 C point: X
	 * @param y3 C point: Y
	 * @return 
	 */
	public static int onWhichSide(double x1, double y1, double tangent, double x3, double y3) {
		double A, B, C;
		if(Math.abs(tangent - Math.PI / 2) < Geometry.EPSILON || Math.abs(tangent - (Math.PI + Math.PI / 2)) < Geometry.EPSILON) {
			A = x1;
			B = 0.0;
			C = 0.0;
		} else {
			A = Math.tan(tangent);
			B = -1.0;
			C = y1 - A * x1;
		}
		
		return (A * x3 + B * y3 + C) > 0.0 ? 1 : -1;
	}
	
	/**
	 * Finds the intersection of the two lines. The input data are read from <tt>data</tt> parameter,
	 * and the result is also written there. The first argument specifies the position of the first
	 * line parameters: A1, B1, C1. The second argument specifies the position of the second line
	 * parameters: A2, B2, C2. The third argument specifies, where the output values X and Y shall
	 * be written in the output array.
	 * 
	 * @param line1 Index of A1 parameter.
	 * @param line2 Index of A2 parameter.
	 * @param out Index of X parameter.
	 * @param data Input/output computation data tablice.
	 */
	public static void intersection(int line1, int line2, int out, double data[]) {
		data[out] = (data[line1+1] * data[line2+2] - data[line1+2] * data[line2+1]) /
			(data[line1] * data[line2+1] - data[line1+1] * data[line2]);
		data[out+1] = (data[line1+2] * data[line2] - data[line1] * data[line2+2]) /
			(data[line1] * data[line2+1] - data[line1+1] * data[line2]);
	}
	
	/**
	 * Calculates the length of a line.
	 * 
	 * @param x1 X coordinate of the first point.
	 * @param y1 Y coordinate of the first point.
	 * @param x2 X coordinate of the second point.
	 * @param y2 Y coordinate of the second point.
	 * @return Line length.
	 */
	public static double lineLength(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	/**
	 * Suppose we have three points: A, B, C. Let <tt>(A, B)</tt> be a vector from A to B. C is
	 * some free point. We cast C to the line specified by the vector and find out, by how much
	 * it would lenghten (+) or shorten(-) that vector, if we did <tt>B = (cast C)</tt>.
	 * In addition, the position of <tt>cast C</tt> is saved in the buffer under the indices
	 * <tt>from + 6, from + 7</tt>.
	 * 
	 * @param x1 A: X
	 * @param y1 A: Y
	 * @param x2 B: X
	 * @param y2 B: Y
	 * @param x3 C: X
	 * @param y3 C: Y
	 * @param from Starting position in the buffer
	 * @param buf The data buffer with at least 8 free slots, starting from <tt>from</tt>.
	 * @return Vector lenght potential change.
	 */
	public static double vectorLengtheningDistance(double x1, double y1, double x2, double y2, double x3, double y3, int from, double buf[]) {
		LineOps.toGeneral(x1, y1, x2, y2, from, buf);
		LineOps.toOrthogonal(from, from + 3, buf, x3, y3);
		LineOps.intersection(from, from + 3, from + 6, buf);
		double dist = lineLength(x2, y2, buf[from+6], buf[from+7]);
		if(lineLength(x1, y1, buf[from+6], buf[from+7]) < lineLength(x1, y1, x2, y2)) {
			return -dist;
		}
		return dist;
	}
	
	/**
	 * Let <tt>(A, B)</tt> be the vector. This method finds the new position of B, if the vector is lenghtened
	 * or shortened by the given <tt>from</tt> value.
	 * 
	 * @param x1 A: X
	 * @param y1 A: Y
	 * @param x2 B: X
	 * @param y2 B: Y
	 * @param value The value we lenghten or shorten the vector by.
	 * @param from Starting position in the buffer
	 * @param buf Buf with at least 2 free slots starting from <tt>from</tt>.
	 */
	public static void lenghtenVector(double x1, double y1, double x2, double y2, double value, int from, double buf[]) {
		double a = 1.0 + value / LineOps.lineLength(x1, y1, x2, y2);
		buf[from] = x1 + (x2 - x1) * a;
		buf[from+1] = y1 + (y2 - y1) * a;
	}

	/**
	 * Calculate parametric value of X or Y point on a line, specified by t
	 * parameter and the line starting and ending points. We need to call it
	 * separately for X and Y to get the point.
	 * 
	 * @param t Position
	 * @param x1 Starting point
	 * @param x2 Ending point
	 * @return The coordinate of the point on the line defined by <tt>t</tt> parameter.
	 */
	public static double linePoint(double t, double x1, double x2) {
		return x1 + t * (x2 - x1);
	}
	
	/**
	 * Calculates the tangent of a line (in radians), using two control points
	 * on this line. The tangent is calculated towards point P2, and P1 is treated
	 * as a centre of the temporary coordinate system.
	 * 
	 * Note that the tangent is in range <tt>[-PI, +PI]</tt>.
	 * 
	 * @param x1 Central point X coordinate
	 * @param y1 Central point Y coordinate
	 * @param x2 P2 point X coordinate
	 * @param y2 P2 point Y coordinate
	 * @return Tangent of the line from P1 to P2
	 */
	public static double getTangent(double x1, double y1, double x2, double y2) {
		return Math.atan2(y2 - y1, x2 - x1);
	}
	
	/**
	 * Finds the angle bisectors of two other lines.
	 * 
	 * @param fst First line index
	 * @param sec Second line index
	 * @param ab1 Where to save the parameters of the first angle bisector.
	 * @param ab2 Where to save the parameters of the second angle bisector.
	 * @param buf Data buffer.
	 */
	public static void angleBisector(int fst, int sec, int ab1, int ab2, double buf[]) {
		double p = Math.sqrt(Math.pow(buf[fst], 2) + Math.pow(buf[fst+1], 2));
		double q = Math.sqrt(Math.pow(buf[sec], 2) + Math.pow(buf[sec+1], 2));
		buf[ab1]   = (buf[fst]   * q + buf[sec]   * p);
		buf[ab1+1] = (buf[fst+1] * q + buf[sec+1] * p);
		buf[ab1+2] = (buf[fst+2] * q + buf[sec+2] * p);
		buf[ab2]   = (buf[fst]   * q - buf[sec]   * p);
		buf[ab2+1] = (buf[fst+1] * q - buf[sec+1] * p);
		buf[ab2+2] = (buf[fst+2] * q - buf[sec+2] * p);
	}
	
	/**
	 * Finds the middle point of a section.
	 * 
	 * @param x1 First point: X
	 * @param y1 First point: Y
	 * @param x2 Second point: X
	 * @param y2 Second point: Y
	 * @param to Where to save the result.
	 * @param buf Data buffer.
	 */
	public static void middlePoint(double x1, double y1, double x2, double y2, int to, double buf[]) {
		buf[to] = (x1 + x2) / 2.0;
		buf[to+1] = (y1 + y2) / 2.0;
	}
	
	/**
	 * Finds the middle point of a section.
	 * 
	 * @param p1 First point in the data buffer
	 * @param p2 Second point in the data buffer
	 * @param to Where to save the result.
	 * @param buf Data buffer.
	 */
	public static void middlePoint(int p1, int p2, int to, double buf[]) {
		buf[to] = (buf[p1] + buf[p2]) / 2.0;
		buf[to+1] = (buf[p1+1] + buf[p2+1]) / 2.0;
	}
	
	/**
	 * Finds the middle point of a section.
	 * 
	 * @param x1 First point: X
	 * @param y1 First point: Y
	 * @param p2 Second point in the data buffer
	 * @param to Where to save the result.
	 * @param buf Data buffer.
	 */
	public static void middlePoint(double x1, double y1, int p2, int to, double buf[]) {
		buf[to] = (x1 + buf[p2]) / 2.0;
		buf[to+1] = (y1 + buf[p2+1]) / 2.0;
	}
	
	/**
	 * Finds a distance between two points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Distance between two points.
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static boolean areParallel(double tan1, double tan2) {
		return Geometry.isZero(tan1 - tan2) || Geometry.isZero(Math.abs(tan1 - tan2) - Math.PI);
	}
	
	/**
	 * If the points lie on the same line, we can sort them in the ascending order.
	 * 
	 * @param buf Data buffer.
	 * @param pts List of point indices in the buffer to reorder. The indices themselves do not have to be sorted.
	 */
	public static void reorder(double buf[], int ... pts) {
		if(pts.length > 1) {
			double tempBuf[] = new double[pts.length * 2];
			int orig[] = new int[pts.length];
			for(int i = 0; i < pts.length; i++) {
				tempBuf[i * 2] = buf[pts[i]];
				tempBuf[i * 2 + 1] = buf[pts[i] + 1];
				orig[i] = pts[i];
			}
			if(Geometry.isZero(buf[pts[0]] - buf[pts[1]])) {
				// Move the sweep vertically
				for(int i = 0; i < pts.length; i++) {
					for(int j = i; j < pts.length; j++) {
						if(buf[pts[i]+1] > buf[pts[j]+1]) {
							int tmp = pts[j];
							pts[j] = pts[i];
							pts[i] = tmp;
						}
					}
				}
			} else {
				// Move the sweep horizontally
				for(int i = 1; i < pts.length; i++) {
					int idx = pts[i];
					int hole = i;
					while(hole >= 1 && buf[idx] < buf[pts[hole - 1]]) {
						pts[hole] = pts[hole - 1];
						hole--;
					}
					pts[hole] = idx;
				}
			}
			for(int i = 0; i < pts.length; i++) {
				tempBuf[i * 2] = buf[pts[i]];
				tempBuf[i * 2 + 1] = buf[pts[i] + 1];
			}
			for(int i = 0; i < pts.length; i++) {
				buf[orig[i]] = tempBuf[i * 2];
				buf[orig[i] + 1] = tempBuf[i * 2 + 1];
			}
		}
	}
}
