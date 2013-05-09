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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
	}
	
	/**
	 * Returns <strong>true</strong>, if the unit of work does not contain any changes
	 * to the data model.
	 * 
	 * @return True, if there are no changes stored.
	 */
	public boolean isEmpty() {
		return this.tracks.isEmpty() && this.vertices.isEmpty();
	}
	
	public void addTrack(TrackRecord track) {
		if(track.getId() == IIdentifiable.NEUTRAL_ID) {
			track.setId(this.nextTrackId--);
		}
		this.tracks.put(Long.valueOf(track.getId()), track);
	}
	
	public void addVertex(VertexRecord vertex) {
		if(vertex.getId() == IIdentifiable.NEUTRAL_ID) {
			vertex.setId(this.nextVertexId--);
		}
		this.vertices.put(Long.valueOf(vertex.getId()), vertex);
	}
	
	/**
	 * Imports the vertex from the domain model. This method may be called only in the
	 * model thread. If the record with the given ID is alread in the unit of work, no
	 * import happens - the method simply returns the existing record then.
	 * 
	 * @param vertex Source vertex.
	 * @return Importex or existing track record.
	 */
	public VertexRecord importVertex(Vertex vertex) {
		VertexRecord record = this.vertices.get(Long.valueOf(vertex.getId()));
		if(null != record) {
			return record;
		}
		record = new VertexRecord(vertex);
		this.vertices.put(Long.valueOf(vertex.getId()), record);
		return record;
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
		record = new TrackRecord(
			track,
			v1 = this.importVertex(track.getFirstVertex()),
			v2 = this.importVertex(track.getSecondVertex())
		);
		this.tracks.put(trackId, record);
		
		// Both of the vertices might have been imported earlier. We must update their references to the
		// actual links.
		v1.replaceReferenceWithRecord(record);
		v2.replaceReferenceWithRecord(record);

		return record;
	}

	/**
	 * We can remove a previously added track from the unit of work. The method
	 * performs the detaching from the neighbouring vertices as well.
	 * 
	 * @param track The track to remove.
	 */
	public void removeTrack(TrackRecord track) {
		Preconditions.checkNotNull(track, "The specified track to remove is NULL.");
		track.getFirstVertex().removeTrack(track);
		track.getSecondVertex().removeTrack(track);
		if(track.getFirstVertex().hasNoTracks()) {
			this.vertices.remove(track.getFirstVertex().getId());
		}
		if(track.getSecondVertex().hasNoTracks()) {
			this.vertices.remove(track.getSecondVertex().getId());
		}
		this.tracks.remove(track.getId());
	}
	
	public Iterator<TrackRecord> overTracks() {
		return this.tracks.values().iterator();
	}
	
	public Iterator<VertexRecord> overVertices() {
		return this.vertices.values().iterator();
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
