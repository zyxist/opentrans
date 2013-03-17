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

import org.invenzzia.opentrans.lightweight.validator.IValidator;

/**
 * Scans the given panel for the form annotations and provides
 * the methods for managing the form controls remotely.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IFormScanner {
	public <T> void discoverValidators(Class<T> controllerClass, T controllerInstance);
	public <T> void bindFields(Class<T> viewClass, T viewInstance);
	/**
	 * Have we performed the discovery task?
	 * 
	 * @return True, if the scanner has been fully initialized.
	 */
	public boolean isDiscovered();
	/**
	 * Clears all the bindings.
	 */
	public void clear();
	
	/**\
	 * Performs the validation of the given form field. The method shall
	 * change the state of the field, if the field is invalid, and display
	 * the error somehow.
	 * 
	 * @param fieldName The name of the form field
	 * @param validators List of validators to apply.
	 * @return True, if the field is valid.
	 */
	public boolean validate(String fieldName, IValidator ... validators);
	/**
	 * Returns the string value of the form field.
	 * 
	 * @param fieldName Name of the form field.
	 * @return Field value.
	 */
	public String getString(String fieldName);
	/**
	 * Returns the integer value of the form field.
	 * 
	 * @param fieldName Name of the form field.
	 * @return Field value.
	 */
	public int getInt(String fieldName);
	/**
	 * Returns the boolean value of the form field.
	 * 
	 * @param fieldName Name of the form field.
	 * @return Field value.
	 */
	public boolean getBoolean(String fieldName);
	/**
	 * Returns the double value of the form field.
	 * 
	 * @param fieldName Name of the form field.
	 * @return Field value.
	 */
	public double getDouble(String fieldName);
	/**
	 * Returns the object value of the form field.
	 * 
	 * @param fieldName Name of the form field.
	 * @return Field value.
	 */
	public <T> T getObject(String fieldName, Class<T> itemType);
	
	public void setString(String fieldName, String value);
	public void setInt(String fieldName, int value);
	public void setBoolean(String fieldName, boolean value);
	public void setDouble(String fieldName, double value);
	public <T> void setObject(String fieldName, T value);
}
