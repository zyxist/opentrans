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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JComboBox;

/**
 * Provides support for combo boxes.
 * 
 * @author zyxist
 */
public class ComboBoxHandler extends AbstractFormScannerComponentHandler {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);

	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		JComboBox combo = this.extract(JComboBox.class, field, viewInstance);
		combo.addActionListener(new FormActionListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		JComboBox combo = this.extract(JComboBox.class, field, viewInstance);
		combo.setSelectedItem(null);
	}

	@Override
	public <T> T getValue(Field field, Object viewInstance, Class<T> expectedType) throws Exception {
		JComboBox combo = this.extract(JComboBox.class, field, viewInstance);
		Object value = combo.getSelectedItem();
		if(expectedType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}
		return null;
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		JComboBox combo = this.extract(JComboBox.class, field, viewInstance);
		combo.setSelectedItem(value);
	}

	@Override
	public void setValid(Field field, Object viewInstance, boolean valid) throws Exception {
		JComboBox combo = this.extract(JComboBox.class, field, viewInstance);
		if(valid) {
			combo.setBackground(Color.WHITE);
		} else {
			combo.setBackground(ERROR_INDICATION_COLOR);
		}
	}
	
}
