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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * Draws a horizontal or vertical ruler that simplifies the navigation
 * on the network editor.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Ruler extends JComponent {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int SIZE = 35;
	/**
	 * Is the ruler drawn horizontally or vertically?
	 */
	private int orientation;
	/**
	 * Snapshot of the camera model information.
	 */
	private CameraModelSnapshot snapshot;
	
	public Ruler() {
		this.orientation = Ruler.HORIZONTAL;
		this.setMinimumSize(new Dimension(Ruler.SIZE, Ruler.SIZE));
	}
	
	public Ruler(int orientation) {
		this.orientation = orientation;
		this.setMinimumSize(new Dimension(Ruler.SIZE, Ruler.SIZE));
	}
	
	public int getOrientation() {
		return this.orientation;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	public void setPreferredSize(int pw) {
		if(Ruler.HORIZONTAL == this.orientation) {
			this.setPreferredSize(new Dimension(pw, Ruler.SIZE));
		} else {
			this.setPreferredSize(new Dimension(Ruler.SIZE, pw));
		}
	}
	
	/**
	 * Injects the new data snapshot into the ruler.
	 * 
	 * @param snapshot The updated camera snapshot.
	 */
	public void injectSnapshot(CameraModelSnapshot snapshot) {
		this.snapshot = snapshot;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Rectangle drawHere = g.getClipBounds();
		
		// Fill the area with the dirty brown/orange color.
		g.setColor(new Color(230, 163, 4));
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
		
		// Choose the ruler label font settings
		g.setFont(new Font("SansSerif", Font.PLAIN, 10));
		g.setColor(Color.black);
		
		if(null != this.snapshot) {
			double mpp = this.snapshot.getMpp();

			double resolution;
			int labelResolution;
			if(mpp < 0.5) {
				resolution = 5.0;
				labelResolution = 50;
			} else {
				resolution = 10.0 * Math.floor(mpp + 0.5);
				labelResolution = (int)(resolution * 10.0);
				
			}
			int resPix = (int) this.snapshot.world2pix(resolution);
			int labels;

			if(Ruler.HORIZONTAL == this.orientation) {
				// Draw horizontal ruler
				double start = ((int)(this.snapshot.getPosX() / resolution)) * resolution;
				if(start < 0.0) {
					start = 0.0;
				}
				
				labels = (int)start;
				int startPx = this.snapshot.world2pixX(start);
				int current = startPx;

				while(current < drawHere.width) {
					int tickLength = 7;
		
					if(labels % labelResolution == 0) {
						if(labels % (int)CameraModel.SEGMENT_SIZE == 0) {
							tickLength = 15;
							g.drawString(Integer.toString(labels / 1000) + " km", current - 5, 10);
						} else {
							tickLength = 12;
							g.drawString(Integer.toString(labels % (int)CameraModel.SEGMENT_SIZE), current - 5, 10);
						}
					}
					
					g.drawLine(current, Ruler.SIZE - 1, current, Ruler.SIZE - tickLength);
					labels += (int) resolution;
					current += resPix;
				}

			} else {
				// Draw vertical ruler
				double start = ((int)(this.snapshot.getPosY() / resolution)) * resolution;
				if(start < 0.0) {
					start = 0.0;
				}
				labels = (int)start;
				int startPx = this.snapshot.world2pixY(start);
				int current = startPx;

				while(current < drawHere.height) {
					int tickLength = 7;
					if(labels % labelResolution == 0) {
						if(labels % (int)CameraModel.SEGMENT_SIZE == 0) {
							tickLength = 15;
							g.drawString(Integer.toString(labels / 1000) + " km", 10, current - 5);
						} else {
							tickLength = 12;
							g.drawString(Integer.toString(labels % (int)CameraModel.SEGMENT_SIZE), 10, current - 5);
						}
					}
					
					g.drawLine(Ruler.SIZE - 1, current, Ruler.SIZE - tickLength, current);
					labels += (int) resolution;
					current += resPix;
				}
			}
		}
	}
}
