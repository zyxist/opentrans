/*
 * Visitons - transportation network simulation and visualization library.
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
package org.invenzzia.opentrans.visitons.utils;

import org.invenzzia.opentrans.visitons.network.Segment;

/**
 * Coordinates within a segment.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class SegmentCoordinate {
	private final Segment segment;
	private final double x;
	private final double y;

	public SegmentCoordinate(Segment s, double x, double y) {
		this.segment = s;
		this.x = x;
		this.y = y;
	}
	
	public Segment getSegment() {
		return this.segment;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	public double getAbsoluteX() {
		return this.segment.getX() * Segment.SIZE + this.x;
	}
	
	public double getAbsoluteY() {
		return this.segment.getY() * Segment.SIZE + this.y;
	}
}
