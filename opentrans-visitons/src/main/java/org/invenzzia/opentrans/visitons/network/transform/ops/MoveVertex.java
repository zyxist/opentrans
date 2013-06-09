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
import org.invenzzia.opentrans.visitons.geometry.LineOps;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.JunctionRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;

/**
 * This operation allows moving a single vertex across the world.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MoveVertex implements IOperation {
	private ITransformAPI api;
	/**
	 * New position (temporary)
	 */
	private double x;
	/**
	 * New position (temporary)
	 */
	private double y;
	/**
	 * If something goes wrong.
	 */
	private StateReverter reverter;
	/**
	 * If some propagation method sets this method to 'true', the whole operation is reverted.
	 */
	private boolean revert = false;
	
	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = api;
	}
	
	/**
	 * Moves the vertex to the new position.
	 * 
	 * @param vr
	 * @param x
	 * @param y
	 * @param mode
	 * @return 
	 */
	public boolean move(IVertexRecord vr, double x, double y, byte mode) {
		this.x = x;
		this.y = y;
		this.revert = false;
		this.reverter = new StateReverter();
		api.getRecordImporter().importMissingNeighboursSmarter(api.getUnitOfWork(), vr);
		if(vr instanceof JunctionRecord) {
			if(!this.findPositionAlongMaster((JunctionRecord) vr)) {
				return false;
			}
			this.propagate(vr, ((JunctionRecord) vr).getSlaveTrack(), mode);
		} else {
			if(mode == NetworkConst.MODE_DEFAULT) {
				if(!this.findPositionAlongStraight((VertexRecord) vr)) {
					return false;
				}
			}
			this.applyPosition((VertexRecord)vr, this.x, this.y);
			TrackRecord ft = vr.getFirstTrack();
			TrackRecord st = vr.getSecondTrack();
			if(null != ft && ft.getType() == NetworkConst.TRACK_FREE) {
				TrackRecord tmp = ft;
				ft = st;
				st = tmp;
			}
			if(null != ft) {
				this.propagate(vr, ft, mode);
			}
			if(null != st) {
				this.propagate(vr, st, mode);
			}
		}
		if(this.revert) {
			this.reverter.restore();
			return false;
		}
		return true;
	}

	/**
	 * This method is used to find the new position of the junction vertex on
	 * a master track.
	 * 
	 * @param junctionRecord
	 * @return 
	 */
	private boolean findPositionAlongMaster(JunctionRecord junctionRecord) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * For straight lines, we move along them by default.
	 * 
	 * @param vr
	 * @return 
	 */
	private boolean findPositionAlongStraight(VertexRecord vr) {
		TrackRecord straight = null;
		if(null != vr.getFirstTrack()) {
			if(vr.getFirstTrack().getType() == NetworkConst.TRACK_STRAIGHT) {
				straight = vr.getFirstTrack();
			}
		}
		if(null != vr.getSecondTrack()) {
			if(vr.getSecondTrack().getType() == NetworkConst.TRACK_STRAIGHT) {
				straight = vr.getSecondTrack();
			}
		}
		if(null != straight) {
			if(straight.getOppositeVertex(vr).hasAllTracks()) {
				double buf[] = new double[8];
				IVertexRecord v1 = straight.getFirstVertex();
				IVertexRecord v2 = straight.getSecondVertex();
				LineOps.toGeneral(v1.x(), v1.y(), v2.x(), v2.y(), 0, buf);
				LineOps.toOrthogonal(0, 3, buf, this.x, this.y);
				LineOps.intersection(0, 3, 6, buf);

				this.x = buf[6];
				this.y = buf[7];
			}
		}
		if(!this.api.getWorld().isWithinWorld(this.x, this.y)) {
			return false;
		}
		return true;		
	}

	private void applyPosition(VertexRecord vr, double x, double y) {
		vr.setPosition(x, y);
	}

	/**
	 * Propagate the position change to the next track. If there is a need,
	 * the recursion can go even deeper to process all the tracks that must
	 * be adjusted to the new position of this vertex.
	 * 
	 * @param vr
	 * @param track
	 * @param mode 
	 */
	private void propagate(IVertexRecord vr, TrackRecord track, byte mode) {
		switch(track.getType()) {
			case NetworkConst.TRACK_STRAIGHT:
				this.propagateStraightTrack(vr, track, mode);
				break;
			case NetworkConst.TRACK_CURVED:
				this.propagateCurvedTrack(vr, track, mode);
				break;
			case NetworkConst.TRACK_FREE:
				this.propagateFreeTrack(vr, track, mode);
				break;
		}
	}

	private void propagateStraightTrack(IVertexRecord vr, TrackRecord track, byte mode) {
		IVertexRecord opposite = track.getOppositeVertex(vr);
		if(opposite instanceof JunctionRecord) {
			JunctionRecord jr = (JunctionRecord) opposite;
			this.revert = true;
		} else {
			VertexRecord or = (VertexRecord) opposite;
			this.reverter.remember(or);
			this.reverter.remember(track);
			if(mode == NetworkConst.MODE_DEFAULT || opposite.hasOneTrack()) {
				this.api.calculateStraightLine(track);
				return;
			}
			TrackRecord nextTrack = or.getOppositeTrack(track);
			if(nextTrack.getType() == NetworkConst.TRACK_CURVED) {
				this.reverter.remember(nextTrack);
				this.reverter.remember(nextTrack.getOppositeVertex(or));
				this.api.curveFollowsStraightTrack(vr, or, nextTrack.getOppositeVertex(or));
			} else {
				this.api.calculateStraightLine(track);
				this.propagate(or, nextTrack, mode);
			}
		}
	}

	private void propagateCurvedTrack(IVertexRecord vr, TrackRecord track, byte mode) {
		IVertexRecord opposite = track.getOppositeVertex(vr);
		if(opposite instanceof JunctionRecord) {
			JunctionRecord jr = (JunctionRecord) opposite;
			if(vr.hasOneTrack()) {
				this.api.curveFollowsPoint(track, (VertexRecord) vr);
			} else {
				switch(jr.getMasterTrack().getType()) {
					case NetworkConst.TRACK_STRAIGHT:
						if(!this.api.vertexAlongStraightTrack(track, jr)) {
							this.revert = true;
						}
						break;
				}
			}
			this.revert = true;
		} else {		
			VertexRecord or = (VertexRecord) opposite;
			this.reverter.remember(or);
			this.reverter.remember(track);

			if(vr.hasOneTrack()) {
				this.api.curveFollowsPoint(track, (VertexRecord) vr);
				return;
			}
			if(vr instanceof VertexRecord) {
				VertexRecord vrc = (VertexRecord) vr;
				TrackRecord previous = vrc.getOppositeTrack(track);
				if(previous.getType() == NetworkConst.TRACK_FREE) {
					this.api.curveFollowsPoint(track, (VertexRecord) vr);
					return;
				}
			}
			
			if(opposite.hasOneTrack()) {
				this.api.curveFollowsPoint(track, or);
				return;
			}
			TrackRecord nextTrack = or.getOppositeTrack(track);
			if(nextTrack.getType() == NetworkConst.TRACK_STRAIGHT) {
				this.reverter.remember(nextTrack);
				this.reverter.remember(nextTrack.getOppositeVertex(or));
				this.api.matchStraightTrackAndCurve(track, nextTrack, vr, or);
			} else {
				this.api.curveFollowsPoint(track, or);
				this.propagate(or, nextTrack, mode);
			}
		}
	}

	private void propagateFreeTrack(IVertexRecord vr, TrackRecord track, byte mode) {
		this.api.calculateFreeCurve(track);
	}
}
