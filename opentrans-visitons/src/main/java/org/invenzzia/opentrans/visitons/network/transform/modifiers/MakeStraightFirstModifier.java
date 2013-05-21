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

import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.transform.TransformInput;

/**
 * Ensures that the straight track is always on <tt>v1</tt>
 * 
 * @author Tomasz Jędrzejewski
 */
public class MakeStraightFirstModifier implements IModifier {

	@Override
	public void modify(TransformInput input) {
		if(null != input.t1 && null != input.t2) {
			if(input.t1.getType() != NetworkConst.TRACK_STRAIGHT) {
				TrackRecord tr = input.t2;
				input.t2 = input.t1;
				input.t1 = tr;
			}
		}
	}

}
