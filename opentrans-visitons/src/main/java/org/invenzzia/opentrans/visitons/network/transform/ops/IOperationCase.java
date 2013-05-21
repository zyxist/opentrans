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

package org.invenzzia.opentrans.visitons.network.transform.ops;

import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Represents a supported use case of the operation. Use cases are registered
 * in the operation together with the conditions and input modifiers. When the
 * {@link #execute(org.invenzzia.opentrans.visitons.network.transform.TransformInput)} is
 * spawned, we are guaranteed that it satisfies the defined rules.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IOperationCase {
	/**
	 * Executes the actual transformation of the tracks and vertices.
	 * 
	 * @param input Input: what has been passed to the operations?
	 * @param api Common transformation API.
	 */
	public void execute(TransformInput input, ITransformAPI api);
}
