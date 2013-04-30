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

package org.invenzzia.opentrans.visitons.editing.network;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;

/**
 * The command carries the {@link NetworkUnitOfWork} to the model thread
 * and then applies all the changes to the original network model.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkLayoutChangeCmd implements ICommand {
	private final NetworkUnitOfWork uw;
	
	private final BiMap<Long, Long> trackMapping;
	
	private final BiMap<Long, Long> vertexMapping;
	
	/**
	 * Creates a new command that modifies the network track layout.
	 * 
	 * @param uw Unit of work that describes the changes to apply.
	 */
	public NetworkLayoutChangeCmd(NetworkUnitOfWork uw) {
		this.uw = Preconditions.checkNotNull(uw);
		this.trackMapping = HashBiMap.create();
		this.vertexMapping = HashBiMap.create();
	}

	@Override
	public void execute(Project project) throws Exception {
		World dieWelt = project.getWorld(); // Deutschland ist ein schones Land :)

		Iterator<VertexRecord> vri = this.uw.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			this.importVertex(vr, project);
		}
		
		Iterator<TrackRecord> tri = this.uw.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			this.importTrack(tr, project);
		}
		vri = this.uw.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			this.importVertexConnections(vr, dieWelt);
		}
		
		tri = this.uw.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			this.importTrackConnections(tr, dieWelt);
		}
	}

	@Override
	public void undo(Project project) {
	}

	@Override
	public void redo(Project project) {
	}

	/**
	 * Imports the vertex to the world.
	 * 
	 * @param vr 
	 * @param project
	 */
	private void importVertex(VertexRecord vr, Project project) {
		if(vr.getId() < IIdentifiable.NEUTRAL_ID) {
			// New vertex
			long tempId = vr.getId();
			Vertex vertex = new Vertex();
			vertex.importFrom(vr, project.getWorld());
			project.getWorld().addVertex(vertex);
			this.vertexMapping.put(Long.valueOf(tempId), Long.valueOf(vertex.getId()));
		} else {
			// Existing vertex
			Vertex vertex = project.getWorld().findVertex(vr.getId());
			vertex.importFrom(vr, project.getWorld());
		}
	}

	/**
	 * Imports the track to the world.
	 * 
	 * @param tr
	 * @param project 
	 */
	private void importTrack(TrackRecord tr, Project project) {
		if(tr.getId() < IIdentifiable.NEUTRAL_ID) {
			long tempId = tr.getId();
			Track track = new Track();
			track.importFrom(tr, project.getWorld(), this.vertexMapping);
			project.getWorld().addTrack(track);
			this.trackMapping.put(Long.valueOf(tempId), Long.valueOf(track.getId()));
		} else {
			
		}
	}

	private void importVertexConnections(VertexRecord vr, World world) {
		Vertex vertex;
		if(vr.getId() < IIdentifiable.NEUTRAL_ID) {
			vertex = world.findVertex(this.vertexMapping.get(vr.getId()));
		} else {
			vertex = world.findVertex(vr.getId());
		}
		vertex.importConnections(vr, world, this.trackMapping);
	}

	private void importTrackConnections(TrackRecord tr, World world) {
		Track track;
		if(tr.getId() < IIdentifiable.NEUTRAL_ID) {
			track = world.findTrack(this.trackMapping.get(tr.getId()));
		} else {
			track = world.findTrack(tr.getId());
		}
		track.importConnections(tr, world, this.vertexMapping);
	}
}
