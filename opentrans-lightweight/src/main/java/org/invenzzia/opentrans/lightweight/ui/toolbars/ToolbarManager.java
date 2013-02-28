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

package org.invenzzia.opentrans.lightweight.ui.toolbars;

import com.google.common.base.Preconditions;
import com.google.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import net.jcip.annotations.NotThreadSafe;

/**
 * Keeps references to all the toolbars available in the application and their current state.
 * This service shall be used by the Swing event dispatch thread.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
@NotThreadSafe
public class ToolbarManager {
	/**
	 * Keeps the list of all the toolbars.
	 */
	private Map<Class<? extends AbstractToolbar>, AbstractToolbar> toolbars;
	/**
	 * The panel that is used for displaying the toolbars.
	 */
	private JPanel toolbarPanel;
	
	public ToolbarManager() {
		this.toolbars = new LinkedHashMap<>();
	}

	/**
	 * Adds a new managed toolbar.
	 * 
	 * @param toolbar 
	 */
	public void addToolbar(AbstractToolbar toolbar) {
		Preconditions.checkNotNull(toolbar);
		this.toolbars.put(toolbar.getClass(), toolbar);
	}
	
	/**
	 * Returns the toolbar object using its class as a key.
	 * 
	 * @param toolbarKey
	 * @return Toolbar object.
	 */
	public <T extends AbstractToolbar> T getToolbar(Class<T> toolbarKey) {
		return (T) this.toolbars.get(toolbarKey);
	}
	
	/**
	 * Sets the panel that would display the toolbars.
	 * 
	 * @param toolbarPanel 
	 */
	public void setToolbarPanel(JPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
	}
	
	/**
	 * Returns the panel that displays the toolbars.
	 * 
	 * @return 
	 */
	public JPanel getToolbarPanel() {
		return this.toolbarPanel;
	}
	
	/**
	 * Updates the specified toolbar panel to display all the active
	 * toolbars.
	 * 
	 * @param toolbarPanel Toolbar panel to update.
	 */
	public void update(JPanel toolbarPanel) {
		Preconditions.checkNotNull(toolbarPanel, "The toolbar panel to update is empty.");
		toolbarPanel.removeAll();
		
		for(AbstractToolbar toolbar: this.toolbars.values()) {
			if(toolbar.isActive()) {
				toolbarPanel.add(toolbar);
			}
		}
		toolbarPanel.revalidate();
	}
	
	/**
	 * Updates the default toolbar panel to display all the active
	 * toolbars.
	 */
	public void update() {
		this.update(this.toolbarPanel);
	}
}
