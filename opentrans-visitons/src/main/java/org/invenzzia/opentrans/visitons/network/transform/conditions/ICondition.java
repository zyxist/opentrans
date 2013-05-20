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

package org.invenzzia.opentrans.visitons.network.transform.conditions;

/**
 * Represents a single condition that might be evaluated. The concept is
 * very similar to Hamcrest library.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface ICondition<T> {
	/**
	 * Evaluates the given input and returns <strong>true</strong>, if it
	 * matches the condition.
	 * 
	 * @param input The input to analyze.
	 * @return True, if the input matches the condition.
	 */
	public boolean matches(T input);
}
