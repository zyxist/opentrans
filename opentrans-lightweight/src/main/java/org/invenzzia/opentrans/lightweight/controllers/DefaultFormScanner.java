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
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.validator.IValidator;

/**
 * Default implementation of the form scanner, which binds the fields
 * and their validators together.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DefaultFormScanner implements IFormScanner {
	/**
	 * All the form field validators.
	 */
	private Map<String, Method> validators = new LinkedHashMap<>();
	/**
	 * All the form fields.
	 */
	private Map<String, Field> fields = new LinkedHashMap<>();
	/**
	 * Managing controller instance.
	 */
	private Object controllerInstance;
	/**
	 * Managed view instance.
	 */
	private Object viewInstance;
	/**
	 * Component handlers know, how to handle certain component types.
	 */
	@Inject
	private Map<Class, IFormScannerComponentHandler> fieldHandlers;

	@Override
	public <T> void discoverValidators(Class<T> controllerClass, T controllerInstance) {
		for(Method method: controllerClass.getMethods()) {
			FormField field = method.getAnnotation(FormField.class);
			if(null != field) {
				this.validators.put(field.name(), method);
			}
		}
		this.controllerInstance = Preconditions.checkNotNull(controllerInstance);
	}

	@Override
	public <T> void bindFields(Class<T> viewClass, T viewInstance) {
		this.viewInstance = Preconditions.checkNotNull(viewInstance, "The view instance must not be empty!");
		for(Field field: viewClass.getDeclaredFields()) {
			FormField annotation = field.getAnnotation(FormField.class);
			if(null != annotation) {
				if(!this.fieldHandlers.containsKey(field.getType())) {
					throw new IllegalStateException("Unsupported field type: '"+field.getType().getSimpleName()+"'.");
				}
				if(!this.validators.containsKey(annotation.name())) {
					throw new IllegalStateException("Unmapped validator: '"+annotation.name()+"'.");
				}
				IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
				try {
					handler.bindEvent(field, this.validators.get(annotation.name()), this.viewInstance, this.controllerInstance);
				} catch(Exception exception) {
					throw new IllegalStateException("Cannot bind the form field '"+annotation.name()+"'to the controller.", exception);
				}
				this.fields.put(annotation.name(), field);
			}
		}
	}

	@Override
	public void clear() {
		for(Field field: this.fields.values()) {
			IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
			try {
				handler.clear(field, this.viewInstance);
			} catch(Exception exception) {
				throw new IllegalStateException("Cannot clear the form field.", exception);
			}
		}
	}
	
	@Override
	public boolean validate(String fieldName, IValidator ... validators) {
		try {
			String value = (String) this.getString(fieldName);
			IFormScannerComponentHandler handler = this.getHandler(fieldName);
			for(IValidator validator: validators) {
				if(!validator.validate(value)) {
					handler.setValid(this.fields.get(fieldName), this.viewInstance, false);
					return false;
				}
			}
			handler.setValid(this.fields.get(fieldName), this.viewInstance, true);
			return true;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot validate the field '"+fieldName+"'.", exception);
		}
	}

	@Override
	public String getString(String fieldName) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		Object value = null;
		try {
			value = handler.getValue(field, this.viewInstance, String.class);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot get the String value of form field '"+fieldName+"'.", exception);
		}
		if(null != value && !(value instanceof String)) {
			throw new IllegalStateException("The component type of field '"+fieldName+"' does not handle string values. Use different method.");
		}
		return (String) value;
	}

	@Override
	public int getInt(String fieldName) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		Object value = null;
		try {
			value = handler.getValue(field, this.viewInstance, Integer.class);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot get the Integer value of form field '"+fieldName+"'.", exception);
		}
		if(null != value && !(value instanceof Integer)) {
			throw new IllegalStateException("The component type of field '"+fieldName+"' does not handle integer values. Use different method.");
		}
		return (Integer) value;
	}

	@Override
	public boolean getBoolean(String fieldName) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		Object value = null;
		try {
			value = handler.getValue(field, this.viewInstance, Boolean.class);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot get the Boolean value of form field '"+fieldName+"'.", exception);
		}
		if(null != value && !(value instanceof Boolean)) {
			throw new IllegalStateException("The component type of field '"+fieldName+"' does not handle boolean values. Use different method.");
		}
		return (Boolean) value;
	}

	@Override
	public double getDouble(String fieldName) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		Object value = null;
		try {
			value = handler.getValue(field, this.viewInstance, Double.class);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot get the Double value of form field '"+fieldName+"'.", exception);
		}
		if(null != value && !(value instanceof Double)) {
			throw new IllegalStateException("The component type of field '"+fieldName+"' does not handle double values. Use different method.");
		}
		return (Double) value;
	}

	@Override
	public <T> T getObject(String fieldName, Class<T> itemType) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		Object value = null;
		try {
			value = handler.getValue(field, this.viewInstance, itemType);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot get the Double value of form field '"+fieldName+"'.", exception);
		}
		if(null != value && !itemType.isAssignableFrom(value.getClass())) {
			throw new IllegalStateException("The component type of field '"+fieldName+"' does not handle '"+itemType.getSimpleName()+"' values. Use different method.");
		}
		return (T) value;
	}

	@Override
	public void setString(String fieldName, String value) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		try {
			handler.setValue(field, this.viewInstance, value);
		} catch(IllegalArgumentException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot set the String value for form field '"+fieldName+"'.", exception);
		}
	}

	@Override
	public void setInt(String fieldName, int value) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		try {
			handler.setValue(field, this.viewInstance, value);
		} catch(IllegalArgumentException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot set the Integer value for form field '"+fieldName+"'.", exception);
		}
	}

	@Override
	public void setBoolean(String fieldName, boolean value) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		try {
			handler.setValue(field, this.viewInstance, value);
		} catch(IllegalArgumentException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot set the Boolean value for form field '"+fieldName+"'.", exception);
		}
	}

	@Override
	public void setDouble(String fieldName, double value) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		try {
			handler.setValue(field, this.viewInstance, value);
		} catch(IllegalArgumentException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot set the Double value for form field '"+fieldName+"'.", exception);
		}
	}

	@Override
	public <T> void setObject(String fieldName, T value) {
		Field field = this.fields.get(fieldName);
		if(null == field) {
			throw new IllegalArgumentException("Unknown field: '"+fieldName+"'.");
		}
		IFormScannerComponentHandler handler = this.fieldHandlers.get(field.getType());
		try {
			handler.setValue(field, this.viewInstance, value);
		} catch(IllegalArgumentException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot set the '"+value.getClass().getSimpleName()+"' value for form field '"+fieldName+"'.", exception);
		}
	}
	
	private IFormScannerComponentHandler getHandler(String fieldName) {
		return this.fieldHandlers.get(this.fields.get(fieldName).getType());
	}
}
