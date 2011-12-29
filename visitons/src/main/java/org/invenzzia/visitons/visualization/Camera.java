/*
 * Visitons - transportation network simulation and visualization library.
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.invenzzia.visitons.visualization;

import java.awt.Canvas;
import java.awt.Dimension;

/**
 * The camera objects are responsible for rendering the simulation
 * and managing the properties of the area that is visible to the
 * user.
 *
 * @author zyxist
 */
public class Camera extends Canvas
{
	public static final double DEFAULT_MPP = 1.0;
	private static final long serialVersionUID = 1L;
	
	/**
	 * A reference to the rendered world object.
	 */
	protected World world;
	/**
	 * A reference for the painting strategy.
	 */
	protected IPainter painterInterface;
	/**
	 * Camera top-left corner position in metres.
	 */
	protected double posX;
	/**
	 * Camera top-left corner position in metres.
	 */
	protected double posY;
	/**
	 * Camera width in metres.
	 */
	protected double camWidth;
	/**
	 * Camera height in metres.
	 */
	protected double camHeight;
	/**
	 * Metres per pixel.
	 */
	protected double mpp = Camera.DEFAULT_MPP;
	/**
	 * Does the horizontal overflow occur?
	 */
	protected boolean hOverflow;
	/**
	 * Does the vertical overflow occur?
	 */
	protected boolean vOverflow;
	/**
	 * Overflow centering on X axis
	 */
	protected double centX;
	/**
	 * Overflow centering on Y axis
	 */
	protected double centY;
	/**
	 * An array containing the segments lying in the eye of the camera.
	 */
	protected Segment drawnSegments[];
	
	/**
	 * Sets the world that will be rendered with this camera.
	 * 
	 * @param world The reference to the world object.
	 * @return Fluent interface.
	 */
	public Camera setWorld(World world)
	{
		this.world = world;
		this.calculateViewport();
		if(null != this.painterInterface)
		{
			this.updateSegmentList();
		}
		return this;
	} // end setWorld();
	
	/**
	 * Returns the reference to the rendered world.
	 * 
	 * @return The rendered world. 
	 */
	public World getWorld()
	{
		return this.world;
	} // end getWorld();
	
	/**
	 * Sets the rendering strategy.
	 * 
	 * @param painterInterface
	 * @return Fluent interface.
	 */
	public Camera setPainterInterface(IPainter painterInterface)
	{
		this.painterInterface = painterInterface;
		return this;
	} // end setPainterInterface();
	
	/**
	 * Returns the current rendering strategy.
	 * 
	 * @return The rendering strategy. 
	 */
	public IPainter getPainterInterface()
	{
		return this.painterInterface;
	} // end getPainterInterface();
	
	/**
	 * Updates the list of segments that are currently covered by the camera.
	 * This method should be called every time we scroll the screen or make
	 * the zoom.
	 */
	public Camera updateSegmentList()
	{
		long whereStartsX = Math.round(Math.floor(this.posX / 1000.0));
		long whereStartsY = Math.round(Math.floor(this.posY / 1000.0));
		
		long whereEndsX = Math.round(Math.floor((this.posX + this.camWidth) / 1000.0));
		long whereEndsY = Math.round(Math.floor((this.posY + this.camHeight) / 1000.0));
		
		int size = (int)((whereEndsX - whereStartsX + 1) * (whereEndsY - whereStartsY + 1));
		
		/*
		if(null != this.drawnSegments)
		{
			for(Segment s: this.drawnSegments)
			{
				try
				{
					s.setDisplayed(false);
				}
				catch(IOException exception)
				{
					// null
				}
			}
		}

		Segment tmp[] = new Segment[size];

		int idx = 0;
		this.extraPoints.clear();
		this.painterInterface.startExtraPoints();
		for(long x = whereStartsX; x <= whereEndsX; x++)
		{
			for(long y = whereStartsY; y <= whereEndsY; y++)
			{
				Segment s = null;
				if(this.world.hasSegment((int)x, (int)y))
				{					
					try
					{
						tmp[idx++] = s = this.world.getSegment((int)x, (int)y).setDisplayed(true);
					}
					catch(IOException exception)
					{
						// null
					}
					
					this.painterInterface.updateExtraPoints(s, this.extraPoints);
				}
			}
		}
		this.painterInterface.finalizeExtraPoints();
		this.drawnSegments = Arrays.copyOf(tmp, idx);
		 */
		return this;
	} // end updateSegmentList();
	
	/**
	 * This method should be called every time the camera geometry changes. It matches
	 * the camera window to the world dimensions, updates the camera position etc.
	 * 
	 * @return Fluent interface. 
	 */
	public Camera calculateViewport()
	{
		Dimension d = this.getSize();
		
		this.camWidth = d.getWidth() * this.mpp;
		this.camHeight = d.getHeight() * this.mpp;
		
		double wx, wy;
		
		if(this.camWidth > (wx = (this.world.getX() * 1000.0)))
		{
			this.hOverflow = true;
			this.centX = (this.camWidth - wx) / 2.0 / this.mpp;
		}
		else
		{
			this.hOverflow = false;
			this.centX = 0.0;
		}
		if(this.camHeight > (wy = (this.world.getY() * 1000.0)))
		{
			this.vOverflow = true;
			this.centY = (this.camHeight - wy) / 2.0 / this.mpp;
		}
		else
		{
			this.vOverflow = false;
			this.centY = 0.0;
		}

		return this;
	} // end calculateViewport();
} // end Camera;

