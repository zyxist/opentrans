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

package org.invenzzia.opentrans.visitons.network.transform;

import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;

/**
 * Simple record of input data passed to the condition evaluator.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TransformInput {
	public TrackRecord t1;
	public TrackRecord t2;
	public VertexRecord v1;
	public VertexRecord v2;
	public final double a1;
	public final double a2;
	public final byte mode;
	
	public TransformInput(TrackRecord t1, TrackRecord t2, VertexRecord v1, VertexRecord v2, double a1, double a2, byte mode) {
		this.t1 = t1;
		this.t2 = t2;
		this.v1 = v1;
		this.v2 = v2;
		this.a1 = a1;
		this.a2 = a2;
		this.mode = mode;
	}
	
	public TransformInput(TrackRecord t1, TrackRecord t2, VertexRecord v1, VertexRecord v2, byte mode) {
		this.t1 = t1;
		this.t2 = t2;
		this.v1 = v1;
		this.v2 = v2;
		this.a1 = Double.NaN;
		this.a2 = Double.NaN;
		this.mode = mode;
	}
	
	public TransformInput(TrackRecord t1, TrackRecord t2, VertexRecord v1, VertexRecord v2) {
		this.t1 = t1;
		this.t2 = t2;
		this.v1 = v1;
		this.v2 = v2;
		this.a1 = Double.NaN;
		this.a2 = Double.NaN;
		this.mode = 0;
	}
}
