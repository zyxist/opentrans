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

package org.invenzzia.opentrans.lightweight.ui.tabs;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField;
import org.invenzzia.opentrans.lightweight.ui.netview.NetworkView;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldTab extends JPanel {
	/**
	 * Editor view that renders the network.
	 */
	private NetworkView networkView;
	/**
	 * Toolbar with extra buttons etc.
	 */
	private JPanel worldToolbar;
	/**
	 * Selects the zoom level.
	 */
	private ZoomField zoomField;
	
	public WorldTab() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		this.zoomField = new ZoomField();
		
		this.worldToolbar = new JPanel();
		this.worldToolbar.setMinimumSize(new Dimension(35, 35));
		this.worldToolbar.setPreferredSize(new Dimension(500, 35));
		this.worldToolbar.setLayout(new BoxLayout(this.worldToolbar, BoxLayout.X_AXIS));
		this.worldToolbar.add(Box.createHorizontalStrut(20));
		this.worldToolbar.add(this.zoomField);
		this.worldToolbar.add(Box.createHorizontalGlue());
		
		this.networkView = new NetworkView();
		this.add(this.worldToolbar);
		this.add(this.networkView);
	}
	
	public NetworkView getNetworkView() {
		return this.networkView;
	}
	
	public ZoomField getZoomField() {
		return this.zoomField;
	}
}
