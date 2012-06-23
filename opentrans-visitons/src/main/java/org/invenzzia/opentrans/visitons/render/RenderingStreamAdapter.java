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
package org.invenzzia.opentrans.visitons.render;

import java.util.List;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * Abstract class that provides default implementations of the setters
 * from the {@link IRenderingStream} interface. You must still write
 * the actual renderer.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class RenderingStreamAdapter implements IRenderingStream {
	protected World world;
	protected List<Segment> visibleSegments;
	
	@Override
	public void setVisibleSegmentList(List<Segment> segments) {
		this.visibleSegments = segments;
	}
	
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
}
