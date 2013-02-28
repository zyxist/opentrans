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

package org.invenzzia.opentrans.lightweight.ui.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;
import org.invenzzia.opentrans.lightweight.ui.workspace.WorkspacePanel;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorkspacePanelProvider implements Provider<WorkspacePanel> {
	private IconService iconService;
	
	@Inject
	public WorkspacePanelProvider(IconService iconService) {
		this.iconService = iconService;
	}
	
	@Override
	public WorkspacePanel get() {
		WorkspacePanel panel = new WorkspacePanel();
		panel.setIconService(this.iconService);
		return panel;
	}
}
