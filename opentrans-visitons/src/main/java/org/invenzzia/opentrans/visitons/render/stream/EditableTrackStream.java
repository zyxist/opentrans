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
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.painters.ITrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;

/**
 * Draws the currently edited network unit of work, managed directly by GUI.
 * Because these are the tracks the user edits, we want to draw some additional
 * guidelines etc.
 * 
 * @author Tomasz Jędrzejewski
 */
public class EditableTrackStream extends RenderingStreamAdapter {
	public static final BasicStroke TRACK_STROKE = new BasicStroke();

	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, long prevTimeFrame) {
		Object payload = scene.get(EditableTrackSnapshot.class);
		if(null != payload) {
			EditableTrackSnapshot trackSnapshot = (EditableTrackSnapshot) payload;
			CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
			if(trackSnapshot.needsRefresh()) {
				trackSnapshot.refreshTrackPainters(camera);
			}
			
			graphics.setStroke(TRACK_STROKE);
			graphics.setColor(Color.BLUE);
			for(ITrackPainter painter: trackSnapshot.getTracks()) {
				painter.draw(camera, graphics, true);
			}
			graphics.setColor(Color.RED);
			double points[] = trackSnapshot.getVertices();
			if(null != points) {
				for(int i = 0; i < points.length; i += 2) {
					int x = camera.world2pixX(points[i]) ;
					int y = camera.world2pixY(points[i+1]);
					graphics.fillOval(x - 1, y - 1, 3, 3);
					
					int angle = (int)((prevTimeFrame / 4)) % 360;
					graphics.drawArc(x-4, y-4, 9, 9, angle, 45);
					graphics.drawArc(x-4, y-4, 9, 9, (angle + 180) % 360, 45);
				}
			}
		}
	}

}
