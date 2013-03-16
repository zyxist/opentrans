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

package org.invenzzia.opentrans.lightweight.validator;

import com.google.common.base.Preconditions;

/**
 * Validates whether a double value falls into a specified range.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DoubleRangeValidator implements IValidator<String> {
	private final double from;
	private final double to;
	
	/**
	 * Creates the range validator.
	 * 
	 * @param from Beginning of the range.
	 * @param to End of the range.
	 */
	public DoubleRangeValidator(double from, double to) {
		Preconditions.checkArgument(from < to, "From must be lower than to");
		this.from = from;
		this.to = to;
	}

	@Override
	public String getErrorMessage() {
		return "Must be from range "+this.from+"-"+this.to;
	}

	@Override
	public boolean validate(String value) {
		double val = Double.parseDouble(value);
		return (val >= from && val < to);
	}	
}
