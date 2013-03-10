/*
 * Copyright (C) 2013 zyxist
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
 * Supporting listener for different form scanner component handlers
 * that need action listeners that fire the controller actions.
 * 
 * @author zyxist
 */
public class FormActionListener implements ActionListener {
	private final Method validatorMethod;
	private final Object controllerInstance;
	
	public FormActionListener(Method validatorMethod, Object controllerInstance) {
		this.validatorMethod = validatorMethod;
		this.controllerInstance = controllerInstance;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			this.validatorMethod.invoke(controllerInstance);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
