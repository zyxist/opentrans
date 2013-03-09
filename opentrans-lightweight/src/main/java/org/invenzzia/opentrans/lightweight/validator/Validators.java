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

/**
 * Quick access to most common validators.
 * 
 * @author zyxist
 */
public class Validators {
	private static final DoubleValidator doubleValidator = new DoubleValidator();
	
	private Validators() {
	}
	
	public static IValidator lengthBetween(int from, int to) {
		return new LengthValidator(from, to);
	}
	
	public static IValidator isInteger() {
		return null;
	}
	
	public static IValidator isDouble() {
		return doubleValidator;
	}
}
