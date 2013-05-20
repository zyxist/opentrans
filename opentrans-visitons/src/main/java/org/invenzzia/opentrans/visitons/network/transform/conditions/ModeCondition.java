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

import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Verifies the tooling mode for operations that support several modes.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ModeCondition implements ICondition<TransformInput> {
	private final byte mode;
	
	public ModeCondition(byte mode) {
		this.mode = mode;
	}

	@Override
	public boolean matches(TransformInput input) {
		return input.mode == this.mode;
	}
}
