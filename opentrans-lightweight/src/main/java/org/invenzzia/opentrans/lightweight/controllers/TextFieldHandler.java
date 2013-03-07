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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TextFieldHandler implements IFormScannerComponentHandler {

	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		field.setAccessible(true);
		JTextField textField = (JTextField) field.get(viewInstance);
		textField.getDocument().addDocumentListener(new TextFieldDocumentListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		field.setAccessible(true);
		JTextField textField = (JTextField) field.get(viewInstance);
		textField.setText("");
	}

	@Override
	public Object getValue(Field field, Object viewInstance) throws Exception {
		field.setAccessible(true);
		JTextField textField = (JTextField) field.get(viewInstance);
		return textField.getText();
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		field.setAccessible(true);
		if(!(value instanceof String)) {
			throw new IllegalArgumentException("The value inserted into the field must be a string.");
		}
		JTextField textField = (JTextField) field.get(viewInstance);
		textField.setText((String) value);
	}
}

class TextFieldDocumentListener implements DocumentListener {
	private final Method validatorMethod;
	private final Object controllerInstance;
	
	TextFieldDocumentListener(Method validatorMethod, Object controllerInstance) {
		this.validatorMethod = validatorMethod;
		this.controllerInstance = controllerInstance;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			this.validatorMethod.setAccessible(true);
			this.validatorMethod.invoke(this.controllerInstance);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			this.validatorMethod.setAccessible(true);
			this.validatorMethod.invoke(this.controllerInstance);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}
}
