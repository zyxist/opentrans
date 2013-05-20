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
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AndCondition<T> implements ICondition<T> {
	private final ICondition<T> conditions[];

	public AndCondition(ICondition<T> ... conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean matches(T input) {
		for(ICondition<T> condition: this.conditions) {
			if(!condition.matches(input)) {
				return false;
			}
		}
		return true;
	}
}
