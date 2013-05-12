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
package org.invenzzia.opentrans.visitons.network;

import net.jcip.annotations.Immutable;

/**
 * Snapshot of the basic data about the world, so that the GUI thread
 * could perform certain types of checks without accessing the actual
 * model.
 * 
 * @author Tomasz Jędrzejewski
 */
@Immutable
public class WorldRecord {
	private final int dimX;
	private final int dimY;
	private final double worldSizeX;
	private final double worldSizeY;
	private final boolean[][] segmentUsage;
	
	public WorldRecord(World world) {
		this.dimX = world.getX();
		this.dimY = world.getY();
		this.worldSizeX = this.dimX * Segment.SIZE;
		this.worldSizeY = this.dimY * Segment.SIZE;
		this.segmentUsage = world.exportSegmentUsage();
	}
	
	public int getX() {
		return this.dimX;
	}
	
	public int getY() {
		return this.dimY;
	}
	
	public double getRealSizeX() {
		return this.worldSizeX;
	}
	
	public double getRealSizeY() {
		return this.worldSizeY;
	}
	
	/**
	 * Returns basic information about the segment usage in the world. Each cell represents a single
	 * segment - if it is <strong>true</strong>, there are some vertices in this segment.
	 * 
	 * @return Segment usage information.
	 */
	public boolean[][] getSegmentUsage() {
		return this.segmentUsage;
	}
	
	/**
	 * When doing calculations, we must check whether we do not exceed the world boundaries, because
	 * this would break the world model during the synchronization.
	 * 
	 * @param x
	 * @param y
	 * @return True, if the point is within the boundaries.
	 */
	public boolean isWithinWorld(double x, double y) {
		return (x >= 0.0 && y >= 0.0 && x < this.worldSizeX && y < this.worldSizeY);
	}	
}
