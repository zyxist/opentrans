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

package org.invenzzia.opentrans.visitons.types;

import org.junit.Assert;
import org.junit.Test;

public class RouteNumberTest {
	
	@Test
	public void testParsing() {
		RouteNumber ln = RouteNumber.parseString("1");
		Assert.assertEquals(1, ln.getNumerical());
		Assert.assertTrue(ln.isNumericalPresent());
		Assert.assertNull(ln.getAlphanumerical());
		
		ln = RouteNumber.parseString("1A");
		Assert.assertEquals(1, ln.getNumerical());
		Assert.assertTrue(ln.isNumericalPresent());
		Assert.assertEquals("A", ln.getAlphanumerical());
		
		ln = RouteNumber.parseString("A1");
		Assert.assertEquals(0, ln.getNumerical());
		Assert.assertFalse(ln.isNumericalPresent());
		Assert.assertEquals("A1", ln.getAlphanumerical());
	}
	
	@Test
	public void testCheckingEquality() {
		RouteNumber fst = RouteNumber.parseString("1");
		RouteNumber sec = RouteNumber.parseString("1");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = RouteNumber.parseString("1A");
		sec = RouteNumber.parseString("1A");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = RouteNumber.parseString("A1");
		sec = RouteNumber.parseString("A1");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = RouteNumber.parseString("1A");
		sec = RouteNumber.parseString("A1");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
		
		fst = RouteNumber.parseString("1");
		sec = RouteNumber.parseString("A1");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
		
		fst = RouteNumber.parseString("1");
		sec = RouteNumber.parseString("1A");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
	}
	
	@Test
	public void testCompareRouteNumbersSameCategory() {
		RouteNumberComparator cmp = RouteNumberComparator.get();
		
		RouteNumber fst = RouteNumber.parseString("1");
		RouteNumber sec = RouteNumber.parseString("2");
		RouteNumber trd = RouteNumber.parseString("2");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		
		fst = RouteNumber.parseString("A");
		sec = RouteNumber.parseString("B");
		trd = RouteNumber.parseString("B");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		
		fst = RouteNumber.parseString("AA");
		sec = RouteNumber.parseString("AB");
		trd = RouteNumber.parseString("AB");
		RouteNumber fth = RouteNumber.parseString("BA");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(fth, fst));
	}
	
	@Test
	public void testCompareRouteNumbersMixedCategories() {
		RouteNumberComparator cmp = RouteNumberComparator.get();
		
		RouteNumber fst = RouteNumber.parseString("1A");
		RouteNumber sec = RouteNumber.parseString("2A");
		RouteNumber trd = RouteNumber.parseString("2A");
		RouteNumber fth = RouteNumber.parseString("1B");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, fth));
		Assert.assertEquals(1, cmp.compare(fth, fst));
		
		Assert.assertEquals(1, cmp.compare(sec, fth));
		Assert.assertEquals(-1, cmp.compare(fth, sec));
	}
	
	@Test
	public void testCompareRouteNumbersDifferentCategories() {
		RouteNumberComparator cmp = RouteNumberComparator.get();
		
		RouteNumber fst = RouteNumber.parseString("1A");
		RouteNumber sec = RouteNumber.parseString("A");
		
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(sec, fst));
	}
}
