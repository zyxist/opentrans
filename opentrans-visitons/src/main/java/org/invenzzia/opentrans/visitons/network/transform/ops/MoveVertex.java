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
import static org.invenzzia.opentrans.visitons.network.transform.modifiers.Modifiers.*;

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
		return this.evaluateCases(new TransformInput(null, null, vr, null, x, y, mode));
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importMissingNeighboursSmarter(api.getUnitOfWork(), input.v1);
		if(input.v1.hasOneTrack()) {
			input.t1 = input.v1.getTrack();
		} else {
			input.t1 = input.v1.getFirstTrack();
			input.t2 = input.v1.getSecondTrack();
		}
	}
	
	@Override
	protected void configure() {
		this.initialModifier(all(makeStraightTrackFirst(), getOppositeVertexForSecondTrack()));
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_DEFAULT)),
			this.extendStraightTrackAlongTangent()
		);
		this.register(
			and(vertex(hasOneTrack()), track(withType(NetworkConst.TRACK_STRAIGHT)), not(withMode(NetworkConst.MODE_DEFAULT))),
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
		this.register(
			and(
				// we should have another straight or free track on the opposite side of the curve
				secondTrack(and(withType(NetworkConst.TRACK_CURVED), not(isOpen()))),
				withMode(NetworkConst.MODE_DEFAULT)
			),
			this.moveExtendCurve()
		);
		this.register(
			and(
				secondVertex(hasAllTracks()),
				secondTrack(withType(NetworkConst.TRACK_FREE)),
				withMode(NetworkConst.MODE_DEFAULT)
			),
			this.moveLenghtenStraightTrackConnectedToFreeCurve()
		);
		this.register(
			and(
				not(withMode(NetworkConst.MODE_DEFAULT))
			),
			this.moveVertexMostComplexCase()
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

	/**
	 * First case of moving the internal vertex: a lenghtening a straight track, which affects the straight
	 * track on the opposite side of the curve. Both tracks are lenghtened by the same distance, changing the
	 * curve radius. The curve centre moves along a imaginary straight line.
	 */
	private IOperationCase moveExtendCurve() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				TrackRecord closerStraightTrack = input.t1;
				TrackRecord furtherStraightTrack = input.t2.getOppositeVertex(input.v1).getOppositeTrack(input.t2);
				VertexRecord clStVert2 = closerStraightTrack.getOppositeVertex(input.v1);
				
				double buf[] = new double[8];
				double mov = LineOps.vectorLengtheningDistance(clStVert2.x(), clStVert2.y(), input.v1.x(), input.v1.y(), input.a1, input.a2, 0, buf);

				if(furtherStraightTrack.getType() == NetworkConst.TRACK_STRAIGHT) {
					if(furtherStraightTrack.computeLength() + mov < 0.0 || closerStraightTrack.computeLength() + mov < 0.0) {
						return;
					}
					VertexRecord ftStVert2 = furtherStraightTrack.getOppositeVertex(input.v2);
					LineOps.lenghtenVector(ftStVert2.x(), ftStVert2.y(), input.v2.x(), input.v2.y(), mov, 0, buf);

					if(!api.getWorld().isWithinWorld(buf[0], buf[1]) || !api.getWorld().isWithinWorld(buf[6], buf[7])) {
						return;
					}
					input.v2.setPosition(buf[0], buf[1]);
					input.v1.setPosition(buf[6], buf[7]);

					api.calculateStraightLine(furtherStraightTrack);
					api.calculateStraightLine(closerStraightTrack);
					api.matchStraightTrackAndCurve(input.t2, furtherStraightTrack, input.v1, input.t2.getOppositeVertex(input.v1));
				} else {
					// Oops, you're not straight. But don't worry. This is also supposed to work.
				}
			}
		};
	}

	/**
	 * Alternative case of the method above: when the curved track is a free (double curve).
	 */
	private IOperationCase moveLenghtenStraightTrackConnectedToFreeCurve() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				if(!api.getWorld().isWithinWorld(input.a1, input.a2)) {
					return;
				}

				double buf[] = new double[8];
				VertexRecord opposite = input.t1.getOppositeVertex(input.v1);
				LineOps.toGeneral(opposite.x(), opposite.y(), input.v1.x(), input.v1.y(), 0, buf);
				LineOps.toOrthogonal(0, 3, buf, input.a1, input.a2);
				LineOps.intersection(0, 3, 6, buf);
				input.v1.setPosition(buf[6], buf[7]);
				api.calculateStraightLine(input.t1);
				api.calculateFreeCurve(input.t2);
			}
		};
	
	}

	private IOperationCase moveVertexMostComplexCase() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				if(!api.getWorld().isWithinWorld(input.a1, input.a2)) {
					return;
				}
				input.v1.setPosition(input.a1, input.a2);
				
				TrackRecord curvedTrack = input.t2;
				TrackRecord straightTrack = input.t1;
				TrackRecord afterCurvedTrack = curvedTrack.getOppositeTrack(straightTrack);
				TrackRecord afterStraightTrack = straightTrack.getOppositeTrack(curvedTrack);

				// Considering the part towards the straight track.
				if(null == afterStraightTrack) {
					api.calculateStraightLine(straightTrack);
				} else if(afterStraightTrack.getType() == NetworkConst.TRACK_CURVED) {
					VertexRecord vr = straightTrack.getOppositeVertex(input.v1);
					api.curveFollowsStraightTrack(input.v1, vr, afterStraightTrack.getOppositeVertex(vr));
				} else if(afterStraightTrack.getType() == NetworkConst.TRACK_FREE) {
					api.calculateStraightLine(straightTrack);
					api.calculateFreeCurve(afterStraightTrack);
				}
				
				// Considering the part towards the curved track.
				if(curvedTrack.getType() == NetworkConst.TRACK_FREE) {
					api.calculateFreeCurve(curvedTrack);
				} else {
					if(null == afterCurvedTrack) {
						api.curveFollowsPoint(curvedTrack, input.v2);
					} else if(afterCurvedTrack.getType() == NetworkConst.TRACK_STRAIGHT) {
						api.matchStraightTrackAndCurve(curvedTrack, afterCurvedTrack, input.v1, curvedTrack.getOppositeVertex(input.v1));
					} else if(afterCurvedTrack.getType() == NetworkConst.TRACK_FREE) {
						api.curveFollowsPoint(curvedTrack, input.v2);
						api.calculateFreeCurve(afterCurvedTrack);
					}
				}
			}
		};
	}

}
