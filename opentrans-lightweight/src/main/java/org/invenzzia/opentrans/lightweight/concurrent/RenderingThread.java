/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.concurrent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.ui.netview.CameraView;
import org.invenzzia.opentrans.visitons.render.Renderer;

/**
 * Rendering thread redraws the screen 25 times per second using the
 * {@link Renderer} instance from Visitons. After each repainting,
 * a proper notification is sent to Swing so that it could redraw
 * everything.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class RenderingThread extends AbstractMessageQueue {
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
	
	@Inject
	public RenderingThread(Renderer renderer) {
		this.renderer = renderer;
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
		this.processMessages();
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
		if(this.prevTime < FRAME_TIME) {
			Thread.sleep(FRAME_TIME - this.prevTime);
		} else {
			Thread.sleep(3);
		}
	}

}
