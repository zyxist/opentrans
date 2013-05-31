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

import org.invenzzia.helium.data.StateReverter;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;
import static org.invenzzia.opentrans.visitons.network.transform.conditions.Conditions.*;

/**
 * Simple operation that allows converting any curved or free track into
 * a straight track with applying appropriate transformations. There is one
 * condition: neither of the surrounding tracks must be straight.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConvertToStraightTrack extends AbstractOperation {

	public boolean convert(TrackRecord track) {
		return this.evaluateCases(new TransformInput(track, null, null, null));
	}
	
	@Override
	protected void configure() {
		this.register(track(surroundedBy(withoutType(NetworkConst.TRACK_STRAIGHT))), this.convertToStraight());
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importAllMissingNeighbors(api.getUnitOfWork(), input.t1.getFirstVertex(), input.t1.getSecondVertex());
	}

	private IOperationCase convertToStraight() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				TrackRecord firstTrack = (input.t1.getFirstVertex().hasAllTracks() ? ((VertexRecord)input.t1.getFirstVertex()).getOppositeTrack(input.t1) : null);
				TrackRecord secondTrack = (input.t1.getSecondVertex().hasAllTracks() ? ((VertexRecord)input.t1.getSecondVertex()).getOppositeTrack(input.t1) : null);
				
				StateReverter reverter = new StateReverter();
				reverter.remember(firstTrack);
				reverter.remember(secondTrack);
				reverter.remember(input.t1);
				reverter.remember((VertexRecord)input.t1.getFirstVertex());
				reverter.remember((VertexRecord)input.t1.getSecondVertex());
				
				input.t1.setType(NetworkConst.TRACK_STRAIGHT);
				api.calculateStraightLine(input.t1);
				input.t1.getFirstVertex().setTangentFor(firstTrack, Geometry.normalizeAngle(input.t1.getFirstVertex().tangentFor(input.t1) + Math.PI));
				input.t1.getSecondVertex().setTangentFor(secondTrack, Geometry.normalizeAngle(input.t1.getSecondVertex().tangentFor(input.t1) + Math.PI));
				if(null != firstTrack) {
					if(firstTrack.getType() == NetworkConst.TRACK_FREE) {
						api.calculateFreeCurve(firstTrack);
					} else {
						VertexRecord vr = (VertexRecord)input.t1.getVertexTo(firstTrack);
						VertexRecord stv = (VertexRecord)input.t1.getOppositeVertex(vr);
						VertexRecord cuv = (VertexRecord)firstTrack.getOppositeVertex(vr);
						api.curveFollowsStraightTrack(stv, vr, cuv);
					}
				}
				if(null != secondTrack) {
					if(secondTrack.getType() == NetworkConst.TRACK_FREE) {
						api.calculateFreeCurve(secondTrack);
					} else {
						VertexRecord vr = (VertexRecord)input.t1.getVertexTo(secondTrack);
						VertexRecord stv = (VertexRecord)input.t1.getOppositeVertex(vr);
						VertexRecord cuv = (VertexRecord)secondTrack.getOppositeVertex(vr);
						api.curveFollowsStraightTrack(stv, vr, cuv);
					}
				}
				
				if(!input.t1.getFirstVertex().areTangentsOK() || !input.t1.getSecondVertex().areTangentsOK()) {
					reverter.restore();
				}
			}
		};
	}

}
