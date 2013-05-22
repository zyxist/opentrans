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
import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;
import static org.invenzzia.opentrans.visitons.network.transform.conditions.Conditions.*;

/**
 * Simple operation that allows converting any curved or straight track into
 * a free track with applying appropriate transformations.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConvertToFreeTrack extends AbstractOperation {
	
	public boolean convert(TrackRecord track) {
		return this.evaluateCases(new TransformInput(track, null, null, null));
	}

	@Override
	protected void configure() {
		this.register(track(withType(NetworkConst.TRACK_STRAIGHT)), this.covertStraightToFree());
		this.register(track(withType(NetworkConst.TRACK_CURVED)), this.convertCurvedToFree());
	}
	
	@Override
	protected void importData(TransformInput input, ITransformAPI api) {
		api.getRecordImporter().importAllMissingNeighbors(api.getUnitOfWork(), input.t1.getFirstVertex(), input.t1.getSecondVertex());
	}

	private IOperationCase covertStraightToFree() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				// TODO: See if we don't want more funny transformations here...
				// if not, refactor this class to use {@link IOperation}
				input.t1.setType(NetworkConst.TRACK_FREE);
				api.calculateFreeCurve(input.t1);
			}
		};
	}

	private IOperationCase convertCurvedToFree() {
		return new IOperationCase() {
			@Override
			public void execute(TransformInput input, ITransformAPI api) {
				input.t1.setType(NetworkConst.TRACK_FREE);
				api.calculateFreeCurve(input.t1);
			}
		};
	}
}
