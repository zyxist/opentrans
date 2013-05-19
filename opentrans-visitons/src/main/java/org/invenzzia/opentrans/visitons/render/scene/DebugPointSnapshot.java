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

package org.invenzzia.opentrans.visitons.render.scene;

import com.google.common.base.Preconditions;

/**
 * Snapshot of some debugging data that may be sent to the renderer.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DebugPointSnapshot {
	private final double data[];
	
	public DebugPointSnapshot(double data[]) {
		Preconditions.checkArgument(data.length % 2 == 0, "The length of the input array must be multiplicity of 2.");
		this.data = data;
	}
	
	public double[] getData() {
		return this.data;
	}
}
