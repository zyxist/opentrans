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
package org.invenzzia.opentrans.visitons.render;

import java.awt.Rectangle;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;

/**
 * Abstract class that provides default implementations of the setters
 * from the {@link IRenderingStream} interface. You must still write
 * the actual renderer.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class RenderingStreamAdapter implements IRenderingStream {
	/**
	 * Helps extracting the data from the scene manager snapshot by casting
	 * them to the destination object. The method assumes that the key is
	 * the class.
	 * 
	 * @param snapshot
	 * @param key
	 * @return Casted object or NULL.
	 */
	public <T> T extract(Map<Object, Object> snapshot, Class<T> key) {
		return (T) snapshot.get(key);
	}
	
	/**
	 * Gets the mouse position for the purpose of testing the hitting.
	 * 
	 * @param snapshot
	 * @return 
	 */
	public Rectangle getMousePosition(Map<Object, Object> snapshot) {
		MouseSnapshot ms = this.extract(snapshot, MouseSnapshot.class);
		if(null != ms) {
			return ms.hitRect();
		}
		return null;
	}
}
