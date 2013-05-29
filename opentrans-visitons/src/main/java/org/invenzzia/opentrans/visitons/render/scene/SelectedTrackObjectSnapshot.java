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

import org.invenzzia.opentrans.visitons.render.scene.AbstractTrackObjectSnapshot.RenderableTrackObject;

/**
 * Information about the currently selected track object.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectedTrackObjectSnapshot {
	private final int type;
	private final long id;
	private final int number;

	public SelectedTrackObjectSnapshot(int type, long id) {
		this.type = type;
		this.id = id;
		this.number = 0;
	}
	
	public SelectedTrackObjectSnapshot(int type, long id, int number) {
		this.type = type;
		this.id = id;
		this.number = number;
	}

	public int getType() {
		return this.type;
	}

	public long getId() {
		return this.id;
	}

	public int getNumber() {
		return this.number;
	}
	
	/**
	 * Returns true, if the snapshot represents the given track object.
	 * 
	 * @param snapshot
	 * @param object
	 * @return 
	 */
	public static boolean isSelected(SelectedTrackObjectSnapshot snapshot, RenderableTrackObject object) {
		if(null == snapshot) {
			return false;
		} else {
			return (snapshot.getType() == object.type && snapshot.getId() == object.id && snapshot.getNumber() == object.number);
		}
	}
}
