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

import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.helium.data.interfaces.ILightMemento;

/**
 * Records the initial state of the tracks and vertices, so that
 * we could restore some state, if we notice that it is impossible
 * to complete such an operation.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TransformReverter {
	private Map<ILightMemento, Object> mementos;
	
	public void remember(ILightMemento object) {
		if(null == this.mementos) {
			this.mementos = new LinkedHashMap<>();
		}
		this.mementos.put(object, object.getMemento());
	}
	
	public void restore() {
		if(null != this.mementos) {
			for(Map.Entry<ILightMemento, Object> entry: this.mementos.entrySet()) {
				entry.getKey().restoreMemento(entry.getValue());
			}
		}
	}
}
