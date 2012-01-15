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
package org.invenzzia.visitons.visualization;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import org.invenzzia.utils.geometry.IPoint;
import org.invenzzia.utils.geometry.Point;

/**
 * The camera objects are responsible for rendering the simulation
 * and managing the properties of the area that is visible to the
 * user.
 *
 * @author zyxist
 */
public class Camera extends JPanel
{
	public static final double DEFAULT_MPP = 1.0;
	private static final long serialVersionUID = 1L;
	
	/**
	 * A reference to the rendered world object.
	 */
	protected World world = null;
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
	 * Restores the initial state of the camera.
	 * 
	 * @return Fluent interface.
	 */
	public Camera clearWorld()
	{
		this.world = null;
		this.drawnSegments = null;
		this.posX = this.posY = 0.0;
		this.camWidth = this.camHeight = 0.0;
		this.centX = this.centY = 0.0;
		this.hOverflow = this.vOverflow = false;
		return this;
	} // end clearWorld();
	
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

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(null == this.world)
		{
			this.drawClearCamera(g);
		}
		else
		{
			this.drawWorld(g);
		}
	} // end paintComponent();
	
	/**
	 * This method is called, if no world is loaded to render.
	 * It displays a dummy text.
	 */
	protected void drawClearCamera(Graphics g)
	{
		g.drawString("Nothing to draw.", this.getWidth() / 2 - 80, this.getHeight() / 2 - 10);
	} // end drawClearCamera();
	
	/**
	 * Draws the world on the given graphics canvas.
	 */
	protected void drawWorld(Graphics g)
	{
		
	} // end drawWorld();
	
	/**
	 * Converts pixels into the world coordinates.
	 * 
	 * @param coord The pixel coordinate X.
	 * @return X world coordinate.
	 */
	public double pix2worldX(long coord)
	{
		return this.posX + (coord - this.centX) * this.mpp;
	} // end pix2worldX();

	/**
	 * Converts pixels into the world coordinates.
	 * 
	 * @param coord The pixel coordinate Y.
	 * @return Y world coordinate.
	 */
	public double pix2worldY(long coord)
	{
		return this.posY + (coord - this.centY) * this.mpp;
	} // end pix2worldY();

	/**
	 * Converts world coordinates into pixels..
	 * 
	 * @param coord The world coordinate X.
	 * @return X pixel position.
	 */
	public long world2pixX(double coord)
	{
		return Math.round((coord - this.posX) / this.mpp + this.centX);
	} // end world2pixX();

	/**
	 * Converts world coordinates into pixels.
	 * 
	 * @param coord The world coordinate Y.
	 * @return Y pixel position.
	 */
	public long world2pixY(double coord)
	{
		return Math.round((coord - this.posY) / this.mpp + this.centY);
	} // end world2pixY();

	/**
	 * Converts world coordinates into pixels.
	 * 
	 * @param coord The point with world coordinates.
	 * @return The pixel point.
	 */
	public IPoint world2pix(IPoint p)
	{
		return Point.valueOf(this.world2pixX(p.getX()), this.world2pixY(p.getY()));
	} // end world2pix();

	/**
	 * Converts length in the world units into pixel length.
	 * 
	 * @param length The world length.
	 * @return The pixel length.
	 */
	public long world2pixLength(double length)
	{
		return Math.round(length / this.mpp);
	} // end world2pixLength();
} // end Camera;
