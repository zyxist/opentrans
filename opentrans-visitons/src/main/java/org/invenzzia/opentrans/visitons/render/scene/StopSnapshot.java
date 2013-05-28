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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.geometry.Point;

/**
 * Information about stops and their names.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopSnapshot {
	private List<StopNameInfo> stopNames;
	
	public StopSnapshot addStops(Collection<Stop> stops) {
		this.stopNames = new ArrayList<>(stops.size());
		for(Stop stop: stops) {
			this.addStop(stop);
		}
		return this;
	}
	
	public StopSnapshot addStop(Stop stop) {
		if(stop.hasPlatforms()) {
			if(null == this.stopNames) {
				this.stopNames = new LinkedList<>();
			}
			Point point = stop.findApproximatePosition();
			this.stopNames.add(new StopNameInfo(point.x(), point.y(), stop.getName().toUpperCase()));
		}
		return this;
	}
	
	public List<StopNameInfo> getStopNameInfo() {
		return this.stopNames;
	}

	public static class StopNameInfo {
		public final double x;
		public final double y;
		public final String label;
		
		StopNameInfo(double x, double y, String label) {
			this.x = x;
			this.y = y;
			this.label = label;
		}
	}
}
