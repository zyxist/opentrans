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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.AbstractButton;


/**
 * Allows automated action binding to buttons by the controllers.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ActionButtonHandler implements IActionScannerComponentHandler {
	@Override
	public void bindAction(Field field, Method method, Object viewInstance, Object controllerInstance) throws Exception {
		if(method.getParameterTypes().length != 0) {
			throw new IllegalStateException("The action method bound to JButton must not take arguments.");
		}
		if(field.getType().isAssignableFrom(AbstractButton.class)) {
			throw new IllegalArgumentException("The field type must be a subclass of AbstractButton, found: '"+field.getType().getCanonicalName()+"'");
		}
		field.setAccessible(true);
		AbstractButton button = (AbstractButton) field.get(viewInstance);
		button.addActionListener(new ButtonListener(controllerInstance, method));
	}

	@Override
	public void detachAction(Field field, Object viewInstance) throws Exception {
		field.setAccessible(true);
		AbstractButton button = (AbstractButton) field.get(viewInstance);
		for(ActionListener listener: button.getActionListeners()) {
			button.removeActionListener(listener);
		}
	}
}
/**
 * Actual tie.
 * 
 * @author Tomasz Jędrzejewski
 */
class ButtonListener implements ActionListener {
	private Object controllerInstance;
	private Method actionMethod;
	
	public ButtonListener(Object controllerInstance, Method actionMethod) {
		this.controllerInstance = controllerInstance;
		this.actionMethod = actionMethod;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			this.actionMethod.setAccessible(true);
			this.actionMethod.invoke(controllerInstance);
		} catch(SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new IllegalStateException("Cannot invoke the action method!", exception);
		}
	}
}
