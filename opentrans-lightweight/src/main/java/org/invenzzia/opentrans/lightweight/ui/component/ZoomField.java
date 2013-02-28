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

package org.invenzzia.opentrans.lightweight.ui.component;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 * A simple component for displaying the zoom editor: a text field with the current zoom,
 * and a drop-down list that allows selecting default levels.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ZoomField extends JPanel {
	private static final Pattern ZOOM_PATTERN = Pattern.compile("^([0-9]+)\\%?$");
	
	private JTextField textField;
	private JButton button;
	/**
	 * Current zoom level.
	 */
	private int zoomLevel = 100;
	/**
	 * List of permitted zoom levels.
	 */
	private int zoomLevels[] = { 10, 25, 50, 100, 200, 400, 800 };
	/**
	 * Logic responsible for handling the popup menu with the zoom levels.
	 */
	private SelectionLogic selectionLogic;
	/**
	 * Set of zoom listeners.
	 */
	private Set<IZoomListener> zoomListeners;
	
	public ZoomField() {
		this.zoomListeners = new LinkedHashSet<>();
		this.selectionLogic = new SelectionLogic();
		this.textField = new JTextField("100%");
		this.textField.setMinimumSize(new Dimension(140, 35));
		this.textField.setSize(new Dimension(140, 35));
		this.textField.setMaximumSize(new Dimension(140, 35));
		this.button = new JButton("▼");
		this.button.setSize(new Dimension(35, 35));
		this.button.setMaximumSize(new Dimension(35, 35));
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.button.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				JPopupMenu popup = new JPopupMenu();
				for(int zl: zoomLevels) {
					JMenuItem item = new JMenuItem(displayZoomLevel(zl));
					item.addActionListener(selectionLogic);
					popup.add(item);
				}
				Point location = button.getLocationOnScreen();
				popup.show(button, 0, button.getHeight());
			}
		});
		
		this.textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newZoomLevel = parseZoomLevel(textField.getText());
				if(newZoomLevel != -1) {
					setZoomLevel(newZoomLevel);
				}
			}
		});
		
		this.add(this.textField);
		this.add(this.button);
	}
	
	/**
	 * Adds a zoom change listener.
	 * 
	 * @param listener 
	 */
	public void addZoomListener(IZoomListener listener) {
		this.zoomListeners.add(listener);
	}
	
	/**
	 * Removes the zoom change listener.
	 * 
	 * @param listener 
	 */
	public void removeZoomListener(IZoomListener listener) {
		this.zoomListeners.remove(listener);
	}
	
	/**
	 * Clears the zoom change listeners in this component.
	 */
	public void removeZoomListeners() {
		this.zoomListeners.clear();
	}
	
	/**
	 * Returns the copy of the default zoom level array.
	 * 
	 * @return Default zoom levels.
	 */
	public int[] getZoomLevels() {
		int zl[] = new int[this.zoomLevels.length];
		System.arraycopy(this.zoomLevels, 0, zl, 0, this.zoomLevels.length);
		return zl;
	}
	
	/**
	 * Sets the default zoom levels. The passed array must not be modified anymore.
	 * 
	 * @param zoomLevels Array of default zoom levels.
	 */
	public void setZoomLevels(int zoomLevels[]) {
		this.zoomLevels = zoomLevels;
	}
	
	/**
	 * Returns the current zoom level.
	 * 
	 * @return Current zoom level.
	 */
	public int getZoomLevel() {
		return this.zoomLevel;
	}
	
	/**
	 * Selects a new zoom level. The value is in percent.
	 * 
	 * @param zoomLevel New zoom level.
	 */
	public void setZoomLevel(int zoomLevel) {
		if(zoomLevel >= 1) {
			this.zoomLevel = zoomLevel;
			this.textField.setText(this.displayZoomLevel(zoomLevel));
			ZoomChangeEvent event = new ZoomChangeEvent(zoomLevel, this);
			for(IZoomListener listener: this.zoomListeners) {
				listener.zoomLevelChanged(event);
			}
		}
	}
	
	private String displayZoomLevel(int zl) {
		return zl+"%";
	}
	
	private int parseZoomLevel(String str) {
		Matcher m = ZOOM_PATTERN.matcher(str);
		if(m.matches()) {
			return Integer.parseInt(m.group(1));
		}
		return -1;
	}
	
	/**
	 * Allows listening for zoom change events.
	 */
	public static interface IZoomListener {
		/**
		 * Notifies that the zoom level has been changed.
		 * 
		 * @param event Zoom change event.
		 */
		public void zoomLevelChanged(ZoomChangeEvent event);
	}
	
	/**
	 * Notification that the zoom level has been changed.
	 */
	public static class ZoomChangeEvent {
		private final int zoomLevel;
		private final ZoomField component;
		
		ZoomChangeEvent(int newZoomLevel, ZoomField component) {
			this.zoomLevel = newZoomLevel;
			this.component = component;
		}

		/**
		 * Returns the new zoom level.
		 * 
		 * @return New zoom level.
		 */
		public int getZoomLevel() {
			return this.zoomLevel;
		}

		/**
		 * Returns the component that fired the event.
		 * 
		 * @return Component that fired the event.
		 */
		public ZoomField getComponent() {
			return this.component;
		}
	}
	
	/**
	 * Handles selecting a zoom level.
	 */
	class SelectionLogic implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			setZoomLevel(parseZoomLevel(item.getText()));
		}
	}
}
