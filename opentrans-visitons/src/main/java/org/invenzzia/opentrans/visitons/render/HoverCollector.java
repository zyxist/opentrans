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
	private byte type;
	private long id;
	
	public void resetHoveredItem() {
		this.type = 0;
		this.id = IIdentifiable.NEUTRAL_ID;
	}
	
	public void registerHoveredItem(byte type, long id) {
		this.type = type;
		this.id = id;
	}
	
	public void emitSnapshot(SceneManager sceneManager) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			sceneManager.updateResource(HoveredItemSnapshot.class, new HoveredItemSnapshot(this.type, this.id));
		} else {
			sceneManager.updateResource(HoveredItemSnapshot.class, null);
		}
	}

}
