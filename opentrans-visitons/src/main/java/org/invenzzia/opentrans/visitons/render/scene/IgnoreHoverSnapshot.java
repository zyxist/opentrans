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

package org.invenzzia.opentrans.visitons.render.scene;

import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;

/**
 * We can send this thing to force ignoring certain elements when looking for
 * hovering.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class IgnoreHoverSnapshot {
	private final long trackId;
	private final long vertexId;
	
	public IgnoreHoverSnapshot(long trackId, long vertexId) {
		this.trackId = trackId;
		this.vertexId = vertexId;
	}
	
	public IgnoreHoverSnapshot(TrackRecord tr, VertexRecord vr) {
		this.trackId = (null != tr ? tr.getId() : 0);
		this.vertexId = (null != vr ? vr.getId() : 0);
	}
	
	public long getTrackId() {
		return this.trackId;
	}
	
	public long getVertexId() {
		return this.vertexId;
	}
}
