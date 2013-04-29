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

import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
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
	private boolean refresh;

	public EditableTrackSnapshot(int trackNum) {
		this.tracks = new ITrackPainter[trackNum];
		this.refresh = true;
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
	
	/**
	 * Marks that the painters need to refresh their data.
	 */
	public void markToRefresh() {
		this.refresh = true;
	}
	
	/**
	 * Returns <strong>true</strong>, if the track painters need to refresh
	 * their data before starting the rendering due to the change in the camera
	 * model.
	 * 
	 * @return 
	 */
	public boolean needsRefresh() {
		return this.refresh;
	}
	
	/**
	 * Refreshes the painter data.
	 * 
	 * @param camera 
	 */
	public void refreshTrackPainters(CameraModelSnapshot camera) {
		for(ITrackPainter painter: this.tracks) {
			painter.refreshData(camera);
		}
		this.refresh = false;
	}
}
