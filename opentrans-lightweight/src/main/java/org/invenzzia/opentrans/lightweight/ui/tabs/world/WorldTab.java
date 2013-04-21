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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField;
import org.invenzzia.opentrans.lightweight.ui.netview.NetworkView;

/**
 * Draws the contents of the world tab: network view, and the mini-toolbar for
 * the edit tools.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldTab extends JPanel {
	/**
	 * Edit mode: selecting points, moving them, etc.
	 */
	public static final int MODE_SELECTION = 0;
	/**
	 * Edit mode: drawing new tracks.
	 */
	public static final int MODE_TRACK_DRAW = 1;
	
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
	/**
	 * Edit mode buttons.
	 */
	private JToggleButton modeButtons[];
	/**
	 * Current edit mode.
	 */
	private int selectedMode = MODE_SELECTION;
	/**
	 * Event listeners for world tab-specific events.
	 */
	private Set<IWorldTabListener> listeners;
	
	public WorldTab() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.listeners = new LinkedHashSet<>();
		
		this.zoomField = new ZoomField();
		this.createModeButtons();
		
		this.worldToolbar = new JPanel();
		this.worldToolbar.setMinimumSize(new Dimension(35, 35));
		this.worldToolbar.setPreferredSize(new Dimension(500, 35));
		this.worldToolbar.setLayout(new BoxLayout(this.worldToolbar, BoxLayout.X_AXIS));
		this.worldToolbar.add(Box.createHorizontalStrut(20));
		this.worldToolbar.add(this.zoomField);
		this.worldToolbar.add(Box.createHorizontalStrut(20));
		for(int i = 0; i < this.modeButtons.length; i++) {
			this.worldToolbar.add(this.modeButtons[i]);
		}
		this.worldToolbar.add(Box.createHorizontalGlue());
		
		this.networkView = new NetworkView();
		this.add(this.worldToolbar);
		this.add(this.networkView);
	}
	
	/**
	 * Imports the button icons from the icon service.
	 * 
	 * @param iconService 
	 */
	public void importIcons(IconService iconService) {
		this.modeButtons[0].setIcon(iconService.getIcon("edit-select"));
		this.modeButtons[1].setIcon(iconService.getIcon("draw-freehand"));
	}
	
	/**
	 * Registers a new listener for world tab events.
	 * 
	 * @param listener 
	 */
	public void addWorldTabListener(IWorldTabListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes an existing listener for world tab events.
	 * 
	 * @param listener 
	 */
	public void removeWorldTabListener(IWorldTabListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Clears the set of world tab listeners.
	 */
	public void removeWorldTabListeners() {
		this.listeners.clear();
	}
	
	/**
	 * Returns the index of the selected mode.
	 * 
	 * @return Selected mode.
	 */
	public int getSelectedMode() {
		return this.selectedMode;
	}
	
	public NetworkView getNetworkView() {
		return this.networkView;
	}
	
	public ZoomField getZoomField() {
		return this.zoomField;
	}
	
	private void createModeButtons() {
		this.modeButtons = new JToggleButton[2];
		this.modeButtons[0] = new JToggleButton();
		this.modeButtons[0].setToolTipText("Select vertices and tracks");
		this.modeButtons[1] = new JToggleButton();
		this.modeButtons[1].setToolTipText("Draw new tracks");
		for(int i = 0; i < this.modeButtons.length; i++) {
			this.createToggleButtonAction(this.modeButtons[i], i);
			this.modeButtons[i].setSize(35, 35);
		}
		this.modeButtons[0].setSelected(true);
	}
	
	private void createToggleButtonAction(final JToggleButton button, final int mode) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(JToggleButton btn: modeButtons) {
					if(btn != button) {
						btn.setSelected(false);
					}
				}
				selectedMode = mode;
				final WorldTabEvent event = new WorldTabEvent(mode);
				for(IWorldTabListener listener: listeners) {
					listener.modeChanged(event);
				}
			}
		});
	}
	
	/**
	 * Listener that allows receiving notifications about world tab state changed.
	 */
	public static interface IWorldTabListener {
		public void modeChanged(WorldTabEvent event);
	}
	
	/**
	 * Carries information about the taken action.
	 */
	public static class WorldTabEvent {
		private final int mode;
		
		public WorldTabEvent(int mode) {
			this.mode = mode;
		}

		/**
		 * @return Current edit mode.
		 */
		public int getMode() {
			return this.mode;
		}
	}
}
