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

import java.awt.Graphics2D;
import java.util.Map;

/**
 * Rendering stream is a single phase of rendering that paints a certain
 * aspect of the world or the graphical interface. The <code>setXXX()</code>
 * methods are called every frame just before the rendering. Use the
 * {@link RenderingStreamAdapter} class for the default implementations
 * of them that will make you free of the interface changes.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public interface IRenderingStream {
	/**
	 * Renders a single frame of the animation using the given graphics device and viewport
	 * settings. For animations, the actual time of rendering the previous frame is given.
	 * The method is called sequentially for all the registered streams, and all of them
	 * render to the same graphics device. Thus, the method shall expect, that something has
	 * already painted with this device.
	 * 
	 * @param graphics Graphics device.
	 * @param scene Description of the stuff in the scene.
	 * @param hoverCollector Emits information about the hovered objects.
	 * @param prevTimeFrame The time of rendering the previous frame in milliseconds.
	 */
	public void render(Graphics2D graphics, Map<Object, Object> scene, HoverCollector hoverCollector, long prevTimeFrame);
} 