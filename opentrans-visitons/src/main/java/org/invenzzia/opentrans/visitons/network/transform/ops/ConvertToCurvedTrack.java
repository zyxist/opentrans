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

import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

import static org.invenzzia.opentrans.visitons.network.transform.conditions.Conditions.*;

/**
 * The case, where we have a free track between two straight ones and
 * we want to make it back curved.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConvertToCurvedTrack extends AbstractOperation {
	
	public boolean convert(TrackRecord track) {
		return this.evaluateCases(new TransformInput(track, null, null, null));
	}

	@Override
	protected void configure() {
		this.register(
			track(and(
				surroundedBy(withType(NetworkConst.TRACK_STRAIGHT)),
				withType(NetworkConst.TRACK_FREE)
			)), this.convertToCurvedNoJunctions());
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importAllMissingNeighbors(api.getUnitOfWork(), input.t1.getFirstVertex(), input.t1.getSecondVertex());
	}

	private IOperationCase convertToCurvedNoJunctions() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord firstVertex = (VertexRecord) input.t1.getFirstVertex();
				VertexRecord secondVertex = (VertexRecord) input.t1.getSecondVertex();
				TrackRecord firstTrack = (firstVertex.hasAllTracks() ? firstVertex.getOppositeTrack(input.t1) : null);
				TrackRecord secondTrack = (secondVertex.hasAllTracks() ? secondVertex.getOppositeTrack(input.t1) : null);
				
				if(null == firstTrack && null == secondTrack) {
					return;
				}
				input.t1.setType(NetworkConst.TRACK_CURVED);
				if(null == firstTrack) {
					api.curveFollowsPoint(input.t1, firstVertex);
				} else if(null == secondTrack) {
					api.curveFollowsPoint(input.t1, secondVertex);
				} else {
					double buffer[] = new double[8];
					LineOps.toGeneral(firstVertex.x(), firstVertex.y(), firstVertex.tangent(), 0, buffer);
					LineOps.toGeneral(secondVertex.x(), secondVertex.y(), secondVertex.tangent(), 3, buffer);
					LineOps.intersection(0, 3, 6, buffer);
					
					double d1 = LineOps.distance(firstVertex.x(), firstVertex.y(), buffer[6], buffer[7]);
					double d2 = LineOps.distance(secondVertex.x(), secondVertex.y(), buffer[6], buffer[7]);
					if(d1 < d2) {
						api.matchStraightTrackAndCurve(input.t1, secondTrack, firstVertex, secondVertex);
					} else {
						api.matchStraightTrackAndCurve(input.t1, firstTrack, secondVertex, firstVertex);
					}
				}
			}
		};
	}
}
