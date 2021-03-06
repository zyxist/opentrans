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
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.INumberable;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.geometry.Point;
import org.invenzzia.opentrans.visitons.network.objects.TrackObject;

class StopBase implements IIdentifiable {
	/**
	 * Unique internal stop ID.
	 */
	protected long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Unique stop name.
	 */
	private String name;

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the previously set ID.");
		}
		this.id = id;
	}
	
	/**
	 * Returns the name of the stop.
	 * 
	 * @return Stop name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the new stop name.
	 * 
	 * @param name Stop name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

public final class Stop extends StopBase implements IMemento<Project> {
	/**
	 * Previous stop name, for the purpose of updating the index.
	 */
	private String previousName;
	/**
	 * Each stop should have at least one platform, where the vehicles could stop.
	 */
	private Map<Integer, Platform> platforms;
	/**
	 * Enumeration for platforms.
	 */
	private int nextPlatformId = INumberable.INCREMENTATION_START;
	
	public Stop() {
		this.platforms = new LinkedHashMap<>();
	}
	
	@Override
	public void setName(String name) {
		this.previousName = name;
		super.setName(name);
	}
	
	/**
	 * Returns the previous stop name.
	 * 
	 * @return Previous stop name.
	 */
	public String getPreviousName() {
		return this.previousName;
	}
	
	/**
	 * Internal API method used by {@link Platform} constructor to bind the platform
	 * to the stop.
	 * 
	 * @param platform 
	 */
	void bindPlatform(Platform platform) {
		platform.setNumber(this.nextPlatformId++);
		this.platforms.put(platform.getNumber(), platform);
	}
	
	/**
	 * Internal API for restoring the previously deleted platform.
	 * 
	 * @param platform The platform to restore.
	 */
	void restorePlatform(Platform platform) {
		this.platforms.put(platform.getNumber(), platform);
	}
	
	/**
	 * Returns <strong>true</strong>, if there is any platform assigned to this stop.
	 * 
	 * @return 
	 */
	public boolean hasPlatforms() {
		return !this.platforms.isEmpty();
	}
	
	/**
	 * Returns the immutable collection of all platforms in this stop.
	 * 
	 * @return Immutable collection of platforms.
	 */
	public Collection<Platform> getPlatforms() {
		return this.platforms.values();
	}
	
	/**
	 * Returns the platform with the specified number, or throws an exception, if such
	 * a platform does not exist.
	 * 
	 * @throws IllegalArgumentException If the platform with the given number does not exist.
	 * @param number Platform number
	 * @return Platform
	 */
	public Platform getPlatform(int number) {
		Platform platform = this.platforms.get(number);
		if(null == platform) {
			throw new IllegalArgumentException("Unknown platform: #"+number);
		}
		return platform;
	}
	
	/**
	 * Returns <strong>true</strong>, if the platform with the given number exists.
	 * 
	 * @param number Platform number.
	 * @return True, if the platform exists.
	 */
	public boolean hasPlatform(int number) {
		return this.platforms.containsKey(number);
	}
	
	/**
	 * Removes the specified platform.
	 * 
	 * @param platform 
	 */
	public void removePlatform(Platform platform) {
		Preconditions.checkNotNull(platform, "The platform is NULL.");
		Platform isThis = this.platforms.get(platform.getNumber());
		Preconditions.checkState(isThis.getNumber() == platform.getNumber(), "Invalid contract for Platform!");
		this.platforms.remove(platform.getNumber());
	}

	@Override
	public Object getMemento(Project domainModel) {
		StopRecord memento = new StopRecord();
		memento.importData(this, domainModel);
		return memento;
	}

	@Override
	public void restoreMemento(Object memento, Project domainModel) {
		if(!(memento instanceof StopRecord)) {
			throw new IllegalArgumentException("Invalid memento for Stop class: "+memento.getClass().getCanonicalName());
		}
		StopRecord record = (StopRecord) memento;
		record.exportData(this, domainModel);
		this.id = record.getId();
	}

	/**
	 * Calculates the approximate position of the stop, using the locations of all the platforms.
	 * 
	 * @return Approximate position of stop.
	 */
	public Point findApproximatePosition() {
		if(this.hasPlatforms()) {
			double sumX = 0.0;
			double sumY = 0.0;
			for(Platform platform: this.platforms.values()) {
				TrackObject to = platform.getTrackObject();
				Point pt = to.getTrack().getPointCharacteristics(to.getPosition());
				sumX += pt.x();
				sumY += pt.y();
			}
			int size = this.platforms.size();
			return new Point(sumX / size, sumY / size);
		} else {
			throw new IllegalStateException("This stop has no platforms to perform such calculations!");
		}
	}
	
	/**
	 * Temporary view of the 'stop', which allows direct data modifications of stops.
	 */
	public final static class StopRecord extends StopBase implements IRecord<Stop, Project> {
		private List<PlatformRecord> platforms = new LinkedList<>();
		
		@Override
		public void exportData(Stop original, Project domainModel) {
			original.setName(this.getName());
			LinkedHashMap<Integer, Platform> newPlatformMap = new LinkedHashMap<>();
			for(PlatformRecord record: this.platforms) {
				if(record.getNumber() == IIdentifiable.NEUTRAL_ID) {
					throw new IllegalStateException("The platform records within the Stop must have their local ID assigned!");
				}
				Platform existingPlatform = original.platforms.get(record.getNumber());
				if(null == existingPlatform) {
					throw new IllegalStateException("The platform #"+record.getNumber()+" disappeared somehow from stop #"+original.getId());
				}
				record.exportData(existingPlatform);
				newPlatformMap.put(Integer.valueOf(existingPlatform.getNumber()), existingPlatform);
			}
			original.platforms = newPlatformMap;
		}

		@Override
		public void importData(Stop original, Project domainModel) {
			this.setId(original.getId());
			this.setName(original.getName());
			
			this.platforms = new LinkedList<>();
			for(Platform platform: original.platforms.values()) {
				PlatformRecord record = new PlatformRecord(this);
				record.importData(platform);
				this.platforms.add(record);
			}
		}
		
		/**
		 * Returns <strong>true</strong>, if there is any platform assigned to this stop.
		 * 
		 * @return 
		 */
		public boolean hasPlatforms() {
			return !this.platforms.isEmpty();
		}

		/**
		 * Returns the immutable collection of all platforms in this stop.
		 * 
		 * @return Immutable collection of platforms.
		 */
		public Collection<PlatformRecord> getPlatforms() {
			return ImmutableList.copyOf(this.platforms);
		}
		
		/**
		 * Returns the platform with the specified number, or throws an exception, if such
		 * a platform does not exist.
		 * 
		 * @throws IllegalArgumentException If the platform with the given number does not exist.
		 * @param number Platform number
		 * @return Platform
		 */
		public PlatformRecord getPlatform(int number) {
			for(PlatformRecord record: this.platforms) {
				if(record.getNumber() == number) {
					return record;
				}
			}
			throw new IllegalArgumentException("Unknown platform: #"+number);
		}

		/**
		 * Returns <strong>true</strong>, if the platform with the given number exists.
		 * 
		 * @param number Platform number.
		 * @return True, if the platform exists.
		 */
		public boolean hasPlatform(int number) {
			for(PlatformRecord record: this.platforms) {
				if(record.getNumber() == number) {
					return true;
				}
			}
			return false;
		}
	}
}