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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

/**
 * Interface for writing reusable popup menu actions that can be used
 * by the pop-up constructors.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IPopupAction {
	/**
	 * Here you can write your custom code.
	 * 
	 * @param x Where we have clicked? [world units]
	 * @param y Where we have clicked? [world units]
	 */
	public void execute(IEditModeAPI api, double x, double y);
}
