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
			tr.moveMetadataPointsByDelta(dx, dy);
			this.updatePosition(tr.getFirstVertex(), tr, dx, dy, updatedElements, tracks);
			this.updatePosition(tr.getSecondVertex(), tr, dx, dy, updatedElements, tracks);
		}

		return true;
	}

	private void updatePosition(VertexRecord vertex, TrackRecord examinedTrack, double dx, double dy, Set<Object> updatedVertices, Set<TrackRecord> tracks) {
		if(!updatedVertices.contains(vertex)) {
			vertex.setPosition(vertex.x() + dx, vertex.y() + dy);
			updatedVertices.add(vertex);
			if(vertex.hasAllTracks()) {
				this.api.getRecordImporter().importAllMissingNeighbors(this.api.getUnitOfWork(), vertex);
				TrackRecord opposite = vertex.getOppositeTrack(examinedTrack);
				if(!tracks.contains(opposite)) {
					opposite.setType(NetworkConst.TRACK_FREE);
					this.api.calculateFreeCurve(opposite);
				}
			}
		}
	}
}
