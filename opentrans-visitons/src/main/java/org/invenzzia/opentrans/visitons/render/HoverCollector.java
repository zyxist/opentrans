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

package org.invenzzia.opentrans.visitons.render;

import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;

/**
 * The renderer passes only a single piece of information back to the
 * application: the currently hovered item. This small class allows
 * the rendering streams registering a hovered object. Note that
 * the calls are overwritten within a single session, so that only
 * a single, top-most object is selected.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HoverCollector {
	/**
	 * Type of the selected item.
	 */
	private int type;
	/**
	 * ID of the selected element (from the domain model).
	 */
	private long id;
	/**
	 * Secondary-level number of the selected item (from the domain model).
	 */
	private int number;
	/**
	 * Additional information about the hover, i.e. which part is actually selected.
	 */
	private double position;
	
	public void resetHoveredItem() {
		this.type = 0;
		this.id = IIdentifiable.NEUTRAL_ID;
	}
	
	public void registerHoveredItem(int type, long id) {
		this.type = type;
		this.id = id;
		this.number = 0;
	}
	
	public void registerHoveredItem(int type, long id, int number) {
		this.type = type;
		this.id = id;
		this.number = number;
	}
	
	public void registerPosition(double position) {
		this.position = position;
	}
	
	public void emitSnapshot(SceneManager sceneManager) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			sceneManager.updateResource(HoveredItemSnapshot.class, new HoveredItemSnapshot(this.type, this.id, this.number, this.position));
		} else {
			sceneManager.updateResource(HoveredItemSnapshot.class, null);
		}
	}

}
