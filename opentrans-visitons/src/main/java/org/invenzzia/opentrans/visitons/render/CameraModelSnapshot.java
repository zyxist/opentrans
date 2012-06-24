/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.render;

/**
 * A snapshot of the data from the camera model. It can be used for
 * asynchronous, non-blocking camera model updates. Modifications
 * of the snapshot are done in a block.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CameraModelSnapshot extends AbstractCameraModelFoundation {
	
	public CameraModelSnapshot(AbstractCameraModelFoundation source) {
		this.copyFrom(source);
	}
	
	/**
	 * Copies the data from a camera model or another snapshot into
	 * this snapshot.
	 * 
	 * @param source Data source.
	 */
	public final void copyFrom(AbstractCameraModelFoundation source) {
		this.posX = source.posX;
		this.posY = source.posY;
		this.viewportHeight = source.viewportHeight;
		this.viewportWidth = source.viewportWidth;
		this.viewportHeightPx = source.viewportHeightPx;
		this.viewportWidthPx = source.viewportWidthPx;
		this.mpp = source.mpp;
		this.overflowCenterX = source.overflowCenterX;
		this.overflowCenterY = source.overflowCenterY;
		this.vertOverflow = source.vertOverflow;
		this.horizOverflow = source.horizOverflow;
	}
}
