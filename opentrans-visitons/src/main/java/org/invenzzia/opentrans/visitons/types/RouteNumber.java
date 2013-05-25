/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.types;

import java.util.Objects;

/**
 * In order to provide better sorting of routes, we introduce a special
 * type for representing the route numbers. Route number consists of two
 * parts: `numerical component` and `alphanumerical component`. At least one
 * of them must be set. The route number is displayed in the following
 * manner: <tt>[numerical component][alphanumerical component]</tt>.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class RouteNumber {
	public static final RouteNumber DEFAULT_NUMBER = new RouteNumber(0, null, true);
	
	/**
	 * Route number usually starts with a number.
	 */
	private final int numerical;
	/**
	 * In many cities, there can be some additional characters, i.e. "1A", "1B".
	 */
	private final String alphanumerical;
	/**
	 * Defines whether the numerical component is present.
	 */
	private final boolean numericalPresent;
	
	private RouteNumber(int numerical, String alphanumerical, boolean numericalPresent) {
		if(!numericalPresent && null == alphanumerical) {
			throw new IllegalArgumentException("The route number must have one of these components.");
		}
		this.numerical = numerical;
		this.alphanumerical = alphanumerical;
		this.numericalPresent = numericalPresent;
	}
	
	/**
	 * Converts a string representation to a {@link RouteNumber} object.
	 * 
	 * @param value
	 * @return 
	 */
	public static RouteNumber parseString(String value) {
		if("".equals(value)) {
			throw new IllegalArgumentException("Cannot parse an empty string.");
		}
		int length = value.length();
		StringBuilder nc = new StringBuilder();
		StringBuilder alc = new StringBuilder();
		int state = 0;
		for(int i = 0; i < length; i++) {
			char chr = value.charAt(i);
			if(state == 0) {
				if(Character.isDigit(chr)) {
					nc.append(chr);
				} else {
					alc.append(chr);
					state = 1;
				}
			} else {
				alc.append(chr);
			}
		}
		if(nc.length() == 0) {
			return new RouteNumber(0, alc.toString(), false);
		} else {
			return new RouteNumber(Integer.parseInt(nc.toString()), (alc.length() == 0 ? null : alc.toString()), true);
		}
	}
	
	@Override
	public String toString() {
		return (this.numericalPresent ? this.numerical : "") + (this.alphanumerical != null ? this.alphanumerical : "");
	}
	
	public int getNumerical() {
		return this.numerical;
	}
	
	public String getAlphanumerical() {
		return this.alphanumerical;
	}
	
	public boolean isNumericalPresent() {
		return this.numericalPresent;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + (this.numericalPresent ? this.numerical : 0);
		hash = 97 * hash + Objects.hashCode(this.alphanumerical);
		hash = 97 * hash + (this.numericalPresent ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final RouteNumber other = (RouteNumber) obj;
		if(this.numericalPresent != other.numericalPresent) {
			return false;
		}
		if(this.numericalPresent && this.numerical != other.numerical) {
			return false;
		}
		if(!Objects.equals(this.alphanumerical, other.alphanumerical)) {
			return false;
		}

		return true;
	}
}
