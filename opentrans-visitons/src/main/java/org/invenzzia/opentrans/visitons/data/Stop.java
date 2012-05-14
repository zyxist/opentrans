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
package org.invenzzia.opentrans.visitons.data;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Max;
import org.invenzzia.opentrans.visitons.exception.NoSuchConnectionException;
import org.invenzzia.opentrans.visitons.network.Connection;

/**
 * Represents a stop, where the passenger exchange takes place after
 * the vehicle arrival.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Stop {
	private String name;
	@Max(value = 100)
	private List<Platform> platforms;
	/**
	 * Connections with other stops defined by lines.
	 */
	private Map<Stop, Connection> connections = new LinkedHashMap<>();
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isConnectedWith(Stop otherStop) {
		return this.connections.containsKey(otherStop);
	}
	
	/**
	 * Returns the connection object to the given stop. An exception is
	 * thrown, if the connection does not exist.
	 * 
	 * @param stop
	 * @return
	 * @throws NoSuchConnectionException 
	 */
	public Connection getConnectionTo(Stop stop) throws NoSuchConnectionException {
		Preconditions.checkNotNull(stop, "Passing empty stop to Stop.getConnectionTo()");
		Connection conn = this.connections.get(stop);
		if(null == conn) {
			throw new NoSuchConnectionException(String.format("There is no connection to stop '%s'.", stop.getName()));
		}
		return conn;
	}
	
	/**
	 * Binds two stops with a newly created {@link Connection} object. If the
	 * connection already exists, the method does nothing.
	 * 
	 * @param stop1
	 * @param stop2 
	 */
	public static void bindStops(Stop stop1, Stop stop2) {
		Preconditions.checkNotNull(stop1, "None of the stops in Stop.bindStops() can be null.");
		Preconditions.checkNotNull(stop2, "None of the stops in Stop.bindStops() can be null.");
		
		if(stop1.isConnectedWith(stop2)) {
			return;
		}
		
		Connection connection = new Connection(stop1, stop2);
		stop1.connections.put(stop2, connection);
		stop2.connections.put(stop1, connection);
	}
	
	/**
	 * Removes the connection between two stops. Nothing happens, if such connection does
	 * not exist.
	 * 
	 * @param stop1
	 * @param stop2 
	 */
	public static void unbindStops(Stop stop1, Stop stop2) {
		Preconditions.checkNotNull(stop1, "None of the stops in Stop.unbindStops() can be null.");
		Preconditions.checkNotNull(stop2, "None of the stops in Stop.unbindStops() can be null.");
		if(!stop1.isConnectedWith(stop2)) {
			return;
		}
		stop1.connections.remove(stop2);
		stop2.connections.remove(stop1);
	}
}
