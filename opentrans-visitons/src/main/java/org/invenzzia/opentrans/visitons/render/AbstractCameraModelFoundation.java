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

import com.vividsolutions.jts.geom.Coordinate;
import org.invenzzia.opentrans.visitons.utils.SegmentCoordinate;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * Common parts of {@link CameraModel} and {@link CameraModelSnapshot}:
 * data definition, getter methods.
 * 
 * @author Tomasz Jędrzejewski
 */
abstract public class AbstractCameraModelFoundation {
	public static final double MIN_VIEWPORT = 1.0;
	public static final double SEGMENT_SIZE = 1000.0;
	public static final double DEFAULT_ZOOM = 1.0;

	/**
	 * Top-left corner viewport position in the world map units (metres).
	 */
	protected double posX;
	protected double posY;
	
	/**
	 * The width and height of the viewport in the world map units (metres).
	 */
	protected double viewportWidth;
	protected double viewportHeight;
	/**
	 * The width and height of the viewport in pixels.
	 */
	protected int viewportWidthPx;
	protected int viewportHeightPx;
	/**
	 * Overflow means that the viewport covers some area outside the world map.
	 */
	protected boolean horizOverflow;
	protected boolean vertOverflow;
	/**
	 * Where to center the view port on the given axis if the overflow occurs?
	 */
	protected double overflowCenterX;
	protected double overflowCenterY;
	/**
	 * Metres per pixel (zoom unit).
	 */
	protected double mpp = 1.0;
	
	/**
	 * @return Zoom level described as metres-per-pixel.
	 */
	public double getMpp() {
		return mpp;
	}
	
	public double getPosX() {
		return this.posX;
	}

	public double getPosY() {
		return this.posY;
	}

	public double getViewportHeight() {
		return this.viewportHeight;
	}
	
	public int getViewportHeightPx() {
		return this.viewportHeightPx;
	}
	
	public double getViewportWidth() {
		return this.viewportWidth;
	}
	
	public int getViewportWidthPx() {
		return this.viewportWidthPx;
	}
	/**
	 * Converts units from pixels to world meters on the X axis. Returns the
	 * absolute value.
	 * 
	 * @param coord X coordinate in pixels.
	 * @return X coordinate in metres.
	 */
	public double pix2worldX(long coord) {
		return this.posX + (coord - this.overflowCenterX) * this.mpp;
	}

	/**
	 * Converts units from pixels to world meters on the Y axis. Returns the
	 * absolute value.
	 * 
	 * @param coord Y coordinate in pixels.
	 * @return Y coordinate in metres.
	 */
	public double pix2worldY(long coord) {
		return this.posY + (coord - this.overflowCenterY) * this.mpp;
	}

	/**
	 * Converts units from world metres to pixels on X axis.
	 * 
	 * @param coord X coordinate in metres (absolute value).
	 * @return X coordinate in pixels.
	 */
	public int world2pixX(double coord) {
		return (int) Math.round((coord - this.posX) / this.mpp + this.overflowCenterX);
	}
	
	/**
	 * Converts units from world metres to pixels on Y axis.
	 * 
	 * @param coord Y coordinate in metres (absolute value).
	 * @return Y coordinate in pixels.
	 */
	public int world2pixY(double coord) {
		return (int) Math.round((coord - this.posY) / this.mpp + this.overflowCenterY);
	}

	/**
	 * Converts units from world metres to pixels.
	 * 
	 * @param p Coordinates in world metres (absolute value).
	 * @return Coordinates in pixels.
	 */
	public Coordinate world2pix(Coordinate p) {
		return new Coordinate(this.world2pixX(p.x), world2pixY(p.y));
	}

	/**
	 * Converts length in metres to pixels.
	 * 
	 * @param length Length in meters.
	 * @return Length in pixels.
	 */
	public long world2pix(double length) {
		return Math.round(length / this.mpp);
	}
	
	/**
	 * Calculates segment coordinate object from the viewport coordinates.
	 * 
	 * @param world Segment repository.
	 * @param x Viewport X coordinate.
	 * @param y Viewport Y coordinate.
	 * @return Segment under the given coordinates and X,Y coordinates within this segment in world metres.
	 */
	public SegmentCoordinate constructSegmentCoordinate(World world, long x, long y) {
		double wx = this.pix2worldX(x);
		double wy = this.pix2worldY(y);
		
		if(wx < 0 || wy < 0 || wx >= world.getX() * SEGMENT_SIZE || wy >= world.getY() * SEGMENT_SIZE) {
			return null;
		}
		
		int sx = (int)Math.floor(wx / SEGMENT_SIZE);
		int sy = (int)Math.floor(wy / SEGMENT_SIZE);
		
		Segment s = world.findSegment(sx, sy);
		if(null != s) {
			return new SegmentCoordinate(s, wx % SEGMENT_SIZE, wy % SEGMENT_SIZE);
		}
		return null;
	}
}
