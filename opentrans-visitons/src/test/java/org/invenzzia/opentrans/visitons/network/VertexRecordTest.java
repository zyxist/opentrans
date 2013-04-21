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

import junit.framework.Assert;
import org.junit.Test;


public class VertexRecordTest {

	@Test
	public void testHasNoTracksWorksCorrectly() {
		VertexRecord vr = new VertexRecord();
		
		Assert.assertTrue(vr.hasNoTracks());
		
		TrackRecord t1 = new TrackRecord();
		TrackRecord t2 = new TrackRecord();
		
		vr.addTrack(t1);
		Assert.assertFalse(vr.hasNoTracks());
		vr.addTrack(t2);
		Assert.assertFalse(vr.hasNoTracks());
	}
	
	@Test
	public void testHasOneTrackWorksCorrectly() {
		VertexRecord vr = new VertexRecord();
		
		Assert.assertFalse(vr.hasOneTrack());
		
		TrackRecord t1 = new TrackRecord();
		TrackRecord t2 = new TrackRecord();
		
		vr.addTrack(t1);
		Assert.assertTrue(vr.hasOneTrack());
		vr.addTrack(t2);
		Assert.assertFalse(vr.hasOneTrack());
	}
	
	@Test
	public void testHasAllTracksWorksCorrectly() {
		VertexRecord vr = new VertexRecord();
		
		Assert.assertFalse(vr.hasAllTracks());
		
		TrackRecord t1 = new TrackRecord();
		TrackRecord t2 = new TrackRecord();
		
		vr.addTrack(t1);
		Assert.assertFalse(vr.hasAllTracks());
		vr.addTrack(t2);
		Assert.assertTrue(vr.hasAllTracks());
	}
	
	@Test
	public void testRemovingTracksWorksCorrectly() {
		VertexRecord vr = new VertexRecord();
		TrackRecord t1 = new TrackRecord();
		TrackRecord t2 = new TrackRecord();
		
		vr.addTrack(t1);
		vr.addTrack(t2);
		Assert.assertTrue(vr.hasAllTracks());
		vr.removeTrack(t1);
		Assert.assertTrue(vr.hasOneTrack());
		Assert.assertSame(t2, vr.getTrack());
		Assert.assertSame(t2, vr.getSecondTrack());
		Assert.assertNull(vr.getFirstTrack());
	}
}
