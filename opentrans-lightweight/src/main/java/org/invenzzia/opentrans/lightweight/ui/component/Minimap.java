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
package org.invenzzia.opentrans.lightweight.ui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JComponent;
import org.invenzzia.opentrans.visitons.render.AbstractCameraModelFoundation;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

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
	/**
	 * Shows which segments are in use and which are not.
	 */
	private boolean data[][] = new boolean[][] { new boolean[]{ false } };
	/**
	 * The offset between the left boundary of the component and the beginning of the
	 * drawing area.
	 */
	private int offsetX;
	/**
	 * The offset between the top boundary of the component and the beginning of the
	 * drawing area.
	 */
	private int offsetY;
	/**
	 * The size of a 'pixel' representing the single segment.
	 */
	private int pixelSize;
	/**
	 * Segments per pixel - if the drawing area is too small to represent each segment individually.
	 */
	private int spp = 1;
	/**
	 * The effective drawing area - always equal to or lower than the component width.
	 */
	private int effectiveSizeX;
	/**
	 * The effective drawing area - always equal to or lower than the component height.
	 */
	private int effectiveSizeY;
	/**
	 * Mouse cursor position over the component in pixels.
	 */
	private int cursorX;
	/**
	 * Mouse cursor position over the component in pixels.
	 */
	private int cursorY;
	/**
	 * The maximum size of a rectangle representing the segment.
	 */
	private int maximumSegmentSize = 21;
	/**
	 * If the field is set to true, the component allows selecting the segments.
	 */
	private boolean segmentSelectionAllowed = true;
	/**
	 * Camera model keeps the information about viewport.
	 */
	private AbstractCameraModelFoundation cameraModel;
	/**
	 * X coordinate of the viewport segment start.
	 */
	private int viewportX = 0;
	/**
	 * Y coordinate of the viewport segment end.
	 */
	private int viewportY = 0;
	/**
	 * Width of the viewport (number of segments).
	 */
	private int viewportWidth = 0;
	/**
	 * Height of the viewport (number of segments).
	 */
	private int viewportHeight = 0;
	
	private Set<ISegmentSelectionListener> segmentSelectionListeners;
	
	public Minimap() {
		super();
		this.segmentSelectionListeners = new LinkedHashSet<>();
		
		MinimapMouseListener localListener = new MinimapMouseListener();
		this.addMouseListener(localListener);
		this.addMouseMotionListener(localListener);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateData();
			}
		});
	}
	
	/**
	 * Registers a new segment selection listener.
	 * 
	 * @param listener 
	 */
	public void addSegmentSelectionListener(ISegmentSelectionListener listener) {
		this.segmentSelectionListeners.add(listener);
	}
	
	/**
	 * Unregisters the segment selection listener.
	 * 
	 * @param listener 
	 */
	public void removeSegmentSelectionListener(ISegmentSelectionListener listener) {
		this.segmentSelectionListeners.remove(listener);
	}
	
	/**
	 * Unregisters all segment selection listeners.
	 */
	public void removeSegmentSelectionListeners() {
		this.segmentSelectionListeners.clear();
	}
	
	/**
	 * Returns the flag that controls whether the component displays and emits the segment selection
	 * events.
	 * 
	 * @return True, if the minimap permits choosing a segment.
	 */
	public boolean isSegmentSelectionAllowed() {
		return this.segmentSelectionAllowed;
	}
	
	/**
	 * Sets the flag that allows the user to select a segment using a minimap.
	 * 
	 * @param value True to enable segment selection.
	 */
	public void setSegmentSelectionAllowed(boolean value) {
		this.segmentSelectionAllowed = value;
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

	/**
	 * Inserts the 2D array of boolean values that show which segments of the world are used, and which are not.
	 * Used segments are such segments that contain at least one vertex associated with them.
	 * 
	 * @param data 2D array of the world segment usage.
	 */
	public void setData(boolean[][] data) {
		this.data = data;
		this.updateData();
	}
	
	/**
	 * Provides the camera data.
	 * 
	 * @param camera 
	 */
	public void setViewport(AbstractCameraModelFoundation camera) {
		this.cameraModel = camera;
	}

	/**
	 * Performs the recalculation of the internal drawing data. The method is called automatically,
	 * when the new data is added.
	 */
	public final void updateData() {
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
			estimatedPixelSize = this.maximumSegmentSize;
		}
		/** segments per pixel - if more than 1, interpolation is used. */
		if(estimatedPixelSize < 1.0) {
			finalPixelSize = 1;
			this.spp = (int) Math.ceil(1.0 / estimatedPixelSize);
		} else {
			finalPixelSize = (int)estimatedPixelSize;
			this.spp = 1;
		}
		/** actual size of painting - must be lower or equal to the component size */
		this.pixelSize = finalPixelSize;
		this.effectiveSizeX = finalPixelSize * this.data.length + this.data.length - 1;
		this.effectiveSizeY = finalPixelSize * this.data[0].length + this.data[0].length - 1;

		/** offsets from the component boundaries */
		if(this.effectiveSizeX < this.getWidth()) {
			this.offsetX = (this.getWidth() - this.effectiveSizeX) / 2;
		} else {
			this.offsetX = 0;
		}
		if(this.effectiveSizeY < this.getHeight()) {
			this.offsetY = (this.getHeight() - this.effectiveSizeY) / 2;
		} else {
			this.offsetY = 0;
		}
		if(null != this.cameraModel) {
			this.viewportX = (int) Math.floor(this.cameraModel.getPosX() / CameraModelSnapshot.SEGMENT_SIZE);
			this.viewportY = (int) Math.floor(this.cameraModel.getPosY() / CameraModelSnapshot.SEGMENT_SIZE);
			this.viewportWidth = (int) Math.ceil(this.cameraModel.getViewportWidth() / CameraModelSnapshot.SEGMENT_SIZE);
			this.viewportHeight = (int) Math.ceil(this.cameraModel.getViewportHeight() / CameraModelSnapshot.SEGMENT_SIZE);
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		int posX = this.offsetX;
		for(int i = 0; i < this.data.length; i += this.spp) {
			int posY = this.offsetY;
			for(int j = 0; j < this.data[0].length; j += this.spp) {
				boolean state;
				if(1 == this.spp) {
					state = this.data[i][j];
				} else {
					state = this.interpolate(i, j, this.spp);
				}
				if(this.cursorX >= posX && this.cursorX < posX + this.pixelSize && this.cursorY >= posY && this.cursorY < posY + this.pixelSize) {
					if(state) {
						g.setColor(this.highlightUsedSegmentColor);
					} else {
						g.setColor(this.highlightEmptySegmentColor);
					}
				} else {
					boolean withinViewport = (i >= this.viewportX && i < this.viewportX + this.viewportWidth
						&& j >= this.viewportY && j < this.viewportY + this.viewportHeight);
					if(state) {
						g.setColor(withinViewport ? this.viewportUsedSegmentColor : this.usedSegmentColor);
					} else {
						g.setColor(withinViewport ? this.viewportEmptySegmentColor : this.emptySegmentColor);
					}
				}

				g.fillRect(posX, posY, this.pixelSize, this.pixelSize);
				
				posY += this.pixelSize + 1;
			}
			posX += this.pixelSize + 1;
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
	
	/**
	 * Allows listening for segment selection on the minimap.
	 */
	public static interface ISegmentSelectionListener {
		/**
		 * Notifies that someone has selected a segment on the minimap.
		 * @param evt 
		 */
		public void segmentSelected(SegmentSelectionEvent evt);
	}
	
	/**
	 * The event that carries the information about the selected segment.
	 */
	public static class SegmentSelectionEvent {
		private final int x;
		private final int y;
		
		public SegmentSelectionEvent(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * X coordinate of the selected segment.
		 * 
		 * @return X coordinate of the selected segment.
		 */
		public int getX() {
			return this.x;
		}
		
		/**
		 * Y coordinate of the selected segment.
		 * @return Y coordinate of the selected segment.
		 */
		public int getY() {
			return this.y;
		}
	}
	
	/**
	 * Handles the internal minimap logic: highlighting the segments under the cursor and
	 * clicking on them.
	 */
	class MinimapMouseListener extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent e) {
			cursorX = e.getX();
			cursorY = e.getY();			
			repaint();
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getX() < offsetX || e.getX() > (offsetX + effectiveSizeX) ||
				  e.getY() < offsetY || e.getY() > (offsetY + effectiveSizeY) ||
				  !segmentSelectionAllowed
			) {
				return;
			}
			
			int posX = e.getX() - offsetX;
			int posY = e.getY() - offsetY;
			
			posX /= (pixelSize + 1);
			posY /= (pixelSize + 1);
			
			if(spp > 1) {
				posX = (posX - 1) * spp;
				posY = (posY - 1) * spp;
			}
			final SegmentSelectionEvent evt = new SegmentSelectionEvent(posX, posY);
			for(ISegmentSelectionListener listener: segmentSelectionListeners) {
				listener.segmentSelected(evt);
			}
		}
	}
}
