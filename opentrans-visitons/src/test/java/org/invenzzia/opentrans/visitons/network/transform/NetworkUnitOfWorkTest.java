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

package org.invenzzia.opentrans.visitons.network.transform;

import junit.framework.Assert;
import org.invenzzia.opentrans.visitons.network.Segment;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class NetworkUnitOfWorkTest {
	private Track t1;
	private Track t2;
	private Track t3;
	
	private Vertex v1;
	private Vertex v2;
	private Vertex v3;
	private Vertex v4;
	
	@Before
	public void populateWorldModel() {
		Segment segment = new Segment(0, 0);
		
		this.v1 = new Vertex();
		this.v1.setId(1);
		this.v1.setPos(new SegmentCoordinate(segment, 10.0, 10.0));
		
		this.v2 = new Vertex();
		this.v2.setId(2);
		this.v2.setPos(new SegmentCoordinate(segment, 20.0, 20.0));
		
		this.v3 = new Vertex();
		this.v3.setId(3);
		this.v3.setPos(new SegmentCoordinate(segment, 30.0, 20.0));
		
		this.v4 = new Vertex();
		this.v4.setId(4);
		this.v4.setPos(new SegmentCoordinate(segment, 40.0, 10.0));
		
		this.t1 = new Track();
		this.t1.setId(1);
		this.t1.setVertices(v1, v2);
		
		this.t2 = new Track();
		this.t2.setId(2);
		this.t2.setVertices(v2, v3);
		
		this.t3 = new Track();
		this.t3.setId(3);
		this.t3.setVertices(v3, v4);
		
		this.v1.setTracks(t1, null);
		this.v2.setTracks(t1, t2);
		this.v3.setTracks(t2, t3);
		this.v4.setTracks(t3, null);
	}
	
	private World buildWorldMock() {
		World world = mock(World.class);
		when(world.findVertex(1)).thenReturn(this.v1);
		when(world.findVertex(2)).thenReturn(this.v2);
		when(world.findVertex(3)).thenReturn(this.v3);
		when(world.findVertex(4)).thenReturn(this.v4);
		
		when(world.findTrack(1)).thenReturn(this.t1);
		when(world.findTrack(2)).thenReturn(this.t2);
		when(world.findTrack(3)).thenReturn(this.t3);
		
		return world;
	}

	@Test
	public void testImportingVerticesMostTrivialCase() {
		NetworkUnitOfWork uw = new NetworkUnitOfWork();
		World world = this.buildWorldMock();
		
		Assert.assertEquals(0, uw.getVertexNum());
		Assert.assertEquals(0, uw.getTrackNum());
		VertexRecord record = uw.importVertex(world, 2);
		Assert.assertEquals(1, uw.getVertexNum());
		Assert.assertEquals(0, uw.getTrackNum());
		
		Assert.assertEquals(2, record.getId());
		Assert.assertNull(record.getFirstTrack());
		Assert.assertNull(record.getSecondTrack());
		Assert.assertEquals(1, record.getFirstTrackId());
		Assert.assertEquals(2, record.getSecondTrackId());
		
		Assert.assertSame(record, uw.importVertex(world, 2));
	}
	
	@Test
	public void testImportingVerticesNeighbourVertex() {
		NetworkUnitOfWork uw = new NetworkUnitOfWork();
		World world = this.buildWorldMock();
		
		Assert.assertEquals(0, uw.getVertexNum());
		Assert.assertEquals(0, uw.getTrackNum());
		VertexRecord record = uw.importVertex(world, 2);
		Assert.assertEquals(1, uw.getVertexNum());
		Assert.assertEquals(0, uw.getTrackNum());
		
		VertexRecord record2 = uw.importVertex(world, 3);
		Assert.assertEquals(2, uw.getVertexNum());
		Assert.assertEquals(1, uw.getTrackNum());
		Assert.assertEquals(3, record2.getId());
		Assert.assertNotNull(record2.getFirstTrack());
		Assert.assertNull(record2.getSecondTrack());
		Assert.assertEquals(0, record2.getFirstTrackId());
		Assert.assertEquals(3, record2.getSecondTrackId());
		
		Assert.assertSame(record2, uw.importVertex(world, 3));
		
		TrackRecord tr = uw.findTrack(2);
		Assert.assertEquals(2, tr.getId());
		Assert.assertSame(record, tr.getFirstVertex());
		Assert.assertSame(record2, tr.getSecondVertex());
	}

	@Test
	public void testImportingTracks() {
		NetworkUnitOfWork uw = new NetworkUnitOfWork();
		World world = this.buildWorldMock();
		
		TrackRecord tr = uw.importTrack(world, 2);
		VertexRecord vr2 = uw.findVertex(2);
		VertexRecord vr3 = uw.findVertex(3);
		
		Assert.assertEquals(2, uw.getVertexNum());
		Assert.assertEquals(1, uw.getTrackNum());
		
		Assert.assertNotNull(tr);
		Assert.assertNotNull(vr2);
		Assert.assertNotNull(vr3);
		
		Assert.assertEquals(2, tr.getId());
		Assert.assertEquals(0, tr.getType());
		
		Assert.assertSame(vr2, tr.getFirstVertex());
		Assert.assertSame(vr3, tr.getSecondVertex());
		Assert.assertSame(tr, vr2.getSecondTrack());
		Assert.assertSame(tr, vr3.getFirstTrack());
		
		TrackRecord tr2 = uw.importTrack(world, 2);
		Assert.assertSame(tr, tr2);
	}
	
	@Test
	public void testImportingTracksOneVertexAlreadyExists() {
		NetworkUnitOfWork uw = new NetworkUnitOfWork();
		World world = this.buildWorldMock();
		
		VertexRecord vr3a = uw.importVertex(world, 3);
		TrackRecord tr = uw.importTrack(world, 2);
		VertexRecord vr2 = uw.findVertex(2);
		VertexRecord vr3 = uw.findVertex(3);
		
		Assert.assertSame(vr3a, vr3);
		Assert.assertEquals(2, uw.getVertexNum());
		Assert.assertEquals(1, uw.getTrackNum());
		
		Assert.assertNotNull(tr);
		Assert.assertNotNull(vr2);
		Assert.assertNotNull(vr3);
		
		Assert.assertEquals(2, tr.getId());
		Assert.assertEquals(0, tr.getType());
		
		Assert.assertSame(vr2, tr.getFirstVertex());
		Assert.assertSame(vr3, tr.getSecondVertex());
		Assert.assertSame(tr, vr2.getSecondTrack());
		Assert.assertSame(tr, vr3.getFirstTrack());
	}
}
