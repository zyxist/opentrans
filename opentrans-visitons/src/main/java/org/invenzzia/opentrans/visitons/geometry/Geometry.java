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
 * Geometry utilities, usually related to angles.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Geometry {
	private static final double PI_2 = Math.PI * 2.0;
	static final double EPSILON = 1.0E-10;
	
	private Geometry() {
	}
	
	public static boolean isZero(double value) {
		return Math.abs(value) < EPSILON;
	}
	
	/**
	 * Calculates a distance between the two angles. The distance is not higher than
	 * 180 degrees.
	 * 
	 * @param a First angle in radians.
	 * @param b Second angle in radians.
	 * @return Distance between the angles in radians.
	 */
	public static double angleDist(double a, double b) {
		double dist = Math.abs(a - b) % (PI_2);
		return (dist > Math.PI ? (PI_2 - dist) : dist);
	}
	
	/**
	 * Calculates a distance between the two angles. The method can return distances
	 * higher than 180 degrees, but we must explicitely specify a convex and pick up
	 * the expected direction.
	 * 
	 * @param a First angle in radians.
	 * @param b Second angle in radians.
	 * @return Distance between the angles in radians.
	 */
	public static double angleDist(double a, double b, byte convex) {
		if(a > Math.PI) {
			if(0 == convex) {
				return +(b < a ? PI_2 - a + b : b - a);
			}
			return -(b < a ? a - b : PI_2 - b + a);
		}
		if(0 == convex) {
			return -(b < a ? a - b : PI_2 - b + a);
		}
		return (b < a ? PI_2 - a + b : b - a);
	}
	
	/**
	 * Reduces the angle to the domain <tt>[-PI, +PI]</tt>. The angle is specified in radians.
	 * 
	 * @param angle The angle in radians.
	 * @return Normalized angle in radians.
	 */
	public static double normalizeAngle(double angle) {
		if(angle < -Math.PI) {
			return angle + PI_2;
		} else if(angle > Math.PI) {
			return angle - PI_2;
		}
		return angle;
	}
}
