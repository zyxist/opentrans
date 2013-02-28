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
import org.invenzzia.opentrans.lightweight.annotations.ToolbarAction;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;

/**
 * This toolbar shows 'Undo' and 'Redo' buttons.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryToolbar extends AbstractToolbar {
	@ToolbarAction("undo")
	private JButton undoButton;
	@ToolbarAction("redo")
	private JButton redoButton;

	public HistoryToolbar() {
		super();
		this.initProperties(2);
		this.initComponents();
	}
	
	/**
	 * Initializes the toolbar buttons.
	 */
	private void initComponents() {
		this.undoButton = new JButton();
		this.undoButton.setPreferredSize(new Dimension(40, 40));
		
		this.redoButton = new JButton();
		this.redoButton.setPreferredSize(new Dimension(40, 40));
		
		this.add(this.undoButton);
		this.add(this.redoButton);
	}
	
	/**
	 * Imports the button icons from the icon service.
	 * 
	 * @param iconService 
	 */
	public void importIcons(IconService iconService) {
		this.undoButton.setIcon(iconService.getIcon("edit-undo"));
		this.redoButton.setIcon(iconService.getIcon("edit-redo"));
	}
	
	/**
	 * Allows enabling or disabling the 'undo' button.
	 * 
	 * @param enabled 
	 */
	public void setUndoEnabled(boolean enabled) {
		this.undoButton.setEnabled(enabled);
	}
	
	/**
	 * Allows enabling or disabling the 'redo' button.
	 * 
	 * @param enabled 
	 */
	public void setRedoEnabled(boolean enabled) {
		this.redoButton.setEnabled(enabled);
	}
	
	@Override
	public String getToolbarName() {
		return "History toolbar";
	}
}
