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
 * Verifies that the first condition is satisfied on one of the tracks, and
 * the second condition - on the second one.
 * 
 * @author Tomasz Jędrzejewski
 */
public class OneOfTracksCondition implements ICondition<TransformInput> {
	private final ICondition<TrackRecord> firstCondition;
	private final ICondition<TrackRecord> secondCondition;

	public OneOfTracksCondition(ICondition<TrackRecord> firstCondition, ICondition<TrackRecord> secondCondition) {
		this.firstCondition = Preconditions.checkNotNull(firstCondition);
		this.secondCondition = Preconditions.checkNotNull(secondCondition);
	}

	@Override
	public boolean matches(TransformInput input) {
		return (this.firstCondition.matches(input.t1) && this.secondCondition.matches(input.t2))
			^ (this.secondCondition.matches(input.t1) && this.firstCondition.matches(input.t2));
	}
}
