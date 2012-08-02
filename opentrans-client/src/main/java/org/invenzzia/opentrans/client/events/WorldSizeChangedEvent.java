/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.events;

import org.invenzzia.opentrans.visitons.world.World;

/**
 * Notification about GUI actions that led to the change of the
 * world size.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldSizeChangedEvent extends ProjectEvent {
	private final World world;
	
	public WorldSizeChangedEvent(World world) {
		this.world = world;
	}
	
	public final World getWorld() {
		return this.world;
	}
}
