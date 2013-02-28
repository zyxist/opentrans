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

package org.invenzzia.opentrans.lightweight.ui.netview;

import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.Renderer;

/**
 * Displays the image from the camera renderer. It has the ability to scroll.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CameraView extends JViewport implements Scrollable {
	/**
	 * Snapshot of the camera information.
	 */
	private CameraModelSnapshot snapshot;
	/**
	 * Here we take the image to render from.
	 */
	private Renderer renderer;
	private Dimension preferredScrollableViewportSize;	
	private int maxUnitIncrement = 10;
	
	public CameraView() {
		this.setPreferredSize(new Dimension(1000, 1000));
		this.setOpaque(true);
	}
	
	/**
	 * Remember to @link {EditorView#revalidate} the editor view, if the renderer is changed. Otherwise, funny
	 * things may happen to the rulers.
	 * 
	 * @param renderer The new renderer to use.
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = Preconditions.checkNotNull(renderer, "The camera component cannot operate without a renderer.");
	}
	
	/**
	 * Returns the currently used renderer object.
	 * 
	 * @return Currently used renderer.
	 */
	public Renderer getRenderer() {
		return this.renderer;
	}
	
	public int getMaxUnitIncrement() {
		return this.maxUnitIncrement;
	}
	
	public void setMaxUnitIncrement(int inc) {
		this.maxUnitIncrement = inc;
	}
	
	public void setPreferredScrollableViewportSize(Dimension dimension) {
		this.preferredScrollableViewportSize = dimension;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.preferredScrollableViewportSize;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle rect, int orientation, int direction) {
		int currentPosition;
		if(SwingConstants.HORIZONTAL == orientation) {
			currentPosition = rect.x;
		} else {
			currentPosition = rect.y;
		}
		
		if(direction < 0) {
			int newPosition = currentPosition - (currentPosition / this.maxUnitIncrement) * this.maxUnitIncrement;
			return (newPosition == 0) ? this.maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / this.maxUnitIncrement) + 1) * this.maxUnitIncrement - currentPosition;
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle rect, int orientation, int direction) {
		if(SwingConstants.HORIZONTAL == orientation) {
			return rect.width - this.maxUnitIncrement;
		} else {
			return rect.height - this.maxUnitIncrement;
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	/**
	 * Injects the new data snapshot into the ruler.
	 * 
	 * @param snapshot The updated camera snapshot.
	 */
	public void injectSnapshot(CameraModelSnapshot snapshot) {
		this.snapshot = snapshot;
		this.setPreferredSize(new Dimension(snapshot.world2pixX(snapshot.getSizeX()), snapshot.world2pixY(snapshot.getSizeY())));
		this.setOpaque(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(null != this.renderer) {
			BufferedImage img = this.renderer.getServedImage();
			g.drawImage(img, 0, 0, null);
		}
	}
}
