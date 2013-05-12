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

import java.awt.Dimension;
import javax.swing.JButton;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;

/**
 * A view of the toolbar with buttons for managing the project.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectToolbar extends AbstractToolbar {
	/**
	 * Button for starting a new project.
	 */
	private JButton newProjectButton;
	/**
	 * Button for opening a project.
	 */
	private JButton openProjectButton;
	/**
	 * Button for saving the project.
	 */
	private JButton saveProjectButton;
	
	public ProjectToolbar() {
		super();
		this.initProperties(3);
		this.initComponents();
	}
	
	@Override
	public String getToolbarName() {
		return "Project toolbar";
	}
	
	@Override
	public String getToolbarPreferenceKey() {
		return "opentrans.gui.toolbar.project";
	}
	
	/**
	 * Initializes the toolbar components.
	 */
	private void initComponents() {
		this.newProjectButton = new JButton();
		this.newProjectButton.setPreferredSize(new Dimension(40, 40));
		this.openProjectButton = new JButton();
		this.openProjectButton.setPreferredSize(new Dimension(40, 40));
		this.saveProjectButton = new JButton();
		this.saveProjectButton.setPreferredSize(new Dimension(40, 40));
		
		this.add(this.newProjectButton);
		this.add(this.openProjectButton);
		this.add(this.saveProjectButton);
	}

	/**
	 * Sets the icons for all the toolbar buttons.
	 * 
	 * @param iconService 
	 */
	public void importIcons(IconService iconService) {
		this.newProjectButton.setIcon(iconService.getIcon("document-new"));
		this.openProjectButton.setIcon(iconService.getIcon("document-open"));
		this.saveProjectButton.setIcon(iconService.getIcon("document-save"));
	}
}
