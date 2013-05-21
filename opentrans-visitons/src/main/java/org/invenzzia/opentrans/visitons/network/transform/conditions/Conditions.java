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
import org.invenzzia.opentrans.visitons.network.VertexRecord;

/**
 * Helper class for spawning condition objects in DSL-like manner.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Conditions {
	private Conditions() {
	}

	public static <T> AndCondition<T> and(ICondition<T> ... conditions) {
		return new AndCondition<>(conditions);
	}

	public static BothTrackCondition bothTracks(ICondition<TrackRecord> condition) {
		return new BothTrackCondition(condition);
	}

	public static OneOfTracksCondition oneOfTracks(ICondition<TrackRecord> firstCondition, ICondition<TrackRecord> secondCondition) {
		return new OneOfTracksCondition(firstCondition, secondCondition);
	}

	public static SingleTrackCondition track(ICondition<TrackRecord> condition) {
		return new SingleTrackCondition(condition);
	}
	
	/**
	 * Extracts the track from the first vertex, which has only one track connected.
	 * 
	 * @param condition Condition to evaluate.
	 * @return Condition.
	 */
	public static FirstVertexTrackCondition fstVertexTrack(ICondition<TrackRecord> condition) {
		return new FirstVertexTrackCondition(condition);
	}
	
	/**
	 * Extracts the track from the second vertex, which has only one track connected.
	 * 
	 * @param condition Condition to evaluate.
	 * @return Condition.
	 */
	public static SecondVertexTrackCondition secVertexTrack(ICondition<TrackRecord> condition) {
		return new SecondVertexTrackCondition(condition);
	}

	public static TrackTypeCondition withType(byte trackType) {
		return new TrackTypeCondition(trackType, true);
	}

	public static TrackTypeCondition withoutType(byte trackType) {
		return new TrackTypeCondition(trackType, false);
	}

	public static ModeCondition withMode(byte mode) {
		return new ModeCondition(mode);
	}
	
	public static OpenTrackCondition isOpen() {
		return new OpenTrackCondition();
	}
	
	public static SingleVertexCondition vertex(ICondition<VertexRecord> condition) {
		return new SingleVertexCondition(condition);
	}
	
	public static BothVertexCondition bothVertices(ICondition<VertexRecord> condition) {
		return new BothVertexCondition(condition);
	}
	
	public static VertexWithAllTracksCondition hasAllTracks() {
		return new VertexWithAllTracksCondition();
	}
	
	public static VertexWithAllTracksCondition haveAllTracks() {
		return new VertexWithAllTracksCondition();
	}
	
	public static VertexWithOneTrackCondition hasOneTrack() {
		return new VertexWithOneTrackCondition();
	}
	
	public static VertexWithOneTrackCondition haveOneTrack() {
		return new VertexWithOneTrackCondition();
	}
}
