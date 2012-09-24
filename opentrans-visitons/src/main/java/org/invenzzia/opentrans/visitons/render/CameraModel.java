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

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.invenzzia.helium.concurrency.InvocationTicket;
import org.invenzzia.helium.concurrency.MethodInvocator;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * A data model of the camera. It holds all the information about the currently
 * viewed part of the world. Listener functionality allows the controllers and
 * views to be notified about changes.
 * 
 * The class is not synchronized. The multithreaded access must be protected
 * externally.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CameraModel extends AbstractCameraModelFoundation {	
	private Map<ICameraModelListener, InvocationTicket> invocationTickets = new LinkedHashMap<>();
	private Set<ICameraModelListener> listeners = new HashSet<>();

	/**
	 * We grab the world size from here.
	 */
	protected final World world;
	/**
	 * Listener notifications can exceed the thread boundaries. Method invocator assumes that each object
	 * is notified in its own thread.
	 */
	private final MethodInvocator methodInvocator;
	
	public CameraModel(World world, MethodInvocator methodInvocator) {
		this.world = Preconditions.checkNotNull(world, "The camera model cannot operate without a world object.");
		this.methodInvocator = Preconditions.checkNotNull(methodInvocator);
	}

	/**
	 * Sets the new zoom level (metres-per-pixel unit). Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param mpp Zoom level in metres-per-pixel unit.
	 */
	public void setMpp(double mpp) {
		this.mpp = mpp;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	public double getSizeX() {
		return this.world.getX() * CameraModel.SEGMENT_SIZE;
	}
	
	public double getSizeY() {
		return this.world.getY() * CameraModel.SEGMENT_SIZE;
	}

	/**
	 * Sets the new top-left corner position (in metres). Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param posX Top-level corner position on the world map (in metres).
	 */
	public void setPosX(double posX) {
		if(posX > this.world.getX()) {
			posX = this.world.getX() - CameraModel.MIN_VIEWPORT;
		}
		this.posX = posX;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}

	/**
	 * Sets the new top-left corner position (in metres). Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param posY Top-level corner position on the world map (in metres).
	 */
	public void setPosY(double posY) {
		if(posY > this.world.getY()) {
			posY = this.world.getY() - CameraModel.MIN_VIEWPORT;
		}
		this.posY = posY;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Sets the new top-left corner position (in metres). Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param x X coordinate in the world map units (metres).
	 * @param y Y coordinate in the world map units (metres).
	 */
	public void setPos(double x, double y) {
		if(x > this.getSizeX()) {
			x = this.getSizeX() - CameraModel.MIN_VIEWPORT;
		}
		if(y > this.getSizeY()) {
			y = this.getSizeY() - CameraModel.MIN_VIEWPORT;
		}
		
		this.posX = x;
		this.posY = y;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Sets the height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param viewportWidth The viewport height (in metres).
	 */
	public void setViewportHeight(double viewportHeight) {
		this.viewportHeight = viewportHeight;
		this.viewportHeightPx = (int) (viewportHeight / this.mpp);
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Sets the height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param viewportWidth The viewport height (in metres).
	 */
	public void setViewportHeightPx(int viewportHeight) {
		this.viewportHeightPx = viewportHeight;
		this.viewportHeight = viewportHeight * this.mpp;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}

	/**
	 * Sets the height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param viewportWidth The viewport width (in metres).
	 */
	public void setViewportWidth(double viewportWidth) {
		this.viewportWidth = viewportWidth;
		this.viewportWidthPx = (int) (viewportWidth / this.mpp);
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}

	/**
	 * Sets the height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param viewportWidth The viewport width (in metres).
	 */
	public void setViewportWidthPx(int viewportWidth) {
		this.viewportWidthPx = viewportWidth;
		this.viewportWidth = viewportWidth * this.mpp;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Sets the width and height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param width The viewport width (in metres).
	 * @param height The viewport height (in metres).
	 */
	public void setViewportDimension(double width, double height) {
		this.viewportWidth = width;
		this.viewportHeight = height;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Sets the width and height of the viewport in metres. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param width The viewport width (in metres).
	 * @param height The viewport height (in metres).
	 */
	public void setViewportDimensionPx(int width, int height) {
		this.viewportWidthPx = width;
		this.viewportHeightPx = height;
		
		this.viewportWidth = width * this.mpp;
		this.viewportHeight = height * this.mpp;
		
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Scrolls the viewport horizontally by the given delta. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param delta Delta movement (in metres).
	 */
	public void scrollHorizontal(double delta) {
		if(!this.horizOverflow) {
			double newVal = this.posX + delta;
			if(newVal > 0.0 && (newVal + this.viewportWidth) < this.world.getX() * CameraModel.SEGMENT_SIZE) {
				this.posX = newVal;
				this.calculateViewport();
				this.notifyCameraModelListeners();
			}
		}
	}
	
	/**
	 * Scrolls the viewport vertically by the given delta. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param delta Delta movement (in metres).
	 */
	public void scrollVertical(double delta) {
		if(!this.vertOverflow) {
			double newVal = this.posY + delta;
			if(newVal > 0.0 && (newVal + this.viewportWidth) < this.world.getY() * CameraModel.SEGMENT_SIZE) {
				this.posY = newVal;
				this.calculateViewport();
				this.notifyCameraModelListeners();
			}
		}
	}
	
	/**
	 * Centers the viewport at the given coordinates. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param x (in metres)
	 * @param y (in metres)
	 */
	public void centerAt(double x, double y) {
		double hx = x - this.viewportWidth / 2.0;
		double hy = y - this.viewportHeight / 2.0;
		
		if(hx < 0.0) {
			this.posX = 0.0;
		} else if(hx > this.world.getX() * CameraModel.SEGMENT_SIZE) {
			this.posX = (this.world.getX() + 1) * CameraModel.SEGMENT_SIZE - this.viewportWidth;
		} else {
			this.posX = hx;
		}
		
		if(hy < 0.0) {
			this.posY = 0.0;
		} else if(hy > this.world.getY() * CameraModel.SEGMENT_SIZE) {
			this.posY = (this.world.getY() + 1) * CameraModel.SEGMENT_SIZE - this.viewportHeight;
		} else {
			this.posY = hy;
		}
		this.calculateViewport();
		this.notifyCameraModelListeners();
	}
	
	/**
	 * Changes the zoom by the given factor. The current zoom level is multiplied by the
	 * value specified in the argument. Updating causes the recalculation of
	 * the viewport and notifications of all the listeners. If the model is tied to the GUI
	 * code, this method should be updated in the event dispatch thread.
	 * 
	 * @param factor 
	 */
	public void zoom(double factor) {
		if(!this.vertOverflow && !this.horizOverflow) {
			double newVal = this.mpp * factor;
			if(newVal >= 0.1) {
				this.mpp = newVal;

				this.calculateViewport();
				this.notifyCameraModelListeners();
			}
		}
	}
	
	/**
	 * Calculates the remaining parameters of the viewport. The method is called automatically
	 * if one of the viewport parameters changes, but the programmer is also allowed to spawn
	 * it manually.
	 */
	public void calculateViewport() {
		double wx, wy;
		
		this.viewportWidth = this.viewportWidthPx * this.mpp;
		this.viewportHeight = this.viewportHeightPx * this.mpp;
		
		if(this.viewportWidth > (wx = (this.world.getX() * CameraModel.SEGMENT_SIZE))) {
			this.horizOverflow = true;
			this.overflowCenterX = (this.viewportWidth - wx) / 2.0 / this.mpp;
		} else {
			this.horizOverflow = false;
			this.overflowCenterX = 0.0;
		}
		if(this.viewportHeight > (wy = (this.world.getY() * CameraModel.SEGMENT_SIZE))) {
			this.vertOverflow = true;
			this.overflowCenterY = (this.viewportHeight - wy) / 2.0 / this.mpp;
		} else {
			this.vertOverflow = false;
			this.overflowCenterX = 0.0;
		}
	}

	public void addCameraModelListener(ICameraModelListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeCameraModelListener(ICameraModelListener listener) {
		this.listeners.remove(listener);
	}
	
	public void removeCameraModelListeners() {
		this.listeners.clear();
	}
	
	protected void notifyCameraModelListeners() {
		for(ICameraModelListener listener: this.listeners) {
			InvocationTicket ticket = this.invocationTickets.get(listener);
			if(null == ticket) {
				this.invocationTickets.put(listener, this.methodInvocator.executeMethod(listener, "cameraUpdated", this));
			} else {
				this.methodInvocator.executeMethod(listener, ticket, this);
			}
		}
	}
}
