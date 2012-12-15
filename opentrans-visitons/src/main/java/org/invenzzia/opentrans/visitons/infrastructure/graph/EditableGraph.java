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

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.invenzzia.opentrans.visitons.exception.GraphException;
import org.invenzzia.opentrans.visitons.infrastructure.GhostVertex;
import org.invenzzia.opentrans.visitons.infrastructure.ITrack;
import org.invenzzia.opentrans.visitons.infrastructure.IVertex;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class EditableGraph {
	/**
	 * List of tracks forked from the network graph.
	 */
	private Map<Long, ITrack> existingTracks;
	/**
	 * List of the vertices forked from the network graph.
	 */
	private Map<Long, IVertex> existingVertices;
	/**
	 * List of tracks that are not present yet in the network graph and do not have their ID numbers.
	 */
	private List<ITrack> newTracks;
	/**
	 * List of vertices that are not present yet in the network graph and do not have their ID numbers.
	 */
	private List<IVertex> newVertices;

	public EditableGraph() {
		this.existingTracks = new LinkedHashMap<>();
		this.existingVertices = new LinkedHashMap<>();
		this.newTracks = new LinkedList<>();
		this.newVertices = new LinkedList<>();
	}
	
	/**
	 * Forks the vertex from the network graph to this editable graph copy, together
	 * with all the tracks. This should be the only method of moving data from the network
	 * graph to editable models.
	 * 
	 * @param vertex The vertex from the network graph to fork in this editable model.
	 * @return The forked vertex.
	 */
	public IVertex fork(IVertex vertex) {
		IVertex ourVertex = (IVertex) vertex.fork();
		
		for(ITrack track: ourVertex.getTracks()) {
			ITrack ourTrack = this.existingTracks.get(track.getId());
			if(null == ourTrack) {
				// This track is not a part of this editable graph yet, we must copy it.
				ourTrack = (ITrack) track.fork();
				this.replaceVertexInTrack(ourTrack, 0);
				this.replaceVertexInTrack(ourTrack, 1);
				
				this.existingTracks.put(ourTrack.getId(), ourTrack);
			} else {
				// OK, this track is present, but it uses ghost vertex to link to us. 
				// We must replace the ghost vertex.
				if(ourTrack.getVertex(0).getId() == ourVertex.getId()) {
					ourTrack.setVertex(0, ourVertex);
				} else {
					ourTrack.setVertex(1, ourVertex);
				}
			}
		}
		this.existingVertices.put(ourVertex.getId(), ourVertex);
		return ourVertex;
	}

	/**
	 * Adds a new vertex to the editable graph. By definition, this vertex must not exist in the actual network
	 * graph, that is it must have ID = -1. For importing existing vertices, use {@link fork} method.
	 * 
	 * @param vertex 
	 */
	public void addVertex(IVertex vertex) {
		Preconditions.checkArgument(vertex.getId() == -1, "For existing vertices, use fork().");
		this.newVertices.add(vertex);
	}

	/**
	 * Adds a new track to the editable graph. By definition, this vertex must not exist in the actual network
	 * graph, that is it must have ID = -1. For importing existing tracks, use {@link fork} method on one of the
	 * vertices.
	 * 
	 * @param track 
	 */
	public void addTrack(ITrack track) {
		this.newTracks.add(track);
	}

	/**
	 * Moves a vertex by a vector, if the graph geometry allows this operation. The vertex must belong to this
	 * graph. For moving a group of vertices, use the other variants of this method.
	 * 
	 * @param vertex A vertex to move.
	 * @param dx Move vector X.
	 * @param dy Move vector Y.
	 * @return True, if the vertex has been successfully moved.
	 * @throws GraphException 
	 */
	public boolean moveByVector(IVertex vertex, double dx, double dy) throws GraphException {
		Preconditions.checkNotNull(vertex);
		if(!this.existingVertices.containsValue(vertex) && !this.newVertices.contains(vertex)) {
			throw new GraphException(String.format("The vertex %d does not belong to this graph.", vertex.getId()));
		}
		throw new UnsupportedOperationException("Moving by vector not implemented yet.");
	/*	vertex.registerUpdate(null, dx, dy);
		try {
			if(!vertex.isUpdatePossible()) {
				vertex.rollbackUpdate();
				return false;
			}
		} catch(Throwable thr) {
			vertex.rollbackUpdate();
			if(thr instanceof RuntimeException) {
				throw thr;
			}
			return false;
		}
		vertex.applyUpdate();
		return true;
		*/
	}

	/**
	 * Moves a group of vertices from this editable graph by a vector, if the editable graph geometry allows it.
	 * Before moving, the method asks each vertex whether such a move is possible. If not, the method does not modify
	 * any permanent state and returns false.
	 * 
	 * @param customVertices Collection of vertices belonging to this graph.
	 * @param dx Move vector X.
	 * @param dy Move vector Y.
	 * @return True, if the operation was successful.
	 * @throws GraphException 
	 */
	public boolean moveByVector(Collection<IVertex> customVertices, double dx, double dy) throws GraphException {
		Preconditions.checkNotNull(customVertices);
		for(IVertex vertex: customVertices) {
			if(!this.existingVertices.containsValue(vertex) && !this.newVertices.contains(vertex)) {
				throw new GraphException(String.format("The vertex %d does not belong to this graph.", vertex.getId()));
			}
		}
		return this.moveByVectorImpl(customVertices, dx, dy);
	}

	/**
	 * Moves all the vertices from this editable by a vector, if the editable graph geometry allows it.
	 * Before moving, the method asks each vertex whether such a move is possible. If not, the method does not modify
	 * any permanent state and returns false.
	 * 
	 * @param dx Move vector X.
	 * @param dy Move vector Y.
	 * @return True, if the operation was successful.
	 * @throws GraphException 
	 */
	public boolean moveByVector(double x, double y) throws GraphException {
		List<IVertex> verticesToMove = new ArrayList<>(this.existingVertices.size() + this.newVertices.size());
		verticesToMove.addAll(this.newVertices);
		verticesToMove.addAll(this.existingVertices.values());
		return this.moveByVectorImpl(verticesToMove, x, y);
	}
	
	private boolean moveByVectorImpl(Collection<IVertex> vertices, double dx, double dy) throws GraphException {
		throw new UnsupportedOperationException("Moving by vector not implemented yet.");
		/*
		for(IVertex vertex: vertices) {
			vertex.registerUpdate(dx, dy);
		}
		boolean ok = true;
		for(IVertex vertex: vertices) {
			if(!vertex.isUpdatePossible()) {
				ok = false;
				break;
			}
		}
		if(!ok) {
			for(IVertex vertex: vertices) {
				vertex.rollbackUpdate();
			}
		} else {
			for(IVertex vertex: vertices) {
				vertex.applyUpdate();
			}
		}
		return true;*/
	}

	/**
	 * @return All tracks from this graph, both existing and new ones.
	 */
	public Collection<ITrack> getTracks() {
		List<ITrack> tracksToMove = new ArrayList<>(this.existingTracks.size() + this.newTracks.size());
		tracksToMove.addAll(this.newTracks);
		tracksToMove.addAll(this.existingTracks.values());
		return tracksToMove;
	}

	/**
	 * @return All vertices from this graph, both existing and the new ones.
	 */
	public Collection<IVertex> getVertices() {
		List<IVertex> verticesToMove = new ArrayList<>(this.existingVertices.size() + this.newVertices.size());
		verticesToMove.addAll(this.newVertices);
		verticesToMove.addAll(this.existingVertices.values());
		return verticesToMove;
	}
	
	/**
	 * Used by the forking method to copy the endings of the track. If one of the track vertices
	 * is already added to this editable graph, it is chosen; otherwise a ghost vertex is created
	 * which delegates some readable operations to the network graph.
	 * 
	 * @param track The track to operate on.
	 * @param id The number of vertex to check.
	 */
	private void replaceVertexInTrack(ITrack track, int id) {
		IVertex v = track.getVertex(id);
		IVertex ourVertex = this.existingVertices.get(v.getId());
		if(null == ourVertex) {
			ourVertex = new GhostVertex(v);
		}
		track.setVertex(id, ourVertex);
	}
}
