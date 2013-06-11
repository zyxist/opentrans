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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.SelectedTrackObjectSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VehicleSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VehicleSnapshot.RenderableVehicle;

/**
 * Draws vehicles.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleStream extends RenderingStreamAdapter {
	private BasicStroke VH_STROKE = new BasicStroke();
	
	private Rectangle mouseRect;
	private HoverCollector hoverCollector;
	private SelectedTrackObjectSnapshot selected;
	
	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame) {
		VehicleSnapshot vehicles = this.extract(scene, VehicleSnapshot.class);
		if(null != vehicles) {
			CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
			MouseSnapshot mouse = this.extract(scene, MouseSnapshot.class);
			this.selected = this.extract(scene, SelectedTrackObjectSnapshot.class);
			this.mouseRect = this.getMousePosition(scene);
			this.hoverCollector = hoverCollector;
			
			BasicStroke vehicleStroke = new BasicStroke(camera.world2pix(2.4));
		
			for(RenderableVehicle rvh: vehicles.getVehicles()) {
				this.renderVehicle(graphics, rvh, camera, mouse, vehicleStroke);
			}
		}
	}

	public void renderVehicle(Graphics2D graphics, RenderableVehicle rvh, CameraModelSnapshot camera, MouseSnapshot mouse, BasicStroke stroke) {
		double knots[] = rvh.knots;
		
		
		graphics.setStroke(stroke);
		
		for(int i = 0; i < (knots.length - 3); i += 2) {
			Line2D line = new Line2D.Double(
				(double)camera.world2pixX(knots[i]),
				(double)camera.world2pixY(knots[i+1]),
				(double)camera.world2pixX(knots[i+2]),
				(double)camera.world2pixY(knots[i+3])
			);
			
			if(graphics.hit(mouseRect, line, false)) {
				graphics.setColor(Color.GREEN);
				this.hoverCollector.registerHoveredItem(HoveredItemSnapshot.TYPE_VEHICLE, rvh.id);
			} else {
				graphics.setColor(Color.RED);
			}
			graphics.draw(line);
		}
	}
}
