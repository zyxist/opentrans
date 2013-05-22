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
import org.invenzzia.opentrans.visitons.render.scene.SelectionSnapshot;

/**
 * The stream for rendering the selection area.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionStream extends RenderingStreamAdapter {
	private static final Color BORDER_COLOR = new Color(0x37, 0x71, 0xc8, 0x88);
	private static final Color INTERIOR_COLOR = new Color(0x55, 0x99, 0xff, 0x88);

	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame) {
		SelectionSnapshot snapshot = this.extract(scene, SelectionSnapshot.class);
		if(null != snapshot) {
			CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
			
			int x1 = camera.world2pixX(snapshot.x1);
			int y1 = camera.world2pixY(snapshot.y1);
			int x2 = camera.world2pixX(snapshot.x2);
			int y2 = camera.world2pixY(snapshot.y2);
			if(x1 > x2) {
				int tmp = x1;
				x1 = x2;
				x2 = tmp;
			}
			if(y1 > y2) {
				int tmp = y1;
				y1 = y2;
				y2 = tmp;
			}
			
			graphics.setColor(INTERIOR_COLOR);
			graphics.fillRect(x1, y1, x2 - x1, y2 - y1);
			graphics.setColor(BORDER_COLOR);
			graphics.drawRect(x1, y1, x2 - x1, y2 - y1);
		}
	}
}
