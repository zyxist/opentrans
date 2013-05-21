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

/**
 * Binds two vertices by inserting a new track between them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class BindVertices extends AbstractOperation {
	
	/**
	 * Binds two tracks together by inserting another track between them.
	 * 
	 * @param v1
	 * @param v2
	 * @return 
	 */
	public boolean bind(VertexRecord v1, VertexRecord v2) {
		return this.evaluateCases(new TransformInput(null, null, v1, v2));
	}

	@Override
	protected void configure() {
		this.initialCondition(bothVertices(haveOneTrack()));
		this.register(and(fstVertexTrack(withType(NetworkConst.TRACK_STRAIGHT)), secVertexTrack(withType(NetworkConst.TRACK_STRAIGHT))), 
			this.connectTwoStraightTracks()
		);
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importAllMissingNeighbors(api.getUnitOfWork(), input.v1, input.v2);
	}

	private IOperationCase connectTwoStraightTracks() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				TrackRecord adjustedStraightTrack = input.v2.getTrack();
				
				TrackRecord tr = new TrackRecord();
				tr.setType(NetworkConst.TRACK_CURVED);
				tr.setVertices(input.v1, input.v2);
				input.v1.addTrack(tr);
				input.v2.addTrack(tr);
				api.getUnitOfWork().addTrack(tr);
				api.matchStraightTrackAndCurve(tr, adjustedStraightTrack, input.v1, input.v2);
			}
		};
	}

}
