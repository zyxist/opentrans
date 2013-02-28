/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.events;

/**
 * Informs that the size of the world has changed. The event contains
 * the new size of the world.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldSizeChangedEvent {
	private final int x;
	private final int y;
	
	public WorldSizeChangedEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getSizeX() {
		return this.x;
	}

	public int getSizeY() {
		return this.y;
	}
}
