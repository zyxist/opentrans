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

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Evaluates a vertex condition on both vertices in the transform input.
 * 
 * @author Tomasz Jędrzejewski
 */
public class BothVertexCondition implements ICondition<TransformInput> {
	private final ICondition<IVertexRecord> condition;
	
	public BothVertexCondition(ICondition<IVertexRecord> condition) {
		this.condition = Preconditions.checkNotNull(condition);
	}

	@Override
	public boolean matches(TransformInput input) {
		return this.condition.matches(input.v1) && this.condition.matches(input.v2);
	}
}
