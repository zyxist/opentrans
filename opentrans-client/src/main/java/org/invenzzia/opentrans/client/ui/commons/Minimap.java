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
package org.invenzzia.opentrans.client.ui.commons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * Minimap is able to show the simplified view of the network map as a series
 * of tiny rectangles representing world segments. Segments are coloured in
 * two ways: empty segments (with no vertices, edges, infrastructure etc.)
 * and used segments (at least one infrastructure element present). In addition,
 * we can highlight current viewport and segments covered by the mouse
 * cursor.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Minimap extends JComponent {
	private Color emptySegmentColor = new Color(188, 188, 188);
	private Color usedSegmentColor = new Color(0, 87, 6);
	private Color viewportEmptySegmentColor = new Color(220, 220, 220);
	private Color viewportUsedSegmentColor = new Color(0, 207, 15);
	private Color highlightEmptySegmentColor = new Color(231, 231, 231);
	private Color highlightUsedSegmentColor = new Color(150, 255, 157);
	
	private boolean data[][] = new boolean[][] { new boolean[]{ false } };
	
	private Dimension viewport = new Dimension();
	private int cursorX = -1;
	private int cursorY = -1;
	
	private int maximumSegmentSize = 7;
	
	public Minimap() {
		super();
	}

	public Color getEmptySegmentColor() {
		return this.emptySegmentColor;
	}

	public void setEmptySegmentColor(Color emptySegmentColor) {
		this.emptySegmentColor = emptySegmentColor;
	}

	public Color getHighlightEmptySegmentColor() {
		return this.highlightEmptySegmentColor;
	}

	public void setHighlightEmptySegmentColor(Color highlightEmptySegmentColor) {
		this.highlightEmptySegmentColor = highlightEmptySegmentColor;
	}

	public Color getHighlightUsedSegmentColor() {
		return this.highlightUsedSegmentColor;
	}

	public void setHighlightUsedSegmentColor(Color highlightUsedSegmentColor) {
		this.highlightUsedSegmentColor = highlightUsedSegmentColor;
	}

	public Color getUsedSegmentColor() {
		return this.usedSegmentColor;
	}

	public void setUsedSegmentColor(Color usedSegmentColor) {
		this.usedSegmentColor = usedSegmentColor;
	}

	public Dimension getViewport() {
		return this.viewport;
	}

	public void setViewport(Dimension viewport) {
		this.viewport = viewport;
	}

	public Color getViewportEmptySegmentColor() {
		return this.viewportEmptySegmentColor;
	}

	public void setViewportEmptySegmentColor(Color viewportEmptySegmentColor) {
		this.viewportEmptySegmentColor = viewportEmptySegmentColor;
	}

	public Color getViewportUsedSegmentColor() {
		return this.viewportUsedSegmentColor;
	}

	public void setViewportUsedSegmentColor(Color viewportUsedSegmentColor) {
		this.viewportUsedSegmentColor = viewportUsedSegmentColor;
	}

	public int getMaximumSegmentSize() {
		return maximumSegmentSize;
	}

	public void setMaximumSegmentSize(int maximumSegmentSize) {
		this.maximumSegmentSize = maximumSegmentSize;
	}

	public boolean[][] getData() {
		return data;
	}

	public void setData(boolean[][] data) {
		this.data = data;
	}
	
	public void setCursorPosition(int x, int y){
		this.cursorX = x;
		this.cursorY = y;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		// Calculate the painting dimensions.
		double estimatedPixelSizeX = this.getWidth() / this.data.length;
		double estimatedPixelSizeY = this.getHeight() / this.data[0].length;
		/** The size of a single rectangle */
		double estimatedPixelSize;
		int finalPixelSize;
		if(estimatedPixelSizeX > estimatedPixelSizeY) {
			estimatedPixelSize = estimatedPixelSizeY;
		} else {
			estimatedPixelSize = estimatedPixelSizeX;
		}
		if(((double)this.maximumSegmentSize) < estimatedPixelSize) {
			System.out.println("I'm setting it.");
			estimatedPixelSize = this.maximumSegmentSize;
		}
		/** segments per pixel - if more than 1, interpolation is used. */
		int spp;
		if(estimatedPixelSize < 1.0) {
			finalPixelSize = 1;
			spp = (int) Math.ceil(1.0 / estimatedPixelSize);
		} else {
			finalPixelSize = (int)estimatedPixelSize;
			spp = 1;
		}
		/** actual size of painting - must be lower or equal to the component size */
		int paintingWidth = finalPixelSize * this.data.length + this.data.length - 1;
		int paintingHeight = finalPixelSize * this.data[0].length + this.data[0].length - 1;

		/** offsets from the component boundaries */
		int offsetX;
		int offsetY;
		if(paintingWidth < this.getWidth()) {
			offsetX = (this.getWidth() - paintingWidth) / 2;
		} else {
			offsetX = 0;
		}
		if(paintingHeight < this.getHeight()) {
			offsetY = (this.getHeight() - paintingHeight) / 2;
		} else {
			offsetY = 0;
		}

		// Do the painting.
		int posX = offsetX;
		for(int i = 0; i < this.data.length; i += spp) {
			int posY = offsetY;
			for(int j = 0; j < this.data[0].length; j += spp) {
				boolean state;
				if(1 == spp) {
					state = this.data[i][j];
				} else {
					state = this.interpolate(i, j, spp);
				}
				if(state) {
					g.setColor(this.usedSegmentColor);
				} else {
					g.setColor(this.emptySegmentColor);
				}
				g.fillRect(posX, posY, finalPixelSize, finalPixelSize);
				
				posY += finalPixelSize + 1;
			}
			posX += finalPixelSize + 1;
		}
	}
	
	/**
	 * If one rectangle represents more than one segment, we must interpolate its state
	 * from the states of several other segments.
	 * 
	 * @param x Starting segment (X).
	 * @param y Starting segment (Y)
	 * @param spp Segments Per Pixel value.
	 * @return True, if at least one segment within the area is used.
	 */
	public boolean interpolate(int x, int y, int spp) {
		int ex = x + spp;
		int ey = y + spp;
		for(int i = x; i < ex; i++){
			for(int j = y; j < ey; j++) {
				if(this.data[i][j]) {
					return true;
				}
			}
		}
		return false;
	}	
}
