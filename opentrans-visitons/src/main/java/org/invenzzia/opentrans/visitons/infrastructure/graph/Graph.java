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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.invenzzia.opentrans.visitons.infrastructure.ITrack;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;

/**
 * A repository for keeping all the tracks and vertices available on the map.
 * The content of the repository is updated through a synchronization process.
 * Synchronization is done with {@link EditableGraph} objects which are directly
 * editable snapshot of the pieces of the graph. Once the editing is completed,
 * the changes are saved in the infrastructure graph in one shot.
 * 
 * <p>The only direct operations available on the graph are related to reading
 * the content. The graph also emits interesting messages about adding and removing
 * vertices, which is necessary for the {@link World} object to update the mapping
 * of the graph contents to concrete world segments.
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
	
	/**
	 * Returns a track with the given ID or NULL.
	 * 
	 * @param id ID of the track.
	 * @return The track or NULL.
	 */
	public ITrack getTrack(long id) {
		return this.tracks.get(id);
	}
	
	/**
	 * Returns a vertex with the given ID or NULL.
	 * 
	 * @param id ID of the vertex.
	 * @return The vertex or NULL.
	 */
	public IVertex getVertex(long id) {
		return this.vertices.get(id);
	}
	
	/**
	 * Returns the number of vertices in the graph.
	 * 
	 * @return The number of vertices.
	 */
	public int countVertices() {
		return this.vertices.size();
	}
	
	/**
	 * Returns the number of tracks.
	 * 
	 * @return Number of tracks in the graph.
	 */
	public int countTracks() {
		return this.tracks.size();
	}
	
	/**
	 * Returns an immutable view of tracks.
	 * 
	 * @return Immutable view of tracks.
	 */
	public Collection<ITrack> tracks() {
		return this.tracks.values();
	}
	
	/**
	 * Returns an immutable view of vertices.
	 * 
	 * @return Immutable view of vertices. 
	 */
	public Collection<IVertex> vertices() {
		return this.vertices.values();
	}
	
	/**
	 * Performs a synchronization of an editable graph with the infrastructure graph. This is the only mechanism
	 * of updating the infrastructure graph. The method must deal with the following cases:
	 * 
	 * <ul>
	 *  <li>new vertices and tracks have appeared,</li>
	 *  <li>existing vertices have changed locations,</li>
	 *  <li>existing vertices and tracks have been removed.</li>
	 * </ul>
	 * 
	 * @param eg The editable graph to synchronize.
	 */
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
				track.setId(this.nextTrackId++);
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
