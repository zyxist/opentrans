/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.network;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.data.Stop;

/**
 * Connection is the primary element of the abstract transportation network
 * graph. It differs from the infrastructure graph that it does not show
 * physical vehicle tracks, but rather abstract connections between different
 * stops defined by transportation lines. The vertices are {@link Stop}
 * objects from the data model.
 * 
 * This model is used for passenger trip planning and gaining statistics.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Connection {
	/**
	 * Two end-points of the connection.
	 */
	protected Stop stops[] = new Stop[2];
	
	public Connection(Stop stop1, Stop stop2) {
		Preconditions.checkNotNull(stop1, "None of the stops in Connection() can be null.");
		Preconditions.checkNotNull(stop2, "None of the stops in Connection() can be null.");
		this.stops[0] = stop1;
		this.stops[1] = stop2;
	}
	
	public void setStop(int idx, Stop stop) {
		Preconditions.checkArgument(idx == 0 || idx == 1, "The connection end-points can have only 0 or 1 as an index.");
		this.stops[idx] = stop;
	}
	
	public Stop getStop(int idx) {
		Preconditions.checkArgument(idx == 0 || idx == 1, "The connection end-points can have only 0 or 1 as an index.");
		return this.stops[idx];
	}
}
