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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Active rendered which renders the world to the image buffer which can be used
 * by the GUI to display the simulation. The renderer should be registered in the
 * camera model as a listener to receive notifications about viewport changes.
 * 
 * The rendered shall be synchronized externally. The only multithreaded code is
 * the annotation on the listener method which guarantees us that the {@link cameraUpdated}
 * method will be updated within the same thread, as the renderer.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Renderer implements ICameraModelListener {
	private BufferedImage servedImage;
	private BufferedImage drawnImage;
	private final CameraModel model;
	private boolean modelUpdated;
	
	public Renderer(CameraModel model) {
		int width = model.getViewportWidthPx();
		int height = model.getViewportHeightPx();
		if(width <= 0) {
			width = 100;
		}
		if(height <= 0) {
			height = 100;
		}
		
		this.model = Preconditions.checkNotNull(model, "The renderer cannot operate without an active camera model.");
		this.servedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.drawnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		this.model.addCameraModelListener(this);
	}
	
	public BufferedImage getServedImage() {
		return this.servedImage;
	}
	
	public CameraModel getModel() {
		return this.model;
	}
	
	public void render(long prevFrameTime) {
		Graphics g = this.drawnImage.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.drawnImage.getWidth(), this.drawnImage.getHeight());

		g.setColor(Color.BLACK);
		
		int x = this.model.world2pixX(137.0);
		int y = this.model.world2pixY(231.0);

		g.drawString("Scrolling demo.", x, y);

		// Swap buffers.
		BufferedImage tmp = this.servedImage;
		this.servedImage = this.drawnImage;
		if(this.modelUpdated) {
			this.drawnImage = new BufferedImage(model.getViewportWidthPx(), model.getViewportHeightPx(), BufferedImage.TYPE_INT_ARGB);
			this.modelUpdated = false;
		} else {
			this.drawnImage = tmp;
		}
		
	}

	@Override
	public void cameraUpdated(CameraModel model) {
		this.modelUpdated = true;
	}
}
