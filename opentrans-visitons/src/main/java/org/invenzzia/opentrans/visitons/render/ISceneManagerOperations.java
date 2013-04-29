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

/**
 * Scene manager passes itself to the scene manager listeners, but we do
 * not want to expose the full interface there. This interface specifies
 * the operations on the scene manager to be performed by the scene
 * listeners.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ISceneManagerOperations {
	/**
	 * Retrieves an object from the scene manager. The method does not
	 * use locking and may be used only by {@link ISceneManagerListener}
	 * instances.
	 * 
	 * @param key Object key.
	 * @return Stored scene resource.
	 */
	public Object getSceneResource(Object key);
	/**
	 * Retrieves an object from the scene manager. The method does not
	 * use locking and may be used only by {@link ISceneManagerListener}
	 * instances.
	 * 
	 * @param key Object key.
	 * @param cast Autocasting
	 * @return Stored scene resource.
	 */
	public <T> T getSceneResource(Object key, Class<T> cast);
}
