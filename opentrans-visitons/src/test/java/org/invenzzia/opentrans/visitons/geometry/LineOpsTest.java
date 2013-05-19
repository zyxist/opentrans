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
import junit.framework.AssertionFailedError;
import org.junit.Test;

public class LineOpsTest {
	
	@Test
	public void testHorizontalReordering() {
		double buffer[] = new double[8];
		
		buffer[0] = 0.0;
		buffer[1] = 0.0;
		buffer[2] = -3.0;
		buffer[3] = 0.0;
		buffer[4] = 6.0;
		buffer[5] = 0.0;
		buffer[6] = 4.0;
		buffer[7] = 0.0;
		
		LineOps.reorder(buffer, 0, 2, 4, 6);
		Assert.assertEquals(-3.0, buffer[0], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[1], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[2], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[3], Geometry.EPSILON);
		Assert.assertEquals(4.0, buffer[4], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[5], Geometry.EPSILON);
		Assert.assertEquals(6.0, buffer[6], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[7], Geometry.EPSILON);
	}
	
	@Test
	public void testVerticalReordering() {
		double buffer[] = new double[8];
		
		buffer[0] = 2.0;
		buffer[1] = 0.0;
		buffer[2] = 2.0;
		buffer[3] = -3.0;
		buffer[4] = 2.0;
		buffer[5] = 6.0;
		buffer[6] = 2.0;
		buffer[7] = 4.0;
		
		LineOps.reorder(buffer, 0, 2, 4, 6);
		Assert.assertEquals(2.0, buffer[0], Geometry.EPSILON);
		Assert.assertEquals(-3.0, buffer[1], Geometry.EPSILON);
		Assert.assertEquals(2.0, buffer[2], Geometry.EPSILON);
		Assert.assertEquals(0.0, buffer[3], Geometry.EPSILON);
		Assert.assertEquals(2.0, buffer[4], Geometry.EPSILON);
		Assert.assertEquals(4.0, buffer[5], Geometry.EPSILON);
		Assert.assertEquals(2.0, buffer[6], Geometry.EPSILON);
		Assert.assertEquals(6.0, buffer[7], Geometry.EPSILON);
	}
	
	@Test
	public void testReoderingIsIndependentFromInitialOrder() {
		double buffer[];
		double expected[] = new double[]{ 1.0, -100.0, 3.0, -60.0, 6.0, 0.0, 10.0, 80.0 };;
		
		buffer = new double[]{ 1.0, -100.0, 3.0, -60.0, 6.0, 0.0, 10.0, 80.0 };
		LineOps.reorder(buffer, 0, 2, 4, 6);
		this.assertArrayEquals(expected, buffer);
		buffer = new double[]{ 3.0, -60.0, 6.0, 0.0, 1.0, -100.0, 10.0, 80.0 };
		LineOps.reorder(buffer, 0, 2, 4, 6);
		this.assertArrayEquals(expected, buffer);
		buffer = new double[]{ 6.0, 0.0, 3.0, -60.0, 1.0, -100.0, 10.0, 80.0 };
		LineOps.reorder(buffer, 0, 2, 4, 6);
		this.assertArrayEquals(expected, buffer);
		buffer = new double[]{ 6.0, 0.0, 10.0, 80.0, 3.0, -60.0, 1.0, -100.0 };
		LineOps.reorder(buffer, 0, 2, 4, 6);
		this.assertArrayEquals(expected, buffer);
	}
	
	@Test
	public void testFindingGeneralFormFromTangent() {
		double buf[] = new double[3];
		LineOps.toGeneral(1.0, 1.0, Math.toRadians(45.0), 0, buf);
		
		Assert.assertEquals(0.0, buf[0] * 0.0 + buf[1] * 0.0 + buf[2], 0.01);
		Assert.assertEquals(0.0, buf[0] * 5.0 + buf[1] * 5.0 + buf[2], 0.01);
		Assert.assertEquals(0.0, buf[0] * -3.0 + buf[1] * -3.0 + buf[2], 0.01);
		
		LineOps.toGeneral(25.0, 100.0, Math.toRadians(45.0), 0, buf);	
		Assert.assertEquals(0.0, buf[0] * 50.0 + buf[1] * 125.0 + buf[2], 0.01);
	}
	
	@Test
	public void testCalculatingLineIntersections() {
		double buffer[] = new double[8];
		buffer[0] = 1.0;
		buffer[1] = -1.0;
		buffer[2] = 75.0;
		buffer[3] = 1.12;
		buffer[4] = -1.0;
		buffer[5] = 94.0;
		LineOps.intersection(0, 3, 6, buffer);
		Assert.assertEquals(-158.33, buffer[6], 0.01);
		Assert.assertEquals(-83.33, buffer[7], 0.01);
	}
	
	@Test
	public void testToOrthogonalCrossesTheSpecifiedPoint() {
		double buffer[] = new double[6];
		buffer[0] = 1.0;
		buffer[1] = -1.0;
		buffer[2] = 75.0;
		LineOps.toOrthogonal(0, 3, buffer, 50.0, 125.0);
		Assert.assertEquals(0.0, buffer[3] * 50.0 + buffer[4] * 125.0 + buffer[5], 0.01);
	}
	
	@Test
	public void testFindingDirection() {
		Assert.assertEquals(1, LineOps.onWhichSide(0.0, 0.0, 1.0, 0.0, 2.0, 2.0));
		Assert.assertEquals(-1, LineOps.onWhichSide(0.0, 0.0, 1.0, 0.0, 2.0, -2.0));
		Assert.assertEquals(0, LineOps.onWhichSide(0.0, 0.0, 1.0, 0.0, 2.0, 0.0));
		
		Assert.assertEquals(1, LineOps.onWhichSide(3.0, 3.0, 4.0, 4.0, 5.0, 6.0));
		Assert.assertEquals(-1, LineOps.onWhichSide(3.0, 3.0, 4.0, 4.0, 5.0, 4.0));
		Assert.assertEquals(0, LineOps.onWhichSide(3.0, 3.0, 4.0, 4.0, 5.0, 5.0));
	}
	
	private void assertArrayEquals(double[] expected, double actual[]) {
		Assert.assertEquals(expected.length, actual.length);
		try {
			for(int i = 0; i < expected.length; i++) {
				Assert.assertEquals(expected[i], actual[i]);
			}
		} catch(AssertionFailedError error) {
			boolean first = true;
			StringBuilder sb = new StringBuilder("actual = [");
			for(int i = 0; i < actual.length; i++) {
				if(!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(actual[i]);
			}
			sb.append("]");
			System.err.println(sb.toString());
			throw error;
		}
	}
}
