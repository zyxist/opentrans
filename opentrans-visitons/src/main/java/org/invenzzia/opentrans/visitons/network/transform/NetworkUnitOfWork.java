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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
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
	private List<TrackRecord> tracks;
	/**
	 * List of vertices modified or added in this session.
	 */
	private List<VertexRecord> vertices;
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
		this.tracks = new LinkedList<>();
		this.vertices = new LinkedList<>();
	}
	
	public void addTrack(TrackRecord track) {
		if(track.getId() == IIdentifiable.NEUTRAL_ID) {
			track.setId(this.nextTrackId--);
		}
		this.tracks.add(track);
	}
	
	public void addVertex(VertexRecord vertex) {
		if(vertex.getId() == IIdentifiable.NEUTRAL_ID) {
			vertex.setId(this.nextVertexId--);
		}
		this.vertices.add(vertex);
	}
	
	public Iterator<TrackRecord> overTracks() {
		return this.tracks.iterator();
	}
	
	public Iterator<VertexRecord> overVertices() {
		return this.vertices.iterator();
	}

	public void importAllMissingNeighbors(VertexRecord vertex) {
		
	}
	
	/**
	 * Exports the editable part of the scene to the scene manager.
	 * 
	 * @param sm 
	 */
	public void exportScene(SceneManager sm) {
		EditableTrackSnapshot snap = new EditableTrackSnapshot(tracks.size());
		
		int i = 0;
		for(TrackRecord rec: this.tracks) {
			switch(rec.getType()) {
				case NetworkConst.TRACK_STRAIGHT:
					snap.setTrackPainter(i++, new StraightTrackPainter(rec.getMetadata()));
					break;
				case NetworkConst.TRACK_CURVED:
					snap.setTrackPainter(i++, new CurvedTrackPainter(rec.getMetadata()));
					break;
				case NetworkConst.TRACK_FREE:
					snap.setTrackPainter(i++, new FreeTrackPainter(rec.getMetadata()));
					break;
			}
		}
		double points[] = new double[this.vertices.size() * 2];
		i = 0;
		for(VertexRecord rec: this.vertices) {
			points[i++] = rec.x();
			points[i++] = rec.y();
		}
		snap.setVertexArray(points);
		sm.updateResource(EditableTrackSnapshot.class, snap);
	}
}
