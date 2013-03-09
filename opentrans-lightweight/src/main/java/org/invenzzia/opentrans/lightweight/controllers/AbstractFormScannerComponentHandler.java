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

import java.lang.reflect.Field;

/**
 * Support utilities for writing form scanner component handlers that
 * reduce the amount of boilerplate code.
 * 
 * @author zyxist
 */
public abstract class AbstractFormScannerComponentHandler implements IFormScannerComponentHandler {
	
	/**
	 * Extracts the given field instance.
	 * 
	 * @param type Field type.
	 * @param field The field.
	 * @param instance View instance.
	 * @return Extracted component.
	 */
	protected <T> T extract(Class<T> type, Field field, Object instance) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		return (T) field.get(instance);
	}
}
