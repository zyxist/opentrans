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

import com.google.common.base.Preconditions;

/**
 * Validates if an integer is within the given range.
 * 
 * @author zyxist
 */
public class IntegerRangeValidator implements IValidator<String> {
	private final int from;
	private final int to;
	
	public IntegerRangeValidator(int from, int to) {
		Preconditions.checkArgument(from < to, "'from' value must be lower than 'to'.");
		this.from = from;
		this.to = to;
	}

	@Override
	public String getErrorMessage() {
		return "Must be from range "+this.from+"-"+this.to;
	}

	@Override
	public boolean validate(String value) {
		int intval = Integer.parseInt(value.trim());
		return (intval >= this.from && intval < this.to);
	}
}
