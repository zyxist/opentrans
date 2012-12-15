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
package org.invenzzia.opentrans.visitons.infrastructure;

import junit.framework.Assert;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VertexTest {
	@Test
	public void testForkingVertex() {
		Vertex original = new Vertex(55, 2, mock(Segment.class), 1.0, 1.0);
		ITrack t1 = new StraightTrack(-1);
		ITrack t2 = new CurvedTrack(-1);
		original.setTrack(0, t1);
		original.setTrack(1, t2);
		
		Vertex forked = original.fork();
		Assert.assertEquals(original.getTrackCount(), forked.getTrackCount());
		Assert.assertEquals(original.getId(), forked.getId());
		Assert.assertEquals(original.x(), forked.x());
		Assert.assertEquals(original.y(), forked.y());
		Assert.assertEquals(original.getSegment(), forked.getSegment());
		Assert.assertSame(t1, forked.getTrack(0));
		Assert.assertSame(t2, forked.getTrack(1));
	}
}
