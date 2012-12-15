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
package org.invenzzia.opentrans.visitons.infrastructure.graph;

import junit.framework.Assert;
import org.invenzzia.opentrans.visitons.infrastructure.CurvedTrack;
import static org.mockito.Mockito.*;
import org.invenzzia.opentrans.visitons.infrastructure.ITrack;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;
import org.invenzzia.opentrans.visitons.infrastructure.StraightTrack;
import org.invenzzia.opentrans.visitons.infrastructure.Vertex;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.junit.Test;

public class GraphTest {
	@Test
	public void testSynchronizingNewTracksAndVertices() {
		Graph graph = new Graph();
		EditableGraph editableGraph = new EditableGraph();
		
		Vertex v1 = new Vertex(-1, 1, mock(Segment.class), 1.0, 1.0);
		Vertex v2 = new Vertex(-1, 1, mock(Segment.class), 3.0, 3.0);
		ITrack track = new StraightTrack(-1);
		v1.setTrack(0, track);
		v2.setTrack(0, track);
		track.setVertex(0, v1);
		track.setVertex(1, v2);
		
		editableGraph.addTrack(track);
		editableGraph.addVertex(v1);
		editableGraph.addVertex(v2);
		
		Assert.assertEquals(0, graph.countVertices());
		Assert.assertEquals(0, graph.countTracks());
		graph.synchronizeWith(editableGraph);
		
		Assert.assertEquals(2, graph.countVertices());
		Assert.assertEquals(1, graph.countTracks());
		
		IVertex v1a = graph.getVertex(0);
		IVertex v2a = graph.getVertex(1);
		ITrack tracka = graph.getTrack(0);
		Assert.assertNotNull(v1a);
		Assert.assertNotNull(v2a);
		Assert.assertNotNull(tracka);
		
		Assert.assertSame(tracka, v1a.getTrack(0));
		Assert.assertSame(tracka, v2a.getTrack(0));
		Assert.assertSame(v1a, tracka.getVertex(0));
		Assert.assertSame(v2a, tracka.getVertex(1));
	}
	
	@Test
	public void testSynchronizingExistingTracksAndVertices() throws Exception {
		Graph graph = new Graph();
		EditableGraph editableGraph = new EditableGraph();
		Segment s = mock(Segment.class);
		
		Vertex v1 = new Vertex(-1, 1, s, 1.0, 1.0);
		Vertex v2 = new Vertex(-1, 1, s, 3.0, 3.0);
		ITrack track = new StraightTrack(-1);
		v1.setTrack(0, track);
		v2.setTrack(0, track);
		track.setVertex(0, v1);
		track.setVertex(1, v2);
		
		editableGraph.addTrack(track);
		editableGraph.addVertex(v1);
		editableGraph.addVertex(v2);
		graph.synchronizeWith(editableGraph);
		
		// Now the actual part.
		editableGraph = new EditableGraph();
		IVertex v1a = editableGraph.fork(graph.getVertex(0));
		IVertex v2a = editableGraph.fork(graph.getVertex(1));
		
		v1a.registerUpdate(s, 2.0, 2.0);
		v2a.registerUpdate(s, 4.0, 4.0);
		v1a.applyUpdate();
		v2a.applyUpdate();
		
		Assert.assertEquals(1.0, graph.getVertex(0).x());
		Assert.assertEquals(1.0, graph.getVertex(0).y());
		Assert.assertEquals(3.0, graph.getVertex(1).x());
		Assert.assertEquals(3.0, graph.getVertex(1).y());
		
		graph.synchronizeWith(editableGraph);
		Assert.assertEquals(2, graph.countVertices());
		Assert.assertEquals(1, graph.countTracks());
		Assert.assertEquals(2.0, graph.getVertex(0).x());
		Assert.assertEquals(2.0, graph.getVertex(0).y());
		Assert.assertEquals(4.0, graph.getVertex(1).x());
		Assert.assertEquals(4.0, graph.getVertex(1).y());
	}
	
	@Test
	public void testSynchronizingRemovals() {
		Graph graph = new Graph();
		EditableGraph editableGraph = new EditableGraph();
		Segment s = mock(Segment.class);
		
		Vertex v1 = new Vertex(-1, 1, s, 1.0, 1.0);
		Vertex v2 = new Vertex(-1, 2, s, 3.0, 3.0);
		Vertex v3 = new Vertex(-1, 1, s, 5.0, 5.0);
		ITrack track1 = new StraightTrack(-1);
		ITrack track2 = new CurvedTrack(-1);
		v1.setTrack(0, track1);
		v2.setTrack(0, track1);
		v2.setTrack(1, track2);
		v3.setTrack(0, track2);
		track1.setVertex(0, v1);
		track1.setVertex(1, v2);
		track2.setVertex(0, v2);
		track2.setVertex(1, v3);
		
		editableGraph.addTrack(track1);
		editableGraph.addTrack(track2);
		editableGraph.addVertex(v1);
		editableGraph.addVertex(v2);
		editableGraph.addVertex(v3);
		graph.synchronizeWith(editableGraph);
		
		Assert.assertEquals(3, graph.countVertices());
		Assert.assertEquals(2, graph.countTracks());
		
		// Now the actual part.
		editableGraph = new EditableGraph();
		IVertex v3a = editableGraph.fork(graph.getVertex(2));
		v3a.markAsDeleted();
		
		graph.synchronizeWith(editableGraph);
		Assert.assertEquals(2, graph.countVertices());
		Assert.assertEquals(1, graph.countTracks());
		Assert.assertNull(graph.getVertex(3));
		Assert.assertNull(graph.getTrack(2));
		Assert.assertNotNull(graph.getVertex(2));
		Assert.assertEquals(1, graph.getVertex(2).getTrackCount());
	}
	
	@Test
	public void testExistingDataNoLongerExist() {
		
	}
	
	@Test
	public void testSynchronizingGhostVertices() {
		
	}
}
