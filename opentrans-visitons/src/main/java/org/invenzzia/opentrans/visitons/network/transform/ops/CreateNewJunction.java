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
import org.invenzzia.opentrans.visitons.geometry.Characteristics;
import org.invenzzia.opentrans.visitons.network.JunctionRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;

/**
 * Operation for making junctions.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CreateNewJunction implements IOperation {
	private ITransformAPI api;
	
	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = Preconditions.checkNotNull(api);
	}
	
	public VertexRecord create(TrackRecord masterTrack, double position, double x2, double y2) {
		Characteristics c = masterTrack.getPointCharacteristics(position);
		if(!this.api.getWorld().isWithinWorld(c.x(), c.y()) || !this.api.getWorld().isWithinWorld(x2, y2)) {
			return null;
		}
		JunctionRecord jr = new JunctionRecord(masterTrack);
		jr.setPosition(position);
		
		VertexRecord v2 = new VertexRecord();
		v2.setPosition(x2, y2);
		
		TrackRecord tr = new TrackRecord();
		tr.setType(NetworkConst.TRACK_CURVED);
		tr.setVertices(jr, v2);
		jr.setSlaveTrack(tr);
		v2.addTrack(tr);
		
		masterTrack.addJunction(jr);
		
		this.api.curveFollowsPoint(tr, v2);
		this.api.getUnitOfWork().addTrack(tr).addVertex(v2).addVertex(jr);
		return v2;
	}
}
