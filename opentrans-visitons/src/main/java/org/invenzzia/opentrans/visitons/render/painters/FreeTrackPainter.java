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
 * Draws a free track (two curved tracks).
 * 
 * @author Tomasz Jędrzejewski
 */
public class FreeTrackPainter implements ITrackPainter {
	private double coordinates[];
	
	public FreeTrackPainter(double metadata[]) {
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
		graphics.draw(new Arc2D.Double(
			(double) camera.world2pixX(this.coordinates[8]),
			(double) camera.world2pixY(this.coordinates[9]),
			(double) camera.world2pix(this.coordinates[10]),
			(double) camera.world2pix(this.coordinates[11]),
			this.coordinates[12],
			this.coordinates[13],
			Arc2D.OPEN
		));
		
		int x = camera.world2pixX(this.coordinates[28]);
		int y = camera.world2pixY(this.coordinates[29]);
		graphics.drawString("E", x+4, y+4);
		graphics.fillOval(x - 1, y - 1, 3, 3);
		x = camera.world2pixX(this.coordinates[30]);
		y = camera.world2pixY(this.coordinates[31]);
		graphics.drawString("F", x+4, y+4);
		graphics.fillOval(x - 1, y - 1, 3, 3);
	}
}
