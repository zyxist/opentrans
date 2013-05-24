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
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Adds support for text areas in the form scanner.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TextAreaHandler extends AbstractFormScannerComponentHandler {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);

	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		JTextArea area = this.extract(JTextArea.class, field, viewInstance);
		area.getDocument().addDocumentListener(new TextAreaDocumentListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		JTextArea textArea = this.extract(JTextArea.class, field, viewInstance);
		textArea.setText("");
	}

	@Override
	public <T> T getValue(Field field, Object viewInstance, Class<T> expectedType) throws Exception {
		if(expectedType != String.class) {
			throw new IllegalArgumentException("Text area supports only string values.");
		}
		JTextArea textArea = this.extract(JTextArea.class, field, viewInstance);
		return (T) textArea.getText();
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		JTextArea textArea = this.extract(JTextArea.class, field, viewInstance);
		textArea.setText((String) value);
	}

	@Override
	public void setValid(Field field, Object viewInstance, boolean valid) throws Exception {
		JTextArea textArea = this.extract(JTextArea.class, field, viewInstance);
		if(!valid) {
			textArea.setBackground(ERROR_INDICATION_COLOR);
		} else {
			textArea.setBackground(Color.WHITE);
		}
	}
}
	
class TextAreaDocumentListener implements DocumentListener {
	private final Method validatorMethod;
	private final Object controllerInstance;
		
	TextAreaDocumentListener(Method validatorMethod, Object controllerInstance) {
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
