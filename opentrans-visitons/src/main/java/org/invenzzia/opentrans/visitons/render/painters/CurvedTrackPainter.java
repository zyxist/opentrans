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

package org.invenzzia.opentrans.visitons.render.painters;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * Draws a curved track.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CurvedTrackPainter implements ITrackPainter {
	private double coordinates[];
	
	public CurvedTrackPainter(double metadata[]) {
		this.coordinates = metadata;
	}

	@Override
	public void draw(CameraModelSnapshot camera, Graphics2D graphics, boolean editable) {
		graphics.draw(new Arc2D.Double(
			(double) camera.world2pixX(this.coordinates[0]),
			(double) camera.world2pixY(this.coordinates[1]),
			(double) camera.world2pix(this.coordinates[2]),
			(double) camera.world2pix(this.coordinates[3]),
			this.coordinates[4],
			this.coordinates[5],
			Arc2D.OPEN
		));
	}
}
