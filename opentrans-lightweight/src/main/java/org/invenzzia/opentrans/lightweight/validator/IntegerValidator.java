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
package org.invenzzia.opentrans.lightweight.validator;

import java.util.regex.Pattern;

/**
 * Checks if the value is a valid integer.
 * @author zyxist
 */
public class IntegerValidator implements IValidator<String> {
	private static final Pattern INTEGER_PATTERN = Pattern.compile("^\\s*[0-9]+\\s*$");

	@Override
	public String getErrorMessage() {
		return "Must be an integer";
	}

	@Override
	public boolean validate(String value) {
		return INTEGER_PATTERN.matcher(value).matches();
	}
}
