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
import com.google.common.eventbus.EventBus;
import java.util.Iterator;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.history.ICommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.events.WorldSegmentUsageChangedEvent;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.Vertex;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.WorldRecord;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The command carries the {@link NetworkUnitOfWork} to the model thread
 * and then applies all the changes to the original network model. Restoring the
 * previous state is done from a single memento object, which does not actually
 * follow the concept of Memento design pattern. Tracks and vertices are too complicated
 * just to create the third representation of them, so we'll make a small trick: create
 * a reversed <tt>NetworkUnitOfWork</tt> It contains records of the same vertices and
 * tracks, as the original one, but with the original state, and the newly added items
 * are marked there as "removed". In this way, we can use exactly the same code to
 * revert the state, as to apply the original changes.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkLayoutChangeCmd implements ICommand, ICommandDetails {
	private static final Logger logger = LoggerFactory.getLogger(NetworkLayoutChangeCmd.class);
	
	private final NetworkUnitOfWork uw;
	/**
	 * Mapping of temporary ID-s to actual ID-s.
	 */
	private final BiMap<Long, Long> trackMapping;
	/**
	 * Mapping of temporary ID-s to actual ID-s.
	 */
	private final BiMap<Long, Long> vertexMapping;
	/**
	 * Old state of our tracks and vertices. 
	 */
	private NetworkUnitOfWork memento;
	/**
	 * This command has a customizable name, because it may represent lots of different
	 * operations.
	 */
	private final String commandName;
	
	/**
	 * Creates a new command that modifies the network track layout.
	 * 
	 * @param uw Unit of work that describes the changes to apply.
	 */
	public NetworkLayoutChangeCmd(NetworkUnitOfWork uw, String commandName) {
		this.uw = Preconditions.checkNotNull(uw);
		this.trackMapping = HashBiMap.create();
		this.vertexMapping = HashBiMap.create();
		this.commandName = commandName;
	}

	@Override
	public String getCommandName() {
		return this.commandName;
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {		
		if(logger.isDebugEnabled()) {
			logger.debug("Network layout change:");
			logger.debug("Updates: "+this.uw.getTrackNum()+" tracks; "+this.uw.getVertexNum()+" vertices");
			logger.debug("Deleted: "+this.uw.getRemovedTrackNum()+" tracks; "+this.uw.getRemovedVertexNum()+" vertices");
		}
		this.memento = this.buildMemento(project.getWorld(), this.uw);
		this.applyUnit(project, eventBus, this.uw);
		this.finishMemento(this.memento);
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		if(logger.isDebugEnabled()) {
			logger.debug("Network layout undo change:");
			logger.debug("Updates: "+this.memento.getTrackNum()+" tracks; "+this.memento.getVertexNum()+" vertices");
			logger.debug("Deleted: "+this.memento.getRemovedTrackNum()+" tracks; "+this.memento.getRemovedVertexNum()+" vertices");
		}
		this.applyUnit(project, eventBus, this.memento);
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		if(logger.isDebugEnabled()) {
			logger.debug("Network layout redo change:");
			logger.debug("Updates: "+this.uw.getTrackNum()+" tracks; "+this.uw.getVertexNum()+" vertices");
			logger.debug("Deleted: "+this.uw.getRemovedTrackNum()+" tracks; "+this.uw.getRemovedVertexNum()+" vertices");
		}
		this.applyUnit(project, eventBus, this.uw);
	}

	/**
	 * Constructs the memento.
	 * 
	 * @param sourceUnit 
	 */
	private NetworkUnitOfWork buildMemento(World world, NetworkUnitOfWork sourceUnit) {
		NetworkUnitOfWork memento = new NetworkUnitOfWork();
		Iterator<VertexRecord> vri = this.uw.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			if(vr.isPersisted()) {
				memento.importVertex(world, vr.getId());
			}
		}
		for(Long removedVertexId: this.uw.getRemovedVertices()) {
			memento.importVertex(world, removedVertexId);
		}
		Iterator<TrackRecord> tri = this.uw.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			if(tr.isPersisted()) {
				memento.importTrack(world, tr.getId());
			}
		}
		for(Long removedTrackId: this.uw.getRemovedTracks()) {
			memento.importVertex(world, removedTrackId);
		}
		return memento;
	}
	
	/**
	 * Finish the memento: take the list of inserted ID-s and set them in memento as "removed".
	 * 
	 * @param memento The memento to finalize.
	 */
	private void finishMemento(NetworkUnitOfWork memento) {
		for(Long id: this.vertexMapping.values()) {
			memento.addRemovedVertexId(id);
		}
		for(Long id: this.trackMapping.values()) {
			memento.addRemovedTrackId(id);
		}
	}
	
	/**
	 * Applies the changes brought by the network unit of work. This method is used both
	 * for executing the initial changes, undoing them and replaying again.
	 * 
	 * @param unit Original unit of work or memento.
	 */
	private void applyUnit(Project project, EventBus eventBus, NetworkUnitOfWork unit) {
		World dieWelt = project.getWorld(); // Deutschland ist ein schones Land :)
		Iterator<VertexRecord> vri = unit.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			this.importVertex(vr, project);
		}
		Iterator<TrackRecord> tri = unit.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			this.importTrack(tr, project);
		}
		vri = unit.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			this.importVertexConnections(vr, dieWelt);
		}
		
		tri = unit.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			this.importTrackConnections(tr, dieWelt);
		}
		
		for(Long removedTrackId: unit.getRemovedTracks()) {
			Track t = dieWelt.findTrack(removedTrackId);
			if(null != t) {
				dieWelt.removeTrack(t);
			}
		}
		for(Long removedVertexId: unit.getRemovedVertices()) {
			Vertex v = dieWelt.findVertex(removedVertexId);
			if(null != v) {
				dieWelt.removeVertex(v);
			}
		}
		eventBus.post(new WorldSegmentUsageChangedEvent(new WorldRecord(dieWelt)));
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
			Long tempId = vr.getId();
			Vertex vertex = new Vertex();
			vertex.importFrom(vr, project.getWorld());
			
			if(this.vertexMapping.containsKey(tempId)) {
				vertex.setId(this.vertexMapping.get(tempId));
				project.getWorld().addVertex(vertex);
			} else {
				project.getWorld().addVertex(vertex);
				this.vertexMapping.put(tempId, Long.valueOf(vertex.getId()));
			}
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
			Long tempId = tr.getId();
			Track track = new Track();
			track.importFrom(tr, project.getWorld(), this.vertexMapping);
			if(this.trackMapping.containsKey(tempId)) {
				track.setId(tempId);
				project.getWorld().addTrack(track);
			} else {
				project.getWorld().addTrack(track);
				this.trackMapping.put(Long.valueOf(tempId), Long.valueOf(track.getId()));
			}
		} else {
			Track track = project.getWorld().findTrack(tr.getId());
			track.importFrom(tr, project.getWorld(), this.vertexMapping);
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
