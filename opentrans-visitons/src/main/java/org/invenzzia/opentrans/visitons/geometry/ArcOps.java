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
	 * Calculates the center of the arc.
	 * 
	 * @param inputData Array of the input data:
	 * @param idx Starting index to write out the result in the output data.
	 * @param outputData Output data array.
	 */
	public static void arcCenter(double inputData[], int idx, double outputData[]) {
		
	}
}
