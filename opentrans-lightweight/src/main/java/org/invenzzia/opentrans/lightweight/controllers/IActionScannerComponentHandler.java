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

package org.invenzzia.opentrans.lightweight.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Provides a support for the new component type to the action
 * scanners.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IActionScannerComponentHandler {
	/**
	 * Binds the specified action to the component in the given view field.
	 * 
	 * @param field The field in the view panel.
	 * @param method The action method in the controller.
	 * @param viewInstance View instance.
	 * @param controllerInstance Controller instace.
	 * @throws Exception 
	 */
	public void bindAction(Field field, Method method, Object viewInstance, Object controllerInstance) throws Exception;
	/**
	 * Detaches all the actions from the given field in the view.
	 * 
	 * @param field
	 * @param viewInstance
	 * @throws Exception 
	 */
	public void detachAction(Field field, Object viewInstance) throws Exception;
}
