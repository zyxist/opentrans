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

package org.invenzzia.opentrans.visitons.data;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.INumberable;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.objects.INamedTrackObject;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;


class PlatformBase implements INumberable {
	/**
	 * Number of the platform in the local stop index. The value is semi-mutable:
	 * during the application run, we can't change it, but the application may choose
	 * to renumber the platforms during the saving.
	 */
	private int number;
	/**
	 * Name of the platform (for the better identification purposes by the user).
	 */
	private String name;

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

/**
 * Each stop has at least one platform, where the vehicles can stop. Platform is
 * directly connected with a track, and has a precise location.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Platform extends PlatformBase implements INamedTrackObject, IIdentifiable {
	/**
	 * Which stop owns this platform?
	 */
	private final Stop stop;
	/**
	 * Describes the precise location of the platform.
	 */
	private final TrackObject<Platform> trackObject;
	
	public Platform(final Stop stop, TrackObject<Platform> trackObject) {
		this.stop = Preconditions.checkNotNull(stop);
		this.stop.bindPlatform(this);
		this.trackObject = Preconditions.checkNotNull(trackObject);
		this.trackObject.setObject(this);
	}
	
	/**
	 * Used for restoring the previously deleted platform.
	 */
	public Platform(final Stop stop, final PlatformRecord record, TrackObject<Platform> trackObject) {
		this.stop = Preconditions.checkNotNull(stop);
		Preconditions.checkArgument(stop.getId() == record.getId());
		record.exportData(this);
		this.stop.restorePlatform(this);
		this.trackObject = Preconditions.checkNotNull(trackObject);
		this.trackObject.setObject(this);
	}
	
	public Stop getStop() {
		return this.stop;
	}

	@Override
	public TrackObject<Platform> getTrackObject() {
		return this.trackObject;
	}

	@Override
	public int getType() {
		return NetworkConst.TRACK_OBJECT_PLATFORM;
	}

	@Override
	public long getId() {
		return this.stop.getId();
	}

	@Override
	public void setId(long id) {
		throw new UnsupportedOperationException("This operation is not supported for platforms.");
	}
	
	@Override
	public String getTrackObjectName() {
		return this.getName();
	}
	
	/**
	 * For making temporary changes.
	 */
	public static class PlatformRecord extends PlatformBase implements IIdentifiable {
		private final StopRecord stop;
		
		public PlatformRecord(StopRecord stop) {
			this.stop = Preconditions.checkNotNull(stop);
		}

		public StopRecord getStop() {
			return this.stop;
		}

		public void importData(Platform original) {
			this.setNumber(original.getNumber());
			this.setName(original.getName());
		}

		public void exportData(Platform original) {
			if(original.getNumber() == INumberable.NEUTRAL_ID) {
				original.setNumber(this.getNumber());
			}
			original.setName(this.getName());
		}

		@Override
		public long getId() {
			return this.stop.getId();
		}

		@Override
		public void setId(long id) {
			throw new UnsupportedOperationException("This operation is not supported for platforms.");
		}
	}
}
