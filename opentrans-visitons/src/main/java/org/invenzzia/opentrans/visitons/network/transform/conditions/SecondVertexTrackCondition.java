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
 * Extracts the track from the vertex <tt>v2</tt> and evaluates another condition
 * on it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SecondVertexTrackCondition implements ICondition<TransformInput> {
	private ICondition<TrackRecord> condition;
	
	public SecondVertexTrackCondition(ICondition<TrackRecord> condition) {
		this.condition = Preconditions.checkNotNull(condition);
	}

	@Override
	public boolean matches(TransformInput input) {
		return this.condition.matches(input.v2.getTrack());
	}

}
