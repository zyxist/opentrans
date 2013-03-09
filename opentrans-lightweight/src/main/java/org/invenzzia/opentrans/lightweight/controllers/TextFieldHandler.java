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

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Provides a support for text fields.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TextFieldHandler extends AbstractFormScannerComponentHandler {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);

	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		JTextField textField = this.extract(JTextField.class, field, viewInstance);
		textField.getDocument().addDocumentListener(new TextFieldDocumentListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		JTextField textField = this.extract(JTextField.class, field, viewInstance);
		textField.setText("");
	}

	@Override
	public <T> T getValue(Field field, Object viewInstance, Class<T> expectedType) throws Exception {
		JTextField textField = this.extract(JTextField.class, field, viewInstance);
		try {
			if(expectedType == String.class) {
				return (T) textField.getText();
			} else if(expectedType == Integer.class) {
				return (T) Integer.valueOf(Integer.parseInt(textField.getText()));
			} else if(expectedType == Double.class) {
				return (T) Double.valueOf(Double.parseDouble(textField.getText()));
			}
		} catch(NumberFormatException exception) {
			return null;
		}
		return null;
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		JTextField textField = this.extract(JTextField.class, field, viewInstance);
		if(value instanceof String) {
			textField.setText((String) value);
		} else if(value instanceof Integer) {
			textField.setText(Integer.toString(((Integer)value).intValue()));
		} else if(value instanceof Double) {
			textField.setText(Double.toString(((Double) value).doubleValue()));
		}
	}
	
	@Override
	public void setValid(Field field, Object viewInstance, boolean valid) throws Exception {
		JTextField textField = this.extract(JTextField.class, field, viewInstance);
		if(!valid) {
			textField.setBackground(ERROR_INDICATION_COLOR);
		} else {
			textField.setBackground(Color.WHITE);
		}
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
