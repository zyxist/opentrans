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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.invenzzia.opentrans.lightweight.ui.component.JReportingSlider;

/**
 * Provides a support for reporting sliders.
 * 
 * @author zyxist
 */
public class ReportingSliderHandler extends AbstractFormScannerComponentHandler {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);
	
	@Override
	public void bindEvent(Field field, Method validator, Object viewInstance, Object controllerInstance) throws Exception {
		JReportingSlider slider = this.extract(JReportingSlider.class, field, viewInstance);
		slider.getModel().addChangeListener(new SliderChangeListener(validator, controllerInstance));
	}

	@Override
	public void clear(Field field, Object viewInstance) throws Exception {
		JReportingSlider slider = this.extract(JReportingSlider.class, field, viewInstance);
		slider.getModel().setValue(slider.getModel().getMinValue());
	}

	@Override
	public <T> T getValue(Field field, Object viewInstance, Class<T> expectedType) throws Exception {
		if(expectedType != Integer.class) {
			throw new IllegalArgumentException("Reporting slider supports only integer values.");
		}
		JReportingSlider slider = this.extract(JReportingSlider.class, field, viewInstance);
		return (T) Integer.valueOf(slider.getModel().getValue());
	}

	@Override
	public void setValue(Field field, Object viewInstance, Object value) throws Exception {
		if(!(value instanceof Integer)) {
			throw new IllegalArgumentException("Reporting slider supports only integer values.");
		}
		JReportingSlider slider = this.extract(JReportingSlider.class, field, viewInstance);
		slider.getModel().setValue(((Integer) value).intValue());
	}

	@Override
	public void setValid(Field field, Object viewInstance, boolean valid) throws Exception {
		JReportingSlider slider = this.extract(JReportingSlider.class, field, viewInstance);
		if(valid) {
			slider.setBackground(Color.WHITE);
		} else {
			slider.setBackground(ERROR_INDICATION_COLOR);
		}
	}
}

class SliderChangeListener implements ChangeListener {
	private final Method validatorMethod;
	private final Object controllerInstance;
	
	public SliderChangeListener(Method validatorMethod, Object controllerInstance) {
		this.validatorMethod = validatorMethod;
		this.controllerInstance = controllerInstance;
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		try {
			this.validatorMethod.invoke(controllerInstance);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
