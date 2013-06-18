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

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.geometry.Geometry;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.JunctionRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;

/**
 * Reverses the junction, so that the tracks go into the opposite direction.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ReverseJunction implements IOperation {
	private ITransformAPI api;

	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = Preconditions.checkNotNull(api);
	}
	
	public boolean reverse(JunctionRecord junctionRecord) {
		Preconditions.checkNotNull(junctionRecord);
		TrackRecord tr = junctionRecord.getSlaveTrack();
		IVertexRecord opposite = tr.getOppositeVertex(junctionRecord);
		
		if(opposite.hasOneTrack() && !(opposite instanceof JunctionRecord)) {
			if(tr.getType() == NetworkConst.TRACK_STRAIGHT) {
				return false;
			}
			
			junctionRecord.setTangentFor(tr, Geometry.normalizeAngle(junctionRecord.tangent() + Math.PI));
			switch(tr.getType()) {
				case NetworkConst.TRACK_CURVED:
					this.api.curveFollowsPoint(tr, (VertexRecord) opposite);
					break;
				case NetworkConst.TRACK_FREE:
					this.api.calculateFreeCurve(tr);
					break;
			}
			return true;
		}
		return false;
	}
}
