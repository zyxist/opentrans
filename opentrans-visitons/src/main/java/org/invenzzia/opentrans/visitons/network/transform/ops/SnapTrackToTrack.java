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

import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;
import static org.invenzzia.opentrans.visitons.network.transform.conditions.Conditions.*;
import static org.invenzzia.opentrans.visitons.network.transform.modifiers.Modifiers.*;

/**
 * Snaps the track to another track.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SnapTrackToTrack extends AbstractOperation {
	
	/**
	 * Bound vertex that is being moved by the cursor now.
	 * 
	 * @param boundVertex
	 * @param clickedTrack
	 * @return 
	 */
	public boolean snap(VertexRecord boundVertex, TrackRecord clickedTrack) {
		return this.evaluateCases(new TransformInput(clickedTrack, null, boundVertex, null));
	}

	@Override
	protected void configure() {
		this.initialCondition(and(track(isOpen()), vertex(hasOneTrack())));
		this.register(
			and(fstVertexTrack(withType(NetworkConst.TRACK_CURVED)), track(withType(NetworkConst.TRACK_STRAIGHT))),
			extractOpenVertex(), this.bindCurveToStraight()
		);
		this.register(
			and(fstVertexTrack(withType(NetworkConst.TRACK_STRAIGHT)), track(withType(NetworkConst.TRACK_CURVED))),
			all(extractOpenVertex(), swapTracks(), swapVertices()), this.bindCurveToStraight()
		);
	}

	private IOperationCase bindCurveToStraight() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord newVertex = api.getUnitOfWork().connectVertices(input.v1, input.v2);
				api.matchStraightTrackAndCurve(input.t1, input.v1.getTrack(), input.v1.getTrack().getOppositeVertex(newVertex), newVertex);
			}
		};
	}
}
