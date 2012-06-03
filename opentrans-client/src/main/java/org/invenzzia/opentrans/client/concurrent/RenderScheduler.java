/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.concurrent;

import javax.swing.SwingUtilities;
import org.invenzzia.helium.activeobject.AbstractScheduler;
import org.invenzzia.opentrans.client.ui.netedit.CameraView;
import org.invenzzia.opentrans.visitons.render.Renderer;

/**
 * This scheduler handles the network editor rendering queue. It spawns redrawing
 * in a constant amount of time and handles extra requests related to the management
 * of the renderer.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RenderScheduler extends AbstractScheduler {
	private final static long FRAME_TIME = 25;
	/**
	 * The Visitons renderer which prepares the image.
	 */
	private Renderer renderer;
	/**
	 * Send here the repaint requests on every frame.
	 */
	private CameraView cameraView;
	/**
	 * The rendering time of the previous frame.
	 */
	private long prevTime;
	
	public RenderScheduler(String name) {
		super(name);
	}

	public void setRenderer(Renderer renderer) {
		try {
			this.guard();
			this.renderer = renderer;
		} finally {
			this.unguard();
		}
	}
	
	public Renderer getRenderer() {
		return this.renderer;
	}
	
	public void setCameraView(CameraView cameraView) {
		this.cameraView = cameraView;
	}
	
	public CameraView getCameraView() {
		return this.cameraView;
	}

	@Override
	protected void executeStep() throws InterruptedException {
		long d0 = System.currentTimeMillis();
		// If there are any requests for this thread, process them.
		while(!this.requests.isEmpty()) {
			this.handleRequest(this.requests.remove(0));
		}
		// Render the current frame.
		if(null != this.renderer) {
			this.renderer.render(this.prevTime);
		}
		this.prevTime = System.currentTimeMillis() - d0;
		
		// Send the repaint request to Swing
		if(null != this.cameraView) {
			this.cameraView.repaint();
		}
		
		// Suspend the execution to provide a constant frame rate.
		if(this.prevTime < RenderScheduler.FRAME_TIME) {
			Thread.sleep(RenderScheduler.FRAME_TIME - this.prevTime);
		} else {
			Thread.sleep(3);
		}
	}

}
