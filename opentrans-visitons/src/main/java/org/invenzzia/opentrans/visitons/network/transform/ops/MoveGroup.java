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

package org.invenzzia.opentrans.visitons.network.transform.ops;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Set;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;

/**
 * Moves the whole group of tracks by the given delta.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MoveGroup implements IOperation {
	private ITransformAPI api;

	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = Preconditions.checkNotNull(api);
	}
	
	public boolean moveByDelta(Set<TrackRecord> tracks, double dx, double dy) {
		// Check if we can move everything to the new positions.
		for(TrackRecord tr: tracks) {
			if(!this.api.getWorld().isWithinWorld(tr.getFirstVertex().x() + dx, tr.getFirstVertex().y() + dy)) {
				return false;
			}
			if(!this.api.getWorld().isWithinWorld(tr.getSecondVertex().x() + dx, tr.getSecondVertex().y() + dy)) {
				return false;
			}
		}
		// Move all vertices.
		Set<Object> updatedElements = new LinkedHashSet<>();
		for(TrackRecord tr: tracks) {
			this.updatePosition(tr.getFirstVertex(), dx, dy, updatedElements);
			this.updatePosition(tr.getSecondVertex(), dx, dy, updatedElements);
		}
		updatedElements.clear();
		// Check the neighbouring tracks, if they need adjusting.
		Set<Object> adjustedTracks = new LinkedHashSet<>();
		for(TrackRecord tr: tracks) {
			if(tr.getFirstVertex().hasUnimportedTracks() || tr.getSecondVertex().hasUnimportedTracks()) {
				this.api.getRecordImporter().importAllMissingNeighbors(this.api.getUnitOfWork(), tr.getFirstVertex(), tr.getSecondVertex());
			}
			if(tr.getFirstVertex().hasAllTracks()) {
				this.adjustTrackType(tr.getFirstVertex().getOppositeTrack(tr), tracks, updatedElements);
			}
			if(tr.getSecondVertex().hasAllTracks()) {
				this.adjustTrackType(tr.getSecondVertex().getOppositeTrack(tr), tracks, updatedElements);
			}
		}		
		return true;
	}

	private void adjustTrackType(TrackRecord track, Set<TrackRecord> movedTracks, Set<Object> alreadyAdjustedTracks) {
		if(alreadyAdjustedTracks.contains(track)) {
			return;
		}
		if(!movedTracks.contains(track)) {
			track.setType(NetworkConst.TRACK_FREE);
		}
		switch(track.getType()) {
			case NetworkConst.TRACK_STRAIGHT:
				this.api.calculateStraightLine(track);
				break;
			case NetworkConst.TRACK_CURVED:
				this.api.calculateCurve(track);
				break;
			case NetworkConst.TRACK_FREE:
				this.api.calculateFreeCurve(track);
		}
		alreadyAdjustedTracks.add(track);
	}

	private void updatePosition(VertexRecord vertex, double dx, double dy, Set<Object> updatedVertices) {
		if(!updatedVertices.contains(vertex)) {
			vertex.setPosition(vertex.x() + dx, vertex.y() + dy);
			updatedVertices.add(vertex);
		}
	}
}
