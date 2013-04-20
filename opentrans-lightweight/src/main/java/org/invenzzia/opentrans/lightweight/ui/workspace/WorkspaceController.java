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

package org.invenzzia.opentrans.lightweight.ui.workspace;

import org.invenzzia.opentrans.lightweight.ui.tabs.world.WorldTab;
import com.google.inject.Inject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import org.invenzzia.opentrans.lightweight.ui.component.IconButton;
import org.invenzzia.opentrans.lightweight.ui.minimap.MinimapController;
import org.invenzzia.opentrans.lightweight.ui.tabs.*;
import org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure.InfrastructureTab;
import org.invenzzia.opentrans.lightweight.ui.tabs.vehicles.VehicleTab;

/**
 * A controller for responding for events coming from the workspace.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorkspaceController implements ComponentListener, ActionListener {
	@Inject
	private DesktopManager desktopManager;
	@Inject
	private MinimapController minimapController;
	
	private WorkspacePanel workspace;
	
	public void setWorkspace(WorkspacePanel workspace) {
		if(null != this.workspace) {
			this.workspace.getWorkspaceSplitter().removeComponentListener(this);
			this.minimapController.setView(null);
			this.workspace.removeButtonListener(this);
		}
		this.workspace = workspace;
		if(null != this.workspace) {
			this.workspace.addButtonListener(this);
			this.workspace.getWorkspaceSplitter().addComponentListener(this);
			this.minimapController.setView(this.workspace.getMinimap());
		}
	}
	

	@Override
	public void componentResized(ComponentEvent e) {
		// Ensure that the divider stays on the right during the resizing.
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src instanceof IconButton) {
			switch(((IconButton)src).getName()) {
				case "project":
					this.desktopManager.setFocus(ProjectTab.class);
					break;
				case "world":
					this.desktopManager.setFocus(WorldTab.class);
					break;
				case "infrastructure":
					this.desktopManager.setFocus(InfrastructureTab.class);
					break;
				case "vehicles":
					this.desktopManager.setFocus(VehicleTab.class);
					break;
			}
		}
	}
}
