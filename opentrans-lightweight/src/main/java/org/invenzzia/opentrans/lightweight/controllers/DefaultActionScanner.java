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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.opentrans.lightweight.annotations.Action;

/**
 * Default action scanner implementation. The supported Swing
 * control types are described and delegated to a separate
 * interface.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DefaultActionScanner implements IActionScanner {
	/**
	 * List of all actions discovered in the descendant class.
	 */
	private Map<String, Method> actions = new LinkedHashMap<>();
	/**
	 * The managing controller instance.
	 */
	private Object controllerInstance;
	/**
	 * Handle various component types and know, how to tie them with action
	 * method.
	 */
	@Inject
	private Map<Class, IActionScannerComponentHandler> componentHandlers;

	@Override
	public <T> void discoverActions(Class<T> controllerClass, T controllerInstance) {
		for(Method method: controllerClass.getMethods()) {
			Action action = method.getAnnotation(Action.class);
			if(null != action) {
				this.actions.put(action.value(), method);
			}
		}
		this.controllerInstance = Preconditions.checkNotNull(controllerInstance);
	}

	@Override
	public <T> void bindComponents(Class<T> viewClass, T viewInstance) {
		for(Field field: viewClass.getDeclaredFields()) {
			Action action = field.getAnnotation(Action.class);
			
			if(null != action) {
				if(!this.componentHandlers.containsKey(field.getType())) {
					throw new IllegalStateException("Unsupported field type: '"+field.getType().getSimpleName()+"'.");
				}
				if(!this.actions.containsKey(action.value())) {
					throw new IllegalStateException("Unmapped action: '"+action.value()+"'.");
				}
				IActionScannerComponentHandler handler = this.componentHandlers.get(field.getType());
				try {
					handler.bindAction(field, this.actions.get(action.value()), viewInstance, this.controllerInstance);
				} catch(Exception exception) {
					throw new IllegalStateException("Cannot bind the action '"+action.value()+"'to the controller.", exception);
				}
			}
		}
	}

	@Override
	public <T> void clear(Class<T> viewClass, T viewInstance) {
		for(Field field: viewClass.getDeclaredFields()) {
			Action action = field.getAnnotation(Action.class);
			
			if(null != action) {
				IActionScannerComponentHandler handler = this.componentHandlers.get(field.getType());
				try {
					handler.detachAction(field, viewInstance);
				} catch(Exception exception) {
					throw new IllegalStateException("Cannot bind the action '"+action.value()+"'to the controller.", exception);
				}
			}
		}
	}
}
