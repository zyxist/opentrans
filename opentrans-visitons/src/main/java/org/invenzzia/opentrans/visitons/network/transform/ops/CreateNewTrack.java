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
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;

/**
 * Basic operation for creating a new track record from two points.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CreateNewTrack implements IOperation {
	private ITransformAPI api;
	
	@Override
	public void setTransformAPI(ITransformAPI api) {
		this.api = Preconditions.checkNotNull(api);
	}
	
	/**
	 * Creates a new, straight track from the given points. The returned value
	 * is the vertex record #2, where we can obtain the straight track from. The method 
	 * returns NULL, if one of the points is outside the world boundaries.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Vertex record #2
	 */
	public VertexRecord create(double x1, double y1, double x2, double y2) {
		if(!this.api.getWorld().isWithinWorld(x1, y1) || !this.api.getWorld().isWithinWorld(x2, y2)) {
			return null;
		}
		
		VertexRecord v1 = new VertexRecord();
		v1.setPosition(x1, y1);
		
		VertexRecord v2 = new VertexRecord();
		v2.setPosition(x2, y2);
		
		TrackRecord tr = new TrackRecord();
		tr.setVertices(v1, v2);
		v1.addTrack(tr);
		v2.addTrack(tr);
		
		this.api.calculateStraightLine(tr);
		
		this.api.getUnitOfWork().addVertex(v1).addVertex(v2).addTrack(tr);
		return v2;
	}
}
