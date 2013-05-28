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

import java.util.LinkedList;
import java.util.List;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.INumberable;
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

/**
 * Contains information about all track objects to draw.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractTrackObjectSnapshot<T> {
	private List<RenderableTrackObject> records = new LinkedList<>();
	
	/**
	 * Adds a new track object to the buffer.
	 * 
	 * @param track The track that owns this object.
	 * @param object The track object to render.
	 */
	public abstract void addTrackObject(T track, TrackObject object);
	
	/**
	 * Local version, with no dependency on the actual track, so it can be reused
	 * in both implementations.
	 * 
	 * @param object Track object.
	 * @param point Characteristics of the point, where we should draw. We shall obtain this info from the track.
	 */
	protected void addTrackObjectInt(TrackObject object, Characteristics point) {
		this.records.add(new RenderableTrackObject(
			object.getObject().getType(),
			point.x(), point.y(), point.tangent(),
			object.getOrientation(),
			(object.getObject() instanceof IIdentifiable) ? ((IIdentifiable)object.getObject()).getId() : IIdentifiable.NEUTRAL_ID,
			(object.getObject() instanceof INumberable) ? ((INumberable)object.getObject()).getNumber(): INumberable.NEUTRAL_ID
		));
	}
	
	public List<RenderableTrackObject> getTrackObjects() {
		return this.records;
	}
	
	/**
	 * Everything that we need to draw a track object.
	 * 
	 * @author Tomasz Jędrzejewski
	 */
	public static class RenderableTrackObject {
		public final int type;
		public final double x;
		public final double y;
		public final double tangent;
		public final byte orientation;
		public final long id;
		public final int number;

		RenderableTrackObject(int type, double x, double y, double tangent, byte orientation, long id, int number) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.tangent = tangent;
			this.orientation = orientation;
			this.id = id;
			this.number = number;
		}
	}
}


