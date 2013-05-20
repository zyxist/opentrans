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

package org.invenzzia.opentrans.visitons.network.transform.modifiers;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Extracts the track from the vertex and then saves it in <tt>v2</tt> field
 * of the input.
 * 
 * @author Tomasz Jędrzejewski
 */
public class GetTrackFromVertexModifier implements IModifier {

	@Override
	public void modify(TransformInput input) {
		Preconditions.checkArgument(input.v1.hasOneTrack(), "The evaluated track must be connected to a single vertex.");
		Preconditions.checkArgument(input.t2 == null, "The second track must not be set!");
		input.t2 = input.v1.getTrack();
	}
}
