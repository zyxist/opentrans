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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.invenzzia.opentrans.visitons.infrastructure.ITrack;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Graph {
	private Map<Long, ITrack> tracks;
	private Map<Long, IVertex> vertices;
	
	private long nextVertexId = 0;
	private long nextTrackId = 0;
	
	public Graph() {
		this.tracks = new LinkedHashMap<>();
		this.vertices = new LinkedHashMap<>();
	}
	
	public Graph(long nextVertexId, long nextTrackId) {
		this();
		
		this.nextTrackId = nextTrackId;
		this.nextVertexId = nextVertexId;
	}
	
	public long getNextVertexId() {
		return this.nextVertexId;
	}
	
	public long getNextTrackId() {
		return this.nextTrackId;
	}
	
	public void synchronizeWith(EditableGraph eg) {
		Set<IVertex> mappedVertices = new HashSet<>();
		Set<ITrack> mappedTracks = new HashSet<>();
		
		// Synchronize the data that is not related to graph connections
		// and move all the new vertices.
		for(IVertex vertex: eg.getVertices()) {
			if(vertex.getId() == -1) {
				vertex.setId(this.nextVertexId++);
				IVertex copy = (IVertex) vertex.fork();
				
				mappedVertices.add(copy);
				this.vertices.put(copy.getId(), copy);
			} else {
				if(vertex.isDeleted()) {
					this.vertices.remove(vertex.getId());
				} else {
					IVertex copy = this.vertices.get(vertex.getId());
					copy.copyFrom(vertex);
					mappedVertices.add(copy);
				}
			}
		}
		for(ITrack track: eg.getTracks()) {
			if(track.getId() == -1) {
				track.setId(this.nextVertexId++);
				ITrack copy = (ITrack) track.fork();
				
				mappedTracks.add(copy);
				this.tracks.put(copy.getId(), copy);
			} else {
				if(track.isDeleted()) {
					this.tracks.remove(track.getId());
				} else {
					ITrack copy = this.tracks.get(track.getId());
					copy.copyFrom(track);
					mappedTracks.add(copy);
				}
			}
		}
		
		// Now we can link the tracks and vertices again.
		for(IVertex vertex: mappedVertices) {
			ITrack vertexTracks[] = vertex.getTracks();
			for(int i = 0; i < vertexTracks.length; i++) {
				vertex.setTrack(i, this.tracks.get(vertexTracks[i].getId()));
			}
		}
		for(ITrack track: mappedTracks) {
			track.setVertex(0, this.vertices.get(track.getVertex(0).getId()));
			track.setVertex(1, this.vertices.get(track.getVertex(1).getId()));
		}
	}
}
