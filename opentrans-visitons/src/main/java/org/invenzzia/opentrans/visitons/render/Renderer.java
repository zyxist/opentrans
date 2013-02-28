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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.invenzzia.opentrans.visitons.network.Segment;

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
@Singleton
public final class Renderer {
	/**
	 * The color of the background.
	 */
	public static final Color BACKGROUND_COLOR = Color.WHITE;
	/**
	 * Double buffering: the image that is currently shown on the screen.
	 */
	private BufferedImage servedImage;
	/**
	 * Double buffering: the image we are currently drawing on.
	 */
	private BufferedImage drawnImage;
	/**
	 * Map of resources to draw on the buffer.
	 */
	private SceneManager sceneManager;
	/**
	 * Snapshot of the camera model - we don't have to care about updates and blocking.
	 */
	private CameraModelSnapshot previousModel;
	/**
	 * Rendering streams that paint sequentially on the device in each frame.
	 */
	private List<IRenderingStream> renderingStreams = new LinkedList<>();
	/**
	 * List of visible segments, updated every time we change the view port.
	 */
	private List<Segment> visibleSegments = new LinkedList<>();
	
	@Inject
	public Renderer(SceneManager sceneManager) {
		this.sceneManager = Preconditions.checkNotNull(sceneManager, "The renderer cannot operate without a scene manager.");
		this.createBuffers();
	}
	
	/**
	 * This method reads the data from the camera model and creates the initial buffers.
	 */
	private void createBuffers() {
		CameraModelSnapshot model = (CameraModelSnapshot) this.sceneManager.getResource(CameraModelSnapshot.class);
		int width = model.getViewportWidthPx();
		int height = model.getViewportHeightPx();
		if(width <= 0) {
			width = 100;
		}
		if(height <= 0) {
			height = 100;
		}
		this.servedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.drawnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Updates the buffer structures if the camera model has been changed.
	 */
	private void updateBuffers(Map<Object, Object> snapshot) {
		CameraModelSnapshot model = (CameraModelSnapshot) snapshot.get(CameraModelSnapshot.class);
		if(model != this.previousModel) {
			int width = model.getViewportWidthPx();
			if(width <= 0) {
				width = 100;
			}
			int height = model.getViewportHeightPx();
			if(height <= 0) {
				height = 100;
			}
			this.drawnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
	/**
	 * Replaces the served buffer with the buffer we are drawing on.
	 * @param snapshot 
	 */
	public void swapBuffers(Map<Object, Object> snapshot) {
		CameraModelSnapshot model = (CameraModelSnapshot) snapshot.get(CameraModelSnapshot.class);
		
		BufferedImage tmp = this.servedImage;
		this.servedImage = this.drawnImage;
		this.drawnImage = tmp;
		this.previousModel = model;
	}
	
	/**
	 * Appends a new stream to the rendering queue.
	 */
	public void addRenderingStream(IRenderingStream stream) {
		this.renderingStreams.add(Preconditions.checkNotNull(stream, "Attempt to register a NULL rendering stream."));
	}
	
	/**
	 * Clears the list of rendering streams.
	 */
	public void clearRenderingStreams() {
		this.renderingStreams.clear();
	}
	
	/**
	 * Returns the current image ready for display. The renderer supports double buffering;
	 * at the same time, another thread is expected to prepare the second image that will
	 * be swapped with this one once the rendering is finished.
	 * 
	 * @return Camera buffer prepared to display.
	 */
	public BufferedImage getServedImage() {
		return this.servedImage;
	}

	/**
	 * Renders the single frame and swaps the camera buffers. In order to make the animations
	 * work, the method expects the actual rendering time of the previous frame.
	 * 
	 * @param prevFrameTime Actual rendering time of the previous frame.
	 */
	public void render(long prevFrameTime) {
		Map<Object, Object> snapshot = this.sceneManager.getSnapshot();
		this.updateBuffers(snapshot);
		
		Graphics2D g = (Graphics2D) this.drawnImage.getGraphics();
		g.setColor(Renderer.BACKGROUND_COLOR);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.fillRect(0, 0, this.drawnImage.getWidth(), this.drawnImage.getHeight());
		
		// Run the rendering streams.
		for(IRenderingStream stream: this.renderingStreams) {
			stream.render((Graphics2D) g, snapshot, prevFrameTime);
		}
		this.swapBuffers(snapshot);
	}

	/**
	 * Scans the segment table and determines, which segments are visible in our viewport.
	 */
/*
	protected void findVisibleSegments() {	
		int whereStartsX = (int) Math.round(Math.floor(this.viewport.getPosX() / 1000.0));
		int whereStartsY = (int) Math.round(Math.floor(this.viewport.getPosY() / 1000.0));
		
		int whereEndsX = (int) Math.round(Math.floor((this.viewport.getPosX() + this.viewport.getViewportWidth()) / 1000.0));
		int whereEndsY = (int) Math.round(Math.floor((this.viewport.getPosY() + this.viewport.getViewportHeight()) / 1000.0));
		
		int size = (int)((whereEndsX - whereStartsX + 1) * (whereEndsY - whereStartsY + 1));
		
		this.visibleSegments.clear();
		for(int x = whereStartsX; x <= whereEndsX; x++) {
			for(int y = whereStartsY; y <= whereEndsY; y++) {
				Segment s = this.world.findSegment(x, y);
				if(null != s) {
					this.visibleSegments.add(s);
				}
			}
		}
	}
*/
}
