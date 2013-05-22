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

import org.invenzzia.opentrans.visitons.network.TrackRecord;

/**
 * Checks if a given track is surrounded by other tracks with certain
 * characteristics.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SurroundedByCondition implements ICondition<TrackRecord> {
	private ICondition<TrackRecord> condition;
	
	public SurroundedByCondition(ICondition<TrackRecord> condition) {
		this.condition = condition;
	}

	@Override
	public boolean matches(TrackRecord input) {
		TrackRecord firstTrack = (input.getFirstVertex().hasAllTracks() ? input.getFirstVertex().getOppositeTrack(input) : null);
		TrackRecord secondTrack = (input.getSecondVertex().hasAllTracks() ? input.getSecondVertex().getOppositeTrack(input) : null);
		boolean firstCondition = (null == firstTrack ? true : this.condition.matches(firstTrack));
		boolean secondCondition = (null == secondTrack ? true : this.condition.matches(secondTrack));
		return firstCondition && secondCondition;
	}
}
