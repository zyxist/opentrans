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
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Map;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.painters.ITrackPainter;
import org.invenzzia.opentrans.visitons.render.scene.AbstractTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.HoveredItemSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.IgnoreHoverSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.MouseSnapshot;

/**
 * Common code for drawing tracks. The basic algorithm is the same, but we use different strategies
 * for drawing edited and committed tracks, because they must be visually different.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TrackStream extends RenderingStreamAdapter {
	public static final Stroke DEFAULT_STROKE = new BasicStroke();
	public static final Color TRACK_COLOR = new Color(0x33, 0x33, 0xff, 0xff);
	public static final Color SELECTED_COLOR = new Color(0x00, 0xc8, 0xff, 0xf4);
	
	/**
	 * Details: how to draw edited tracks?
	 */
	private final ITrackDrawingStrategy editStrategy;
	/**
	 * Details: how to draw committed tracks?
	 */
	private final ITrackDrawingStrategy commitStrategy;
	
	private byte hoveredItem = 0;
	private long hoveredItemId = IIdentifiable.NEUTRAL_ID;
	/**
	 * Stroke is generated dynamically, depending on the zoom.
	 */
	private Stroke currentStroke;
	
	public TrackStream() {
		super();
		this.editStrategy = new EditStrategy();
		this.commitStrategy = new CommitStrategy();
	}

	@Override
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame) {
		CommittedTrackSnapshot committedTrackSnapshot = this.extract(scene, CommittedTrackSnapshot.class);
		EditableTrackSnapshot editableTrackSnapshot = this.extract(scene, EditableTrackSnapshot.class);
		CameraModelSnapshot camera = this.extract(scene, CameraModelSnapshot.class);
		IgnoreHoverSnapshot ignoreHover = this.extract(scene, IgnoreHoverSnapshot.class);
		MouseSnapshot mouse = this.extract(scene, MouseSnapshot.class);
		Rectangle mouseRect = this.getMousePosition(scene);
		if(null != committedTrackSnapshot) {
			this.drawTracksFromSnapshot(this.commitStrategy, graphics, scene, hoverCollector, prevTimeFrame,
				committedTrackSnapshot, camera, ignoreHover, mouse, mouseRect);
		}
		if(null != editableTrackSnapshot) {
			this.drawTracksFromSnapshot(this.editStrategy, graphics, scene, hoverCollector, prevTimeFrame,
				editableTrackSnapshot, camera, ignoreHover, mouse, mouseRect);
		}
	}
	
	/**
	 * Common code for drawing data from the given track snapshot, and using the specified drawing
	 * strategy.
	 * 
	 * @param strategy Drawing strategy.
	 * @param graphics Drawing canvas.
	 * @param scene
	 * @param prevTimeFrame
	 * @param trackSnapshot
	 * @param camera
	 * @param mouse
	 * @param mouseRect 
	 */
	private void drawTracksFromSnapshot(ITrackDrawingStrategy strategy, Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector,
		long prevTimeFrame, AbstractTrackSnapshot trackSnapshot, CameraModelSnapshot camera, IgnoreHoverSnapshot ignoreHover,
		MouseSnapshot mouse, Rectangle mouseRect)
	{
		if(trackSnapshot.needsRefresh()) {
			trackSnapshot.refreshTrackPainters(camera);
		}

		float height = camera.world2pix(1.5);
		this.currentStroke = new BasicStroke(height > 1.0f ? height : 1.0f);
		strategy.prepareTrackStroke(graphics);
		boolean restore = false;
		boolean found = false;
		boolean checkHover;

		for(ITrackPainter painter: trackSnapshot.getTracks()) {
			checkHover = true;
			if(null != ignoreHover) {
				checkHover = ignoreHover.getTrackId() != painter.getId();
			}
			if(!found && checkHover && painter.hits(graphics, mouseRect)) {
				strategy.prepareSelectedTrackStroke(graphics);
				restore = true;
				found = true;
				hoverCollector.registerHoveredItem(HoveredItemSnapshot.TYPE_TRACK, painter.getId());
			}
			painter.draw(camera, graphics, true);
			if(restore) {
				strategy.restoreTrackStroke(graphics);
				restore = false;
			}
		}
		strategy.prepareVertexStroke(graphics);
		double points[] = trackSnapshot.getVertices();
		long ids[] = trackSnapshot.getVertexIds();
		restore = false;
		found = false;
		if(null != points) {
			float radius = camera.world2pix(2.0);
			if(radius < 3.0f) {
				radius = 3.0f;
			}
			int radiusInt = (int)Math.ceil(radius);
			int halfRadius = (int) Math.floor(radiusInt / 2.0);
			
			for(int i = 0, j = 0; i < points.length; i += 2, j++) {
				int x = camera.world2pixX(points[i]) ;
				int y = camera.world2pixY(points[i+1]);

				if(null != mouse) {
					checkHover = true;
					if(null != ignoreHover) {
						checkHover = ignoreHover.getVertexId() != ids[j];
					}
					if(!found && checkHover && mouse.hits(x - halfRadius, y - halfRadius, radiusInt, radiusInt)) {
						strategy.prepareSelectedVertexStroke(graphics);
						restore = true;
						found = true;
						hoverCollector.registerHoveredItem(HoveredItemSnapshot.TYPE_VERTEX, ids[j]);
					}
				}
				strategy.drawVertex(graphics, x, y, radiusInt, halfRadius, prevTimeFrame);
				if(restore == true) {
					strategy.restoreVertexStroke(graphics);
				}
			}
		}
	}
	
	/**
	 * Exact painting and details are delegated to two classes implementing this
	 * strategy.
	 */
	interface ITrackDrawingStrategy {
		public void prepareTrackStroke(Graphics2D graphics);
		public void prepareSelectedTrackStroke(Graphics2D graphics);
		public void restoreTrackStroke(Graphics2D graphics);
		public void prepareVertexStroke(Graphics2D graphics);
		public void prepareSelectedVertexStroke(Graphics2D graphics);
		public void restoreVertexStroke(Graphics2D graphics);
		public void drawVertex(Graphics2D graphics, int x, int y, int radius, int halfRadius, long prevTimeFrame);
	}
	
	class EditStrategy implements ITrackDrawingStrategy {

		@Override
		public void prepareTrackStroke(Graphics2D graphics) {
			graphics.setStroke(currentStroke);
			graphics.setColor(SELECTED_COLOR);
		}
		
		@Override
		public void prepareSelectedTrackStroke(Graphics2D graphics) {
			graphics.setColor(Color.ORANGE);
		}
		
		@Override
		public void restoreTrackStroke(Graphics2D graphics) {
			graphics.setColor(SELECTED_COLOR);
		}

		@Override
		public void prepareVertexStroke(Graphics2D graphics) {
			graphics.setStroke(DEFAULT_STROKE);
			graphics.setColor(Color.RED);
		}
		
		@Override
		public void prepareSelectedVertexStroke(Graphics2D graphics) {
			graphics.setColor(Color.GREEN);
		}
		
		@Override
		public void restoreVertexStroke(Graphics2D graphics) {
			graphics.setColor(Color.RED);
		}

		@Override
		public void drawVertex(Graphics2D graphics, int x, int y, int radius, int halfRadius, long prevTimeFrame) {
			graphics.fillOval(x - halfRadius, y - halfRadius, radius, radius);
					
			int angle = (int)((prevTimeFrame / 4)) % 360;
			halfRadius += 3;
			radius += 6;
			graphics.drawArc(x-halfRadius, y-halfRadius, radius, radius, angle, 45);
			graphics.drawArc(x-halfRadius, y-halfRadius, radius, radius, (angle + 180) % 360, 45);
		}
	}
	
	class CommitStrategy implements ITrackDrawingStrategy {

		@Override
		public void prepareTrackStroke(Graphics2D graphics) {
			graphics.setStroke(currentStroke);
			graphics.setColor(TRACK_COLOR);
		}
		
		@Override
		public void prepareSelectedTrackStroke(Graphics2D graphics) {
			graphics.setColor(Color.ORANGE);
		}
		
		@Override
		public void restoreTrackStroke(Graphics2D graphics) {
			graphics.setColor(TRACK_COLOR);
		}

		@Override
		public void prepareVertexStroke(Graphics2D graphics) {
			graphics.setStroke(DEFAULT_STROKE);
			graphics.setColor(Color.RED);
		}
		
		@Override
		public void prepareSelectedVertexStroke(Graphics2D graphics) {
			graphics.setColor(Color.GREEN);
		}
		
		@Override
		public void restoreVertexStroke(Graphics2D graphics) {
			graphics.setColor(Color.RED);
		}

		@Override
		public void drawVertex(Graphics2D graphics, int x, int y, int radius, int halfRadius, long prevTimeFrame) {
			graphics.fillOval(x - halfRadius, y - halfRadius, radius, radius);
		}
	}
}