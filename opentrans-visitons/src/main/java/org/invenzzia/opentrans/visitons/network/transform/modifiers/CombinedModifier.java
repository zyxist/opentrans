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

package org.invenzzia.opentrans.visitons.network.transform.modifiers;

import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Combines several modifiers into a single one.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CombinedModifier implements IModifier {
	private final IModifier modifiers[];
	
	public CombinedModifier(IModifier ... modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public void modify(TransformInput input) {
		for(IModifier subModifier: this.modifiers) {
			subModifier.modify(input);
		}
	}
}
