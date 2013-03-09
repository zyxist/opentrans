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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JCheckBox;


/**
 * Provides a support for checkboxes in the forms.
 * 
 * @author zyxist
 */
public class CheckboxHandler extends AbstractFormScannerComponentHandler {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);

	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		JCheckBox checkbox = this.extract(JCheckBox.class, field, viewInstance);
		checkbox.addActionListener(new CheckboxActionListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		JCheckBox checkbox = this.extract(JCheckBox.class, field, viewInstance);
		checkbox.setSelected(false);
	}

	@Override
	public <T> T getValue(Field field, Object viewInstance, Class<T> expectedType) throws Exception {
		if(expectedType != Boolean.class) {
			throw new IllegalArgumentException("Checkbox fields supports only boolean values.");
		}
		JCheckBox checkbox = this.extract(JCheckBox.class, field, viewInstance);
		return (T) Boolean.valueOf(checkbox.isSelected());
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		if(!(value instanceof Boolean)) {
			throw new IllegalArgumentException("The value inserted into the checkbox must be a boolean.");
		}
		JCheckBox checkbox = this.extract(JCheckBox.class, field, viewInstance);
		checkbox.setSelected(((Boolean) value).booleanValue());
	}

	@Override
	public void setValid(Field field, Object viewInstance, boolean valid) throws Exception {
		JCheckBox checkbox = this.extract(JCheckBox.class, field, viewInstance);
		if(!valid) {
			checkbox.setBackground(ERROR_INDICATION_COLOR);
		} else {
			checkbox.setBackground(Color.WHITE);
		}
	}
}
class CheckboxActionListener implements ActionListener {
	private final Method validatorMethod;
	private final Object controllerInstance;
	
	CheckboxActionListener(Method validatorMethod, Object controllerInstance) {
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