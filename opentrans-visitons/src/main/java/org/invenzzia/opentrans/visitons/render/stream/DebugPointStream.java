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

package org.invenzzia.opentrans.visitons.render.stream;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.DebugPointSnapshot;

/**
 * Additional rendering stream used for debugging purposes.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DebugPointStream extends RenderingStreamAdapter {

	@Override
	public void render(Graphics2D graphics, Map<Object, Object> snapshot, HoverCollector hoverCollector, long prevTimeFrame) {
		CameraModelSnapshot camera = this.extract(snapshot, CameraModelSnapshot.class);
		DebugPointSnapshot debug = this.extract(snapshot, DebugPointSnapshot.class);
		
		if(null != debug && null != camera) {
			graphics.setColor(Color.DARK_GRAY);
			double points[] = debug.getData();
			for(int i = 0; i < points.length; i += 2) {
				int x = camera.world2pixX(points[i]) ;
				int y = camera.world2pixY(points[i+1]);
				
				graphics.fillOval(x - 1, y - 1, 3, 3);
				graphics.drawString("["+(i / 2)+": "+Math.round(points[i])+", "+Math.round(points[i+1])+"]", x + 6, y - 6);
			}
		}
	}
	

}
