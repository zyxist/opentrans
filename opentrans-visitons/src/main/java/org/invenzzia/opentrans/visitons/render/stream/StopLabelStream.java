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
import org.invenzzia.opentrans.visitons.render.scene.StopSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.StopSnapshot.StopNameInfo;

/**
 * This stream displays the labels with stop names.
 * 
 * @author Tomasz Jędrzejewski
 */
public class StopLabelStream extends RenderingStreamAdapter {
	private static final Color FRAME_COLOR = new Color(0xEE, 0xEE, 0xEE, 0xCC);
	private static final Color TEXT_COLOR = new Color(0x55, 0x99, 0xFF, 0xCC);
	
	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame) {
		StopSnapshot stopSnapshot  = this.extract(scene, StopSnapshot.class);
		CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
		if(null != stopSnapshot) {
			graphics.setFont(this.fontRepository.getFont("stop-name"));
			for(StopNameInfo info: stopSnapshot.getStopNameInfo()) {
				int width = graphics.getFontMetrics().stringWidth(info.label);
				int height = graphics.getFontMetrics().getHeight();
				width += 4;
				
				int x = camera.world2pixX(info.x);
				int y = camera.world2pixY(info.y - 15.0);
				int rounding = (int)camera.world2pix(1.8);
				
				graphics.setColor(FRAME_COLOR);
				graphics.fillRect(x - width / 2, y - height / 2, width, height);
				graphics.setColor(TEXT_COLOR);
				graphics.drawString(info.label, x - width / 2 + 2, y + height / 2 - rounding);
			}
		}
	}
}
