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
package org.invenzzia.opentrans.visitons.exception;

import org.invenzzia.opentrans.visitons.network.World;

/**
 * Problems reported by the world model.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldException extends Exception {
	private final World world;
	
	public WorldException(String message, World world) {
		super(message);
		this.world = world;
	}
	
	public final World getWorld() {
		return this.world;
	}
}
