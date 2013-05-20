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
 * The most basic operation of creating the new track, starting from the given point.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ExtendTrack extends AbstractOperation {
	private TrackRecord createdTrack;

	public TrackRecord extend(VertexRecord vr, double x, double y, byte mode) {
		this.createdTrack = null;
		this.evaluateCases(new TransformInput(vr.getTrack(), null, vr, null, x, y, mode));
		return this.createdTrack;
	}
	
	@Override
	protected void configure() {
		this.register(and(track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnCurvedTrack());
		this.register(and(track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_ALT1)), this.spawnFreeTrack());
		this.register(and(track(withType(NetworkConst.TRACK_CURVED)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnStraightTrack());
		this.register(and(track(withType(NetworkConst.TRACK_CURVED)), withMode(NetworkConst.MODE_ALT1)), this.spawnFreeTrack());
		this.register(and(track(withType(NetworkConst.TRACK_FREE)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnStraightTrack());
		this.register(and(track(withType(NetworkConst.TRACK_FREE)), withMode(NetworkConst.MODE_ALT1)), this.spawnCurvedTrack());
	}

	private IOperationCase spawnCurvedTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord newVertex = buildVertex(api, input);
				createdTrack = buildTrack(api, input, newVertex);
				createdTrack.setType(NetworkConst.TRACK_CURVED);
				api.curveFollowsPoint(createdTrack, newVertex);
			}
		};
	}

	private IOperationCase spawnStraightTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord newVertex = buildVertex(api, input);
				createdTrack = buildTrack(api, input, newVertex);
				createdTrack.setType(NetworkConst.TRACK_STRAIGHT);
				api.calculateStraightLine(createdTrack);
			}
		};
	}

	private IOperationCase spawnFreeTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				VertexRecord newVertex = buildVertex(api, input);
				createdTrack = buildTrack(api, input, newVertex);
				createdTrack.setType(NetworkConst.TRACK_FREE);
				api.calculateFreeCurve(createdTrack, newVertex, createdTrack.getOppositeVertex(newVertex));
			}
		};
	}
	
	private VertexRecord buildVertex(ITransformAPI api, TransformInput input) {
		VertexRecord vr = new VertexRecord();
		vr.setPosition(input.a1, input.a2);
		vr.setTangent(input.v1.tangent());
		
		api.getUnitOfWork().addVertex(vr);
		return vr;
	}
	
	private TrackRecord buildTrack(ITransformAPI api, TransformInput input, VertexRecord newVertex) {
		TrackRecord tr = new TrackRecord();
		tr.setVertices(newVertex, input.v1);
		newVertex.addTrack(tr);
		input.v1.addTrack(tr);
		
		api.getUnitOfWork().addTrack(tr);
		
		return tr;
	}
}
