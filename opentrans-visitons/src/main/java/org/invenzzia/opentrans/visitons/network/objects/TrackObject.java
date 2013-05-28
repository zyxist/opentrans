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

package org.invenzzia.opentrans.visitons.network.objects;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.INumberable;


class TrackObjectBase {
	/**
	 * Position on the track: <tt>[0.0 , 1.0]</tt>
	 */
	private double position;
	/**
	 * Specifies the orientation of the track object (placement on the right side
	 * of the track, and the direction, if this object needs it).
	 */
	private byte orientation;
	
	public void setPosition(double position) {
		this.position = position;
	}
	
	public double getPosition() {
		return this.position;
	}
	
	public void setOrientation(byte orientation) {
		this.orientation = orientation;
	}
	
	public byte getOrientation() {
		return this.orientation;
	}
}

/**
 * Track objects are the references to some objects that are put at some position on the track.
 * We assume that references to these objects are stored in exactly two places: the track itself,
 * and the actual object, so they serve as a... 'references'. You make a persistent reference
 * at your own risk, because the API may replace the one instance with another one that represents
 * exactly the same object.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackObject<T extends ITrackObject> extends TrackObjectBase {
	/**
	 * The actual object.
	 */
	private T object;
	
	public void setObject(T object) {
		this.object = Preconditions.checkNotNull(object);
	}
	
	public T getObject() {
		return this.object;
	}

	public static class TrackObjectRecord extends TrackObjectBase {
		/**
		 * Type of the track object.
		 */
		private int type;
		/**
		 * ID of the track object.
		 */
		private long id;
		/**
		 * Optional field for extra identification. Used mostly by platforms.
		 */
		private int number;
		/**
		 * Optional track object name.
		 */
		private String name;
		
		public void setObject(int type, long id) {
			this.type = type;
			this.id = id;
		}
		
		public void setObject(int type, long id, int number) {
			this.type = type;
			this.id = id;
			this.number = number;
		}
		
		public int getType() {
			return this.type;
		}
		
		public long getId() {
			return this.id;
		}
		
		public int getNumber() {
			return this.number;
		}
		
		public String getName() {
			return this.name;
		}
		
		/**
		 * Imports the data from the original track object.
		 */
		public void importFrom(TrackObject original) {
			this.setOrientation(original.getOrientation());
			this.setPosition(original.getPosition());
			
			ITrackObject backedObject = original.getObject();
			if(backedObject instanceof INamedTrackObject) {
				this.name = ((INamedTrackObject)backedObject).getTrackObjectName();
			}
			
			this.setObject(
				backedObject.getType(),
				backedObject instanceof IIdentifiable ? ((IIdentifiable) backedObject).getId() : IIdentifiable.NEUTRAL_ID,
				backedObject instanceof INumberable ? ((INumberable) backedObject).getNumber() : INumberable.NEUTRAL_ID
			);
		}
	}
}
