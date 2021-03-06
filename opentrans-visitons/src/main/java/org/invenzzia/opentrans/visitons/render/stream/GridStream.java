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
package org.invenzzia.opentrans.visitons.render.stream;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot.SegmentInfo;

/**
 * Draws the navigation grid on the map.
 * 
 * @author Tomasz Jędrzejewski
 */
public class GridStream extends RenderingStreamAdapter {
	public static final Color GRID_COLOR = Color.MAGENTA;
	public static final int MIN_SCREEN_WIDTH_FOR_SUBGRID = 60;
	public static final BasicStroke SEGMENT_STROKE = new BasicStroke();
	public static final BasicStroke SUBGRID_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] { 2.0f }, 0.0f);
	
	@Override
	public void render(Graphics2D g, Map<Object, Object> snapshot, HoverCollector hoverCollector, long prevTimeFrame) {
		g.setColor(GridStream.GRID_COLOR);
		VisibleSegmentSnapshot vss = this.extract(snapshot, VisibleSegmentSnapshot.class);
		CameraModelSnapshot camera = this.extract(snapshot, CameraModelSnapshot.class);
			
		if(null != vss && null != camera) {
			double mpp = camera.getMpp();
			for(SegmentInfo s: vss.getSegments()) {
				int x = (int) camera.world2pixX(s.x * CameraModelSnapshot.SEGMENT_SIZE);
				int y = (int) camera.world2pixY(s.y * CameraModelSnapshot.SEGMENT_SIZE);

				int width = (int) (CameraModelSnapshot.SEGMENT_SIZE / mpp);
				int height = (int) (CameraModelSnapshot.SEGMENT_SIZE / mpp);

				g.setStroke(GridStream.SEGMENT_STROKE);
				g.drawRect(x, y, width, height);

				if(width >= GridStream.MIN_SCREEN_WIDTH_FOR_SUBGRID) {
					g.setStroke(GridStream.SUBGRID_STROKE);
					g.drawLine(x + width / 2, y, x + width / 2, y + height);
					g.drawLine(x, y + height / 2, x + width, y + height / 2);
				}
			}
		}
	}
}
