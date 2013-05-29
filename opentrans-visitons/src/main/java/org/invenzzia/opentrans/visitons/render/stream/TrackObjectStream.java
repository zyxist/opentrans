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
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.AbstractTrackObjectSnapshot.RenderableTrackObject;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackObjectSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackObjectSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.SelectedTrackObjectSnapshot;

/**
 * Paints all types of track objects.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackObjectStream extends RenderingStreamAdapter {
	private static final Color PLATFORM_NAME_COLOR = new Color(0x33, 0x33, 0x33, 0xFF);
	
	private static final Color PLATFORM_COLOR = new Color(0xAA, 0xAA, 0xAA, 0xFF);
	private static final Color PLATFORM_EDITABLE_COLOR = new Color(0xAA, 0xAA, 0xAA, 0xAA);
	private static final Color PLATFORM_HOVER_COLOR = new Color(0xAA, 0xAA, 0x00, 0xCC);
	
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke();

	private Rectangle mouseRect;
	private HoverCollector hoverCollector;
	private SelectedTrackObjectSnapshot selected;
	
	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame) {
		CommittedTrackObjectSnapshot committedTrackSnapshot = this.extract(scene, CommittedTrackObjectSnapshot.class);
		EditableTrackObjectSnapshot editableTrackSnapshot = this.extract(scene, EditableTrackObjectSnapshot.class);
		CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
		MouseSnapshot mouse = this.extract(scene, MouseSnapshot.class);
		this.selected = this.extract(scene, SelectedTrackObjectSnapshot.class);
		this.mouseRect = this.getMousePosition(scene);
		this.hoverCollector = hoverCollector;
		if(null != committedTrackSnapshot) {
			this.drawObjects(graphics, camera, committedTrackSnapshot.getTrackObjects(), false, prevTimeFrame);
		}
		if(null != editableTrackSnapshot) {
			this.drawObjects(graphics, camera, editableTrackSnapshot.getTrackObjects(), true, prevTimeFrame);
		}
	}
	
	private void drawObjects(Graphics2D graphics, CameraModelSnapshot camera, List<RenderableTrackObject> objects, boolean transparency, long prevTimeFrame) {
		for(RenderableTrackObject object: objects) {
			switch(object.type) {
				case NetworkConst.TRACK_OBJECT_PLATFORM:
					this.drawPlatform(graphics, camera, object, transparency, prevTimeFrame);
					break;
			}
		}
	}

	
	private void drawPlatform(Graphics2D graphics, CameraModelSnapshot camera, RenderableTrackObject object, boolean transparency, long prevTimeFrame) {
		double dist = camera.world2pix(2.5);
		
		int x = camera.world2pixX(object.x);
		int y = camera.world2pixY(object.y);
		
		graphics.rotate(object.tangent, x, y);
		graphics.translate(0, -dist);
		
		int width = (int) camera.world2pix(30.0);
		int height = (int) camera.world2pix(3.0);

		Rectangle2D r2d = new Rectangle2D.Float((float) x - width / 2, (float) y - height / 2, (float) width, (float)height);
		
		if(graphics.hit(this.mouseRect, r2d, false)) {
			graphics.setColor(PLATFORM_HOVER_COLOR);
			this.hoverCollector.registerHoveredItem(object.type + HoveredItemSnapshot.TYPE_TRACK_OBJECT_DELTA, object.id, object.number);
		} else if(transparency) {
			graphics.setColor(PLATFORM_EDITABLE_COLOR);
		} else {
			graphics.setColor(PLATFORM_COLOR);
		}
		graphics.fill(r2d);
		
		if(SelectedTrackObjectSnapshot.isSelected(this.selected, object)) {
			float phase = (float)((prevTimeFrame / 32.0) % 10.0);
			graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.5f, new float[]{5.0f, 5.0f}, phase));
			graphics.setColor(Color.RED);
			graphics.draw(r2d);
			graphics.setStroke(DEFAULT_STROKE);
		}
		
		if(camera.getMpp() < 0.50) {
			graphics.setColor(PLATFORM_NAME_COLOR);
			graphics.setFont(this.fontRepository.getFont("platform-name"));
			int textWidth = graphics.getFontMetrics().stringWidth(object.name);
			int textHeight = graphics.getFontMetrics().getHeight();
			graphics.drawString(object.name, x - textWidth / 2, y + textHeight / 4);
		}
		
		graphics.translate(0, dist);
		graphics.rotate(-object.tangent, x, y);
	}
}
