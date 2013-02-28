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

/**
 * Action scanners can be used by the controller to simplify the bindings
 * between the buttons, etc. and controller actions thanks to {@link Action}
 * annotation. The scanner analyzes the fields of the view panel and each
 * supported field type annotated with this annotation is tied to a proper
 * controller action annotated with the same stuff.
 * 
 * <p>The implementation is not bound to any scope - each controller gets
 * a fresh copy.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IActionScanner {
	/**
	 * This method shall be used in the controller constructor to discover
	 * the action methods.
	 * 
	 * @param controllerClass The class to scan.
	 * @param controllerInstance Controller instance is required for proper calling the action methods.
	 */
	public <T> void discoverActions(Class<T> controllerClass, T controllerInstance);
	/**
	 * Discovers the action-annotated swing components and ties them to the controller methods.
	 * 
	 * @param viewClass View class
	 * @param viewInstance View instance
	 */
	public <T> void bindComponents(Class<T> viewClass, T viewInstance);
	/**
	 * Clears all the bindings.
	 * 
	 * @param viewClass View class
	 * @param viewInstance View instance
	 */
	public <T> void clear(Class<T> viewClass, T viewInstance);
}
