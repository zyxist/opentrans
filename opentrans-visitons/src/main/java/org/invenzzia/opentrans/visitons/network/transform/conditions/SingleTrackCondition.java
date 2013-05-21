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
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Evaluates a condition on the single track from the input.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SingleTrackCondition implements ICondition<TransformInput> {
	private ICondition<TrackRecord> condition;
	private int which;
	
	public SingleTrackCondition(ICondition<TrackRecord> condition, int which) {
		this.condition = Preconditions.checkNotNull(condition);
		this.which = which;
	}

	@Override
	public boolean matches(TransformInput input) {
		return this.condition.matches(this.which == 1 ? input.t1 : input.t2);
	}

}
