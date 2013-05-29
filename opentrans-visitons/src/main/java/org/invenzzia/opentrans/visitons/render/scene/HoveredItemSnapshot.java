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

/**
 * This is the only piece of information passed in the opposite direction:
 * from the renderer to the application. It specifies, which object
 * 
 * @author Tomasz Jędrzejewski
 */
public class HoveredItemSnapshot {
	public static final byte TYPE_TRACK = 1;
	public static final byte TYPE_VERTEX = 2;
	
	public static final int TYPE_TRACK_OBJECT_DELTA = 30;
	public static final int TYPE_PLATFORM = 32;	// delta + track object type number.
	
	private final int type;
	private final long id;
	private final int number;
	private final double position;
	
	public HoveredItemSnapshot(int type, long id, int number, double position) {
		this.type = type;
		this.id = id;
		this.number = number;
		this.position = position;
	}
	
	/**
	 * @return Type of the hovered element.
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * @return Domain model ID of the hovered element. 
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * @return Domain model secondary number of the hovered element (optional)
	 */
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * @return Information about relative position of the cursor above the hovered element. 
	 */
	public double getPosition() {
		return this.position;
	}
}
