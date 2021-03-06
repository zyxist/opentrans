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

/**
 * Allows receiving notifications about camera model updates.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public interface ICameraModelListener {
	/**
	 * The camera viewport has been updated. The listener should receive the new
	 * parameters and change the rendering.
	 * 
	 * @param model 
	 */
	public void cameraUpdated(CameraModel model);
}
