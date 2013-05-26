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
 * The most basic operation of creating the new track, starting from the given point.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ExtendTrack extends AbstractOperation {
	private VertexRecord createdVertex;

	public VertexRecord extend(VertexRecord vr, double x, double y, byte mode) {
		this.createdVertex = null;
		
		if(!this.getAPI().getWorld().isWithinWorld(x, y)) {
			return null;
		}
		
		this.evaluateCases(new TransformInput(null, null, vr, null, x, y, mode));
		return this.createdVertex;
	}
	
	@Override
	protected void configure() {
		this.initialModifier(extractTrack());
		this.register(and(track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnCurvedTrack());
		this.register(and(track(withType(NetworkConst.TRACK_STRAIGHT)), withMode(NetworkConst.MODE_ALT1)), this.spawnFreeTrack());
		this.register(and(track(withType(NetworkConst.TRACK_CURVED)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnStraightTrack());
		this.register(and(track(withType(NetworkConst.TRACK_CURVED)), withMode(NetworkConst.MODE_ALT1)), this.spawnFreeTrack());
		this.register(and(track(withType(NetworkConst.TRACK_FREE)), withMode(NetworkConst.MODE_DEFAULT)), this.spawnStraightTrack());
		this.register(and(track(withType(NetworkConst.TRACK_FREE)), withMode(NetworkConst.MODE_ALT1)), this.spawnCurvedTrack());
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importAllMissingNeighbors(api.getUnitOfWork(), input.v1);
	}

	private IOperationCase spawnCurvedTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				createdVertex = buildVertex(api, input);
				TrackRecord newTrack = buildTrack(api, input, createdVertex);
				newTrack.setType(NetworkConst.TRACK_CURVED);
				api.curveFollowsPoint(newTrack, createdVertex);
			}
		};
	}

	private IOperationCase spawnStraightTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				createdVertex = buildVertex(api, input);
				TrackRecord newTrack = buildTrack(api, input, createdVertex);
				newTrack.setType(NetworkConst.TRACK_STRAIGHT);
				
				if(input.v1.hasAllTracks()) {
					double buf[] = new double[8];
					LineOps.toGeneral(input.v1.x(), input.v1.y(), input.v1.tangent(), 0, buf);
					LineOps.toOrthogonal(0, 3, buf, input.a1, input.a2);
					LineOps.intersection(0, 3, 6, buf);
					createdVertex.setPosition(buf[6], buf[7]);
				}
				api.calculateStraightLine(newTrack);
			}
		};
	}

	private IOperationCase spawnFreeTrack() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				createdVertex = buildVertex(api, input);
				TrackRecord newTrack = buildTrack(api, input, createdVertex);
				newTrack.setType(NetworkConst.TRACK_FREE);
				api.calculateFreeCurve(newTrack);
			}
		};
	}
	
	private VertexRecord buildVertex(ITransformAPI api, TransformInput input) {
		VertexRecord vr = new VertexRecord();
		vr.setPosition(input.a1, input.a2);
		vr.setFirstTangent(input.v1.tangent());
		
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
