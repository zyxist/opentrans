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
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.painters.ITrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackSnapshot;

/**
 * Draws a piece of the simulation world. The class is pretty similar to
 * {@link EditableTrackStream}, but handles the vertices and tracks that
 * are already added to the world, not the ones being drawn. 
 * 
 * @author Tomasz Jędrzejewski
 */
public class CommittedTrackStream extends RenderingStreamAdapter {
	public static final BasicStroke TRACK_STROKE = new BasicStroke();
	
	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, long prevTimeFrame) {
		CommittedTrackSnapshot trackSnapshot = this.extract(scene, CommittedTrackSnapshot.class);
		if(null != trackSnapshot) {
			CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
			Rectangle mouse = this.getMousePosition(scene);
			if(trackSnapshot.needsRefresh()) {
				trackSnapshot.refreshTrackPainters(camera);
			}
			
			graphics.setStroke(TRACK_STROKE);
			graphics.setColor(Color.BLACK);
			boolean restore = false;
			for(ITrackPainter painter: trackSnapshot.getTracks()) {
				if(painter.hits(graphics, mouse)) {
					graphics.setColor(Color.ORANGE);
					restore = true;
				}
				painter.draw(camera, graphics, true);
				if(restore) {
					graphics.setColor(Color.BLACK);
					restore = false;
				}
			}
			graphics.setColor(Color.RED);
			double points[] = trackSnapshot.getVertices();
			if(null != points) {
				for(int i = 0; i < points.length; i += 2) {
					int x = camera.world2pixX(points[i]) ;
					int y = camera.world2pixY(points[i+1]);
					graphics.fillOval(x - 1, y - 1, 3, 3);
				}
			}
		}
	}
}
