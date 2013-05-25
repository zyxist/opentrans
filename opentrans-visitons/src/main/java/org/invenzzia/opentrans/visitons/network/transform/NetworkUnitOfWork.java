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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.painters.CurvedTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.FreeTrackPainter;
import org.invenzzia.opentrans.visitons.render.painters.StraightTrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;

/**
 * Network unit of work is similar in its purpose to ordinary {@link UnitOfWork}
 * from Helium library. It keeps the local changes made by the GUI thread that
 * have not been synchronized with the model yet. It also provides all the necessary
 * data to undo the changes.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkUnitOfWork {
	/**
	 * List of the tracks modified or added in this session.
	 */
	private Map<Long, TrackRecord> tracks;
	/**
	 * List of vertices modified or added in this session.
	 */
	private Map<Long, VertexRecord> vertices;
	/**
	 * Removed existing tracks - to be deleted from the world model.
	 */
	private Set<Long> removedTracks;
	/**
	 * Removed existing vertices - to be deleted from the world model.
	 */
	private Set<Long> removedVertices;
	/**
	 * Identify new tracks with negative ID-s to distinguish them from the
	 * existing tracks.
	 */
	private long nextTrackId = -IIdentifiable.INCREMENTATION_START;
	/**
	 * Identify new vertices with negative ID-s to distinguish them from
	 * the existing tracks.
	 */
	private long nextVertexId = -IIdentifiable.INCREMENTATION_START;
	
	public NetworkUnitOfWork() {
		this.tracks = new LinkedHashMap<>();
		this.vertices = new LinkedHashMap<>();
		this.removedTracks = new LinkedHashSet<>();
		this.removedVertices = new LinkedHashSet<>();
	}
	
	/**
	 * Returns <strong>true</strong>, if the unit of work does not contain any changes
	 * to the data model.
	 * 
	 * @return True, if there are no changes stored.
	 */
	public boolean isEmpty() {
		return this.tracks.isEmpty() && this.vertices.isEmpty() && this.removedTracks.isEmpty() && this.removedVertices.isEmpty();
	}
	
	/**
	 * @return Number of imported tracks. 
	 */
	public int getTrackNum() {
		return this.tracks.size();
	}
	
	/**
	 * @return Number of imported vertices. 
	 */
	public int getVertexNum() {
		return this.vertices.size();
	}
	
	/**
	 * @return Number of removed tracks. 
	 */
	public int getRemovedTrackNum() {
		return this.removedTracks.size();
	}
	
	/**
	 * @return Number of removed vertices. 
	 */
	public int getRemovedVertexNum() {
		return this.removedVertices.size();
	}
	
	/**
	 * Adds a track, and if this is a new track, it assigns a temporary ID to it.
	 * 
	 * @param track
	 * @return Fluent interface.
	 */
	public NetworkUnitOfWork addTrack(TrackRecord track) {
		if(track.getId() == IIdentifiable.NEUTRAL_ID) {
			track.setId(this.nextTrackId--);
		}
		this.tracks.put(Long.valueOf(track.getId()), track);
		return this;
	}
	
	/**
	 * Adds a vertex, and if this is a new vertex, it assigns a temporary ID to it.
	 * 
	 * @param vertex
	 * @return Fluent interface.
	 */
	public NetworkUnitOfWork addVertex(VertexRecord vertex) {
		if(vertex.getId() == IIdentifiable.NEUTRAL_ID) {
			vertex.setId(this.nextVertexId--);
		}
		this.vertices.put(Long.valueOf(vertex.getId()), vertex);
		return this;
	}
	
	/**
	 * Returns the vertex record with the specified ID or null.
	 * 
	 * @param id Vertex ID to find.
	 * @return Vertex record with this ID or null.
	 */
	public VertexRecord findVertex(long id) {
		return this.vertices.get(id);
	}
	
	/**
	 * Returns the track record with the specified ID or null.
	 * 
	 * @param id Track ID to find.
	 * @return Track record with this ID or null.
	 */
	public TrackRecord findTrack(long id) {
		return this.tracks.get(id);
	}
	
	/**
	 * Imports the vertex from the domain model. This method may be called only in the
	 * model thread. If the record with the given ID is alread in the unit of work, no
	 * import happens - the method simply returns the existing record then.
	 * 
	 * @param vertex Source vertex.
	 * @return Importex or existing track record.
	 */
	public VertexRecord importVertex(World world, Vertex vertex) {
		VertexRecord record = this.vertices.get(Long.valueOf(vertex.getId()));
		if(null != record) {
			return record;
		}
		record = new VertexRecord(vertex);
		this.vertices.put(Long.valueOf(vertex.getId()), record);
		this.commonVertexImport(world, vertex, record);
		return record;
	}
	
	/**
	 * Imports the vertex from the domain model. This method may be called only in the
	 * model thread. If the record with the given ID is alread in the unit of work, no
	 * import happens - the method simply returns the existing record then.
	 * 
	 * @param world
	 * @param vertexId
	 * @return Importex or existing track record.
	 */
	public VertexRecord importVertex(World world, long vertexId) {
		VertexRecord record = this.vertices.get(Long.valueOf(vertexId));
		if(null != record) {
			return record;
		}
		Vertex vertex = world.findVertex(vertexId);
		record = new VertexRecord(vertex);
		this.vertices.put(Long.valueOf(vertex.getId()), record);
		this.commonVertexImport(world, vertex, record);
		
		return record;
	}
	
	/**
	 * Checks if we do not need to import some tracks, as well.
	 * 
	 * @param world
	 * @param vertex
	 * @param record 
	 */
	private void commonVertexImport(World world, Vertex vertex, VertexRecord record) {
		if(null != vertex.getFirstTrack()) {
			Vertex another = vertex.getFirstTrack().getOppositeVertex(vertex);
			VertexRecord anotherRecord = this.vertices.get(Long.valueOf(another.getId()));
			if(null != anotherRecord) {
				TrackRecord tr = this.importTrack(world, vertex.getFirstTrack().getId());
				record.replaceReferenceWithRecord(tr);
				anotherRecord.replaceReferenceWithRecord(tr);
			}
		}
		if(null != vertex.getSecondTrack()) {
			Vertex another = vertex.getSecondTrack().getOppositeVertex(vertex);
			VertexRecord anotherRecord = this.vertices.get(Long.valueOf(another.getId()));
			if(null != anotherRecord) {
				TrackRecord tr = this.importTrack(world, vertex.getSecondTrack().getId());
				record.replaceReferenceWithRecord(tr);
				anotherRecord.replaceReferenceWithRecord(tr);
			}
		}
	}

	/**
	 * Imports the track from the domain model. The method may be called only
	 * in the model thread. If the record with the given ID is alread in the unit of work, no
	 * import happens - the method simply returns the existing record then.
	 * 
	 * @param world
	 * @param trackId
	 * @return Imported or existing track record.
	 */
	public TrackRecord importTrack(World world, long trackId) {
		TrackRecord record = this.tracks.get(Long.valueOf(trackId));
		if(null != record) {
			return record;
		}
		Track track = world.findTrack(trackId);
		VertexRecord v1, v2;
		record = new TrackRecord(track);
		this.tracks.put(trackId, record);
		record.setVertices(
			v1 = this.importVertex(world, track.getFirstVertex()),
			v2 = this.importVertex(world, track.getSecondVertex())
		);
		
		// Both of the vertices might have been imported earlier. We must update their references to the
		// actual links.
		v1.replaceReferenceWithRecord(record);
		v2.replaceReferenceWithRecord(record);

		return record;
	}
	
	/**
	 * Imports the track from the domain model. The method may be called only
	 * in the model thread. If the record with the given ID is alread in the unit of work, no
	 * import happens - the method simply returns the existing record then.
	 * 
	 * @param world
	 * @param track The track to import
	 * @return Imported or existing track record.
	 */
	public TrackRecord importTrack(World world, Track track) {
		TrackRecord record = this.tracks.get(Long.valueOf(track.getId()));
		if(null != record) {
			return record;
		}
		VertexRecord v1, v2;
		record = new TrackRecord(track);
		this.tracks.put(track.getId(), record);
		record.setVertices(
			v1 = this.importVertex(world, track.getFirstVertex()),
			v2 = this.importVertex(world, track.getSecondVertex())
		);
		
		// Both of the vertices might have been imported earlier. We must update their references to the
		// actual links.
		v1.replaceReferenceWithRecord(record);
		v2.replaceReferenceWithRecord(record);

		return record;
	}

	/**
	 * We can remove a previously added track from the unit of work. The method
	 * performs the detaching from the neighbouring vertices as well. If the track
	 * is not a brand-new track, it is added to the set of tracks to remove, when
	 * this unit of work will be persisted.
	 * 
	 * @param track The track to remove.
	 */
	public void removeTrack(TrackRecord track) {
		Preconditions.checkNotNull(track, "The specified track to remove is NULL.");
		VertexRecord firstVertex = track.getFirstVertex();
		VertexRecord secondVertex = track.getSecondVertex();
		firstVertex.removeTrack(track);
		secondVertex.removeTrack(track);
		if(firstVertex.hasNoTracks()) {
			this.vertices.remove(firstVertex.getId());
			this.addRemovedVertexId(firstVertex.getId());
		}
		if(secondVertex.hasNoTracks()) {
			this.vertices.remove(secondVertex.getId());
			this.addRemovedVertexId(secondVertex.getId());
		}
		this.tracks.remove(track.getId());
		this.addRemovedTrackId(track.getId());
	}
	
	/**
	 * We can remove a previously added vertex from the unit of work. The method also
	 * removes all the tracks connected to this vertex. If the vertex is not a brand-new
	 * vertex, it is added to the set of vertices to remove, when this unit of work will
	 * be persisted.
	 * 
	 * @param vertex 
	 */
	public void removeVertex(VertexRecord vertex) {
		Preconditions.checkNotNull(vertex, "The specified vertex to remove is NULL.");
		Preconditions.checkArgument(!vertex.hasUnimportedTracks(), "The vertex to remove must not have unimported tracks!");
		if(vertex.getFirstTrack() != null) {
			this.removeTrack(vertex.getFirstTrack());
		}
		if(vertex.getSecondTrack() != null) {
			this.removeTrack(vertex.getSecondTrack());
		}
		Long theId = Long.valueOf(vertex.getId());
		this.vertices.remove(theId);
		this.addRemovedVertexId(theId);
	}
	
	/**
	 * We can insert the ID of the track to remove by force. This is needed for {@link NetworkLayoutChangeCmd}
	 * to create a memento. If the ID is temporary, nothing happens.
	 * 
	 * @param id 
	 */
	public void addRemovedTrackId(long id) {
		if(id > IIdentifiable.NEUTRAL_ID) {
			this.removedTracks.add(id);
		}
	}
	
	/**
	 * We can insert the ID of the vertex to remove by force. This is needed for {@link NetworkLayoutChangeCmd}
	 * to create a memento. If the ID is temporary, nothing happens.
	 * 
	 * @param id 
	 */
	public void addRemovedVertexId(long id) {
		if(id > IIdentifiable.NEUTRAL_ID) {
			this.removedVertices.add(id);
		}
	}
	
	/**
	 * Connects two vertices that have only one track connected, into a single vertex. Geometric
	 * adjustment of shapes must be performed separately.
	 * 
	 * @param mainVertex
	 * @param freeVertex
	 * @return The record representing the new vertex.
	 */
	public VertexRecord connectVertices(VertexRecord mainVertex, VertexRecord freeVertex) {
		Preconditions.checkArgument(mainVertex.hasOneTrack());
		Preconditions.checkArgument(freeVertex.hasOneTrack());
		
		TrackRecord tr = freeVertex.getTrack();
		tr.replaceVertex(freeVertex, mainVertex);
		mainVertex.addTrack(tr);
		
		freeVertex.removeTrack(tr);
		this.addRemovedVertexId(freeVertex.getId());
		// We must leave the removed vertex in the vertex buffer, because it must be synchronized
		// to inform the model that it does not have any tracks connected. Without it, the synchronization
		// process would also remove the track previously connected to it.
		if(mainVertex.getId() == IIdentifiable.NEUTRAL_ID) {
			this.addVertex(mainVertex);
		}
		return mainVertex;
	}
	
	public Iterator<TrackRecord> overTracks() {
		return this.tracks.values().iterator();
	}
	
	public Iterator<VertexRecord> overVertices() {
		return this.vertices.values().iterator();
	}
	
	/**
	 * Returns the set of all existing, but removed track ID-s.
	 * 
	 * @return 
	 */
	public Set<Long> getRemovedTracks() {
		return ImmutableSet.copyOf(this.removedTracks);
	}
	
	/**
	 * Returns the set of all existing, but removed vertex ID-s.
	 * 
	 * @return 
	 */
	public Set<Long> getRemovedVertices() {
		return ImmutableSet.copyOf(this.removedVertices);
	}
	
	/**
	 * Exports the editable part of the scene to the scene manager.
	 * 
	 * @param sm 
	 */
	public void exportScene(SceneManager sm) {
		EditableTrackSnapshot snap = new EditableTrackSnapshot(tracks.size());
		
		int i = 0;
		for(TrackRecord rec: this.tracks.values()) {
			Preconditions.checkState(rec.getId() != IIdentifiable.NEUTRAL_ID, "Track record has a neutral ID.");
			switch(rec.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					snap.setTrackPainter(i++, new StraightTrackPainter(rec.getId(), rec.getMetadata()));
					break;
				case NetworkConst.TRACK_CURVED:
					snap.setTrackPainter(i++, new CurvedTrackPainter(rec.getId(), rec.getMetadata()));
					break;
				case NetworkConst.TRACK_FREE:
					snap.setTrackPainter(i++, new FreeTrackPainter(rec.getId(), rec.getMetadata()));
					break;
			}
		}
		double points[] = new double[this.vertices.size() * 2];
		long ids[] = new long[this.vertices.size()];
		i = 0;
		int j = 0;
		for(VertexRecord rec: this.vertices.values()) {
			points[i++] = rec.x();
			points[i++] = rec.y();
			ids[j++] = rec.getId();
		}
		snap.setVertexArray(points, ids);
		sm.updateResource(EditableTrackSnapshot.class, snap);
	}
}
