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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * The main component for drawing the network view with all the eyecandy
 * stuff.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkView extends JPanel {
	/**
	 * Ruler displayed on the top.
	 */
	private Ruler horizontalRuler;
	/**
	 * Ruler displayed on the left.
	 */
	private Ruler verticalRuler;
	/**
	 * Horizontal scroll bar for the camera.
	 */
	private JScrollBar horizontalBar;
	/**
	 * Vertical scroll bar for the camera.
	 */
	private JScrollBar verticalBar;
	/**
	 * Displays the image from the renderer.
	 */
	private CameraView cameraView;
	/**
	 * Snapshot of the data from the camera.
	 */
	private CameraModelSnapshot snapshot;
	
	public NetworkView() {
		super(new GridBagLayout());
		this.horizontalRuler = new Ruler(Ruler.HORIZONTAL);
		this.horizontalRuler.setPreferredSize(600);
		
		this.verticalRuler = new Ruler(Ruler.VERTICAL);
		this.verticalRuler.setPreferredSize(600);

		this.horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL);
		this.horizontalBar.setMinimum(0);
		this.horizontalBar.setPreferredSize(new Dimension(600, 18));
		
		this.verticalBar = new JScrollBar(JScrollBar.VERTICAL);
		this.verticalBar.setMinimum(0);
		this.verticalBar.setPreferredSize(new Dimension(18, 600));
		
		this.updateScrollbars();
		
		this.cameraView = new CameraView();
		this.cameraView.setPreferredSize(new Dimension(1200, 700));
		
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		this.add(this.horizontalRuler, c);
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		this.add(this.horizontalBar, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		this.add(this.verticalRuler, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(this.verticalBar, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(this.cameraView, c);
	}
	
	/**
	 * Returns the camera view used by this network view.
	 * 
	 * @return The component camera view.
	 */
	public CameraView getCameraView() {
		return this.cameraView;
	}
	
	/**
	 * Injects the new data snapshot into the subcomponents and updates the scrollbar
	 * information.
	 * 
	 * @param snapshot The updated camera snapshot.
	 */
	public void injectSnapshot(CameraModelSnapshot snapshot) {
		this.snapshot = snapshot;
		this.cameraView.injectSnapshot(snapshot);
		this.horizontalRuler.injectSnapshot(snapshot);
		this.verticalRuler.injectSnapshot(snapshot);
		this.horizontalRuler.repaint();
		this.verticalRuler.repaint();
		this.updateScrollbars();
	}
	
	/**
	 * Updates the scrollbar information.
	 */
	public final void updateScrollbars() {
		if(null != this.snapshot) {
			this.horizontalBar.setMaximum((int) this.snapshot.getSizeX());
			this.verticalBar.setMaximum((int) this.snapshot.getSizeY());

			this.horizontalBar.setVisibleAmount((int) this.snapshot.getViewportWidth());
			this.verticalBar.setVisibleAmount((int) this.snapshot.getViewportHeight());
		} else {
			this.horizontalBar.setMaximum(1000);
			this.verticalBar.setMaximum(1000);

			this.horizontalBar.setVisibleAmount(1000);
			this.verticalBar.setVisibleAmount(1000);
		}
	}
	
	public final void updateScrollbarPositions() {
		if(null != this.snapshot) {
			this.horizontalBar.setValue((int) this.snapshot.getPosX());
			this.verticalBar.setValue((int) this.snapshot.getPosY());
		}
	}
	
	/**
	 * Return tyhe current value of the horizontal scroll bar.
	 * 
	 * @return Scroll bar value
	 */
	public int getHorizontalScrollBarValue() {
		return this.horizontalBar.getValue();
	}
	
	/**
	 * Return the current value of the vertical scroll bar.
	 * 
	 * @return Scroll bar value
	 */
	public int getVerticalScrollBarValue() {
		return this.verticalBar.getValue();
	}
	
	/**
	 * Registers the listener that listens for the scoll bars adjustment changes.
	 * 
	 * @param listener 
	 */
	public void addAdjustmentListener(AdjustmentListener listener) {
		this.horizontalBar.addAdjustmentListener(listener);
		this.verticalBar.addAdjustmentListener(listener);
	}
	
	/**
	 * Removes the adjustment listener from scollbars.
	 * @param listener 
	 */
	public void removeAdjustmentListener(AdjustmentListener listener) {
		this.horizontalBar.removeAdjustmentListener(listener);
		this.verticalBar.removeAdjustmentListener(listener);
	}
}
