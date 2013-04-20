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

import junit.framework.Assert;
import org.junit.Test;

public class ArcOpsTest {
	@Test
	public void testCircleLineIntersectionCalculatorCase1() {
		double buf[] = new double[10];
		buf[0] = 2.0; // a
		buf[1] = 0.0; // b
		buf[2] = 4.0; // r^2
		
		buf[3] = 0.0;
		buf[4] = 1.0;
		buf[5] = 0.0;
		
		ArcOps.circleLineIntersection(0, 3, 6, buf);
		Assert.assertEquals(4.0, buf[6], Geometry.EPSILON);
		Assert.assertEquals(0.0, buf[7], Geometry.EPSILON);
		Assert.assertEquals(0.0, buf[8], Geometry.EPSILON);
		Assert.assertEquals(0.0, buf[9], Geometry.EPSILON);
	}
	
	@Test
	public void testCircleLineIntersectionCalculatorCase2() {
		double buf[] = new double[10];
		buf[0] = 2.0; // a
		buf[1] = 0.0; // b
		buf[2] = 4.0; // r^2
		
		buf[3] = 1.0;
		buf[4] = 0.0;
		buf[5] = -2.0;
		
		ArcOps.circleLineIntersection(0, 3, 6, buf);
		Assert.assertEquals(2.0, buf[6], Geometry.EPSILON);
		Assert.assertEquals(2.0, buf[7], Geometry.EPSILON);
		Assert.assertEquals(2.0, buf[8], Geometry.EPSILON);
		Assert.assertEquals(-2.0, buf[9], Geometry.EPSILON);
	}
	
	@Test
	public void testCircleLineIntersectionCalculatorCase3() {
		double buf[] = new double[10];
		buf[0] = 2.0; // a
		buf[1] = 0.0; // b
		buf[2] = 4.0; // r^2
		
		buf[3] = 1.0;
		buf[4] = 1.0;
		buf[5] = -2.0;
		
		ArcOps.circleLineIntersection(0, 3, 6, buf);
		Assert.assertEquals(2.0 + Math.sqrt(2.0), buf[6], Geometry.EPSILON);
		Assert.assertEquals(-Math.sqrt(2.0), buf[7], Geometry.EPSILON);
		Assert.assertEquals(2.0 - Math.sqrt(2.0), buf[8], Geometry.EPSILON);
		Assert.assertEquals(Math.sqrt(2.0), buf[9], Geometry.EPSILON);
	}
}
