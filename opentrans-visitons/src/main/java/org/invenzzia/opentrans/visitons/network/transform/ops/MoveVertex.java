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
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

import static org.invenzzia.opentrans.visitons.network.transform.conditions.Conditions.*;

/**
 * This operation allows moving a single vertex across the world.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MoveVertex extends AbstractOperation {

	/**
	 * Moves the given vertex to the new position, if possible.
	 * 
	 * @param vr Vertex being moved.
	 * @param x New position of this vertex.
	 * @param y New position of this vertex.
	 * @param mode Editing mode
	 * @return True, if the operation succeeded.
	 */
	public boolean move(VertexRecord vr, double x, double y, byte mode) {
		if(vr.hasOneTrack()) {
			return this.evaluateCases(new TransformInput(vr.getTrack(), null, vr, null, x, y, mode));
		} else {
			TrackRecord firstTrack = vr.getFirstTrack();
			TrackRecord secondTrack = vr.getSecondTrack();
			if(firstTrack.getType() == NetworkConst.TRACK_STRAIGHT) {
				return this.evaluateCases(new TransformInput(firstTrack, secondTrack, vr, null, x, y, mode));
			} else {
				return this.evaluateCases(new TransformInput(secondTrack, firstTrack, vr, null, x, y, mode));
			}
		}
	}
	
	@Override
	protected void configure() {
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_DEFAULT)),
			this.extendStraightTrackAlongTangent()
		);
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_ALT1)),
			this.openStraightTrackFreeMovement()
		);
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_CURVED))),
			this.moveOpenCurvedTrack()
		);
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_FREE))),
			this.moveOpenFreeTrack()
		);
	}

	private IOperationCase extendStraightTrackAlongTangent() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord opposite = input.t1.getOppositeVertex(input.v1);
				
				if(opposite.hasAllTracks()) {
					double buf[] = new double[8];
					LineOps.toGeneral(opposite.x(), opposite.y(), input.v1.x(), input.v1.y(), 0, buf);
					LineOps.toOrthogonal(0, 3, buf, input.a1, input.a2);
					LineOps.intersection(0, 3, 6, buf);
					input.v1.setPosition(buf[6], buf[7]);
				} else {
					input.v1.setPosition(input.a1, input.a2);
				}
				api.calculateStraightLine(input.t1);
			}
		};
	}

	private IOperationCase openStraightTrackFreeMovement() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				TrackRecord previousTrack = input.t1.getPreviousTrack();
				input.v1.setPosition(input.a1, input.a2);
				if(null == previousTrack) {
					api.calculateStraightLine(input.t1);
				} else if(previousTrack.getType() == NetworkConst.TRACK_CURVED) {
					VertexRecord connecting = input.t1.getOppositeVertex(input.v1);
					api.curveFollowsStraightTrack(input.v1, connecting, previousTrack.getOppositeVertex(connecting));
				} else if(previousTrack.getType() == NetworkConst.TRACK_FREE) {
					api.calculateStraightLine(input.t1);
					api.calculateFreeCurve(previousTrack);
				}
			}
		};
	}

	private IOperationCase moveOpenCurvedTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				input.v1.setPosition(input.a1, input.a2);
				api.curveFollowsPoint(input.t1, input.v1);
			}
		};
	}

	private IOperationCase moveOpenFreeTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				input.v1.setPosition(input.a1, input.a2);
				api.calculateFreeCurve(input.t1);
			}
		};
	}

}
