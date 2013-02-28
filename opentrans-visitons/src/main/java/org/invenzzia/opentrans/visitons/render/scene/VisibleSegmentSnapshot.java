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
package org.invenzzia.opentrans.visitons.render.scene;

import com.google.common.base.Preconditions;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;
import org.invenzzia.opentrans.visitons.network.Segment;

/**
 * Keeps information about visible segments in the scene manager.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisibleSegmentSnapshot {
	private final List<SegmentInfo> segments;
	
	public VisibleSegmentSnapshot() {
		this.segments = new LinkedList<>();
	}
	
	/**
	 * Adds a new information about a visible segment to the snapshot used by the renderer.
	 * 
	 * @param segmentInfo 
	 */
	public void addSegmentInfo(SegmentInfo segmentInfo) {
		this.segments.add(Preconditions.checkNotNull(segmentInfo));
	}
	
	/**
	 * Returns the list of visible segments. The list shall not be modified during the rendering.
	 * 
	 * @return List of information about visible segments.
	 */
	public List<SegmentInfo> getSegments() {
		return this.segments;
	}
	
	
	/**
	 * Snapshot of a segment information.
	 */
	public static class SegmentInfo {
		public final Image image;
		public final int x;
		public final int y;
		public final boolean isUsed;
		
		public SegmentInfo(Segment segment, Image bitmap) {
			this.x = segment.getX();
			this.y = segment.getY();
			this.isUsed = segment.isUsed();
			this.image = bitmap;
		}
	}
}
