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

import java.awt.Graphics2D;
import java.util.Map;
import org.invenzzia.opentrans.visitons.render.AbstractCameraModelFoundation;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.RenderingStreamAdapter;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot.SegmentInfo;

/**
 * Draws the bitmaps associated to each segment, i.e. prerendered maps of
 * some area.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SegmentBitmapStream extends RenderingStreamAdapter {
	private static final double MAX_ZOOM_VISIBILITY = 3.0;
	
	@Override
	public void render(Graphics2D graphics, Map<Object, Object> snapshot, long prevTimeFrame) {
		VisibleSegmentSnapshot vss = this.extract(snapshot, VisibleSegmentSnapshot.class);
		CameraModelSnapshot camera = this.extract(snapshot, CameraModelSnapshot.class);
		
		for(SegmentInfo segment: vss.getSegments()) {
			if(null != segment.image) {
				int x = (int) camera.world2pixX(segment.x * CameraModel.SEGMENT_SIZE);
				int y = (int) camera.world2pixY(segment.y * CameraModel.SEGMENT_SIZE);
				
				if(camera.getMpp() < MAX_ZOOM_VISIBILITY) {
					int newSize = (int) Math.round((CameraModel.DEFAULT_ZOOM / camera.getMpp() * CameraModel.SEGMENT_SIZE));
					graphics.drawImage(segment.image, x, y, newSize, newSize, null);
				}
			}
		}	
	}
} 