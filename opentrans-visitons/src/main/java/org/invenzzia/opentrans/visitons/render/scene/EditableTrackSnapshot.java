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

import org.invenzzia.opentrans.visitons.render.painters.ITrackPainter;

/**
 * Contains the information about the currently edited network unit of work,
 * that is manipulated directly by GUI code.
 * 
 * @author Tomasz Jędrzejewski
 */
public class EditableTrackSnapshot {
	private ITrackPainter tracks[];
	private double[] vertices;

	public EditableTrackSnapshot(int trackNum) {
		this.tracks = new ITrackPainter[trackNum];
	}
	
	public void setTrackPainter(int idx, ITrackPainter ptr) {
		this.tracks[idx] = ptr;
	}
	
	public void setVertexArray(double array[]) {
		this.vertices = array;
	}
	
	public ITrackPainter[] getTracks() {
		return this.tracks;
	}

	public double[] getVertices() {
		return this.vertices;
	}
}
