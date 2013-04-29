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
 * Scene manager listeners allow performing certain additional operations
 * on the given objects, when they are being modified.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ISceneManagerListener {
	/**
	 * Returns the hints, where to register this listener (for autoregistration).
	 * 
	 * @return Keys, where we should be registered.
	 */
	public Object[] getListenKeyHints();
	/**
	 * Called, when the given key is being updated. The method is executed within
	 * the scene manager lock, so it should finish fast.
	 * 
	 * @param ops Allowed operations on the scene manager.
	 * @param key Key being updated.
	 */
	public void notifyObjectChanged(ISceneManagerOperations ops, Object key);
}
