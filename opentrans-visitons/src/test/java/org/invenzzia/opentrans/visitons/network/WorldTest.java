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

package org.invenzzia.opentrans.visitons.network;

import org.invenzzia.opentrans.visitons.network.World.HorizontalDir;
import org.invenzzia.opentrans.visitons.network.World.VerticalDir;
import org.junit.Assert;
import org.junit.Test;

public class WorldTest {
	@Test
	public void testCreatingTheWorld() {
		World world = new World();
		Assert.assertEquals(1, world.getX());
		Assert.assertEquals(1, world.getY());
		
		Segment segment = world.findSegment(0, 0);
		Assert.assertNotNull(segment);
		Assert.assertEquals(0, segment.getX());
		Assert.assertEquals(0, segment.getY());
	}
	
	@Test
	public void testCreatingTheWorldWithCustomDimensions() {
		World world = new World();
		world.construct(3, 4);
		Assert.assertEquals(3, world.getX());
		Assert.assertEquals(4, world.getY());
		
		Segment segment = world.findSegment(2, 3);
		Assert.assertNotNull(segment);
		Assert.assertEquals(2, segment.getX());
		Assert.assertEquals(3, segment.getY());
		
		segment = world.findSegment(1, 1);
		Assert.assertNotNull(segment);
		Assert.assertEquals(1, segment.getX());
		Assert.assertEquals(1, segment.getY());
	}
	
	@Test
	public void testExtendingTheWorldHorizontally() {
		World world = new World();
		Assert.assertEquals(1, world.getX());
		Assert.assertEquals(1, world.getY());
		Segment existingSegment = world.findSegment(0, 0);
		Assert.assertNotNull(existingSegment);
		Assert.assertEquals(0, existingSegment.getX());
		Assert.assertEquals(0, existingSegment.getY());
		
		world.extendHorizontally(HorizontalDir.LEFT);
		Assert.assertEquals(2, world.getX());
		Assert.assertEquals(1, world.getY());
		
		Segment newSegment = world.findSegment(0, 0);
		Assert.assertSame(existingSegment, world.findSegment(1, 0));
		Assert.assertEquals(1, existingSegment.getX());
		Assert.assertEquals(0, existingSegment.getY());
		
		Assert.assertNotNull(newSegment);
		Assert.assertEquals(0, newSegment.getX());
		Assert.assertEquals(0, newSegment.getY());
		
		world.extendHorizontally(HorizontalDir.RIGHT);
		newSegment = world.findSegment(2, 0);
		Assert.assertSame(existingSegment, world.findSegment(1, 0));
		Assert.assertEquals(1, existingSegment.getX());
		Assert.assertEquals(0, existingSegment.getY());
		
		Assert.assertNotNull(newSegment);
		Assert.assertEquals(2, newSegment.getX());
		Assert.assertEquals(0, newSegment.getY());
	}
	
	@Test
	public void testExtendingTheWorldVertically() {
		World world = new World();
		Assert.assertEquals(1, world.getX());
		Assert.assertEquals(1, world.getY());
		Segment existingSegment = world.findSegment(0, 0);
		Assert.assertNotNull(existingSegment);
		Assert.assertEquals(0, existingSegment.getX());
		Assert.assertEquals(0, existingSegment.getY());
		
		world.extendVertically(VerticalDir.UP);
		Assert.assertEquals(1, world.getX());
		Assert.assertEquals(2, world.getY());
		
		Segment newSegment = world.findSegment(0, 0);
		Assert.assertSame(existingSegment, world.findSegment(0, 1));
		Assert.assertEquals(0, existingSegment.getX());
		Assert.assertEquals(1, existingSegment.getY());
		
		Assert.assertNotNull(newSegment);
		Assert.assertEquals(0, newSegment.getX());
		Assert.assertEquals(0, newSegment.getY());
		
		world.extendVertically(VerticalDir.DOWN);
		newSegment = world.findSegment(0, 2);
		Assert.assertSame(existingSegment, world.findSegment(0, 1));
		Assert.assertEquals(0, existingSegment.getX());
		Assert.assertEquals(1, existingSegment.getY());
		
		Assert.assertNotNull(newSegment);
		Assert.assertEquals(0, newSegment.getX());
		Assert.assertEquals(2, newSegment.getY());
	}
	
	@Test
	public void testShrinkingTheWorldHorizontallyIfAllSegmentsAreUnused() {
		
	}
	
	@Test
	public void testShrinkingTheWorldHorizontallyIfSomeSegmentsAreUnused() {
		
	}
	
	@Test
	public void testShrinkingTheWorldVerticallyIfAllSegmentsAreUnused() {
		
	}
	
	@Test
	public void testShrinkingTheWorldVerticallyIfSomeSegmentsAreUnused() {
		
	}
}
