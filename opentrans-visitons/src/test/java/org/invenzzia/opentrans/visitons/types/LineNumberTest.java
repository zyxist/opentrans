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

public class LineNumberTest {
	
	@Test
	public void testParsing() {
		LineNumber ln = LineNumber.parseString("1");
		Assert.assertEquals(1, ln.getNumerical());
		Assert.assertTrue(ln.isNumericalPresent());
		Assert.assertNull(ln.getAlphanumerical());
		
		ln = LineNumber.parseString("1A");
		Assert.assertEquals(1, ln.getNumerical());
		Assert.assertTrue(ln.isNumericalPresent());
		Assert.assertEquals("A", ln.getAlphanumerical());
		
		ln = LineNumber.parseString("A1");
		Assert.assertEquals(0, ln.getNumerical());
		Assert.assertFalse(ln.isNumericalPresent());
		Assert.assertEquals("A1", ln.getAlphanumerical());
	}
	
	@Test
	public void testCheckingEquality() {
		LineNumber fst = LineNumber.parseString("1");
		LineNumber sec = LineNumber.parseString("1");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = LineNumber.parseString("1A");
		sec = LineNumber.parseString("1A");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = LineNumber.parseString("A1");
		sec = LineNumber.parseString("A1");
		Assert.assertTrue(fst.equals(sec));
		Assert.assertTrue(sec.equals(fst));
		
		fst = LineNumber.parseString("1A");
		sec = LineNumber.parseString("A1");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
		
		fst = LineNumber.parseString("1");
		sec = LineNumber.parseString("A1");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
		
		fst = LineNumber.parseString("1");
		sec = LineNumber.parseString("1A");
		Assert.assertFalse(fst.equals(sec));
		Assert.assertFalse(sec.equals(fst));
	}
	
	@Test
	public void testCompareLineNumbersSameCategory() {
		LineNumberComparator cmp = LineNumberComparator.get();
		
		LineNumber fst = LineNumber.parseString("1");
		LineNumber sec = LineNumber.parseString("2");
		LineNumber trd = LineNumber.parseString("2");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		
		fst = LineNumber.parseString("A");
		sec = LineNumber.parseString("B");
		trd = LineNumber.parseString("B");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		
		fst = LineNumber.parseString("AA");
		sec = LineNumber.parseString("AB");
		trd = LineNumber.parseString("AB");
		LineNumber fth = LineNumber.parseString("BA");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(fth, fst));
	}
	
	@Test
	public void testCompareLineNumbersMixedCategories() {
		LineNumberComparator cmp = LineNumberComparator.get();
		
		LineNumber fst = LineNumber.parseString("1A");
		LineNumber sec = LineNumber.parseString("2A");
		LineNumber trd = LineNumber.parseString("2A");
		LineNumber fth = LineNumber.parseString("1B");
		
		Assert.assertEquals(0, cmp.compare(sec, trd));
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(sec, fst));
		Assert.assertEquals(-1, cmp.compare(fst, fth));
		Assert.assertEquals(1, cmp.compare(fth, fst));
		
		Assert.assertEquals(1, cmp.compare(sec, fth));
		Assert.assertEquals(-1, cmp.compare(fth, sec));
	}
	
	@Test
	public void testCompareLineNumbersDifferentCategories() {
		LineNumberComparator cmp = LineNumberComparator.get();
		
		LineNumber fst = LineNumber.parseString("1A");
		LineNumber sec = LineNumber.parseString("A");
		
		Assert.assertEquals(-1, cmp.compare(fst, sec));
		Assert.assertEquals(1, cmp.compare(sec, fst));
	}
}
