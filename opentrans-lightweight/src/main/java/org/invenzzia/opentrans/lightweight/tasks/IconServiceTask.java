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

package org.invenzzia.opentrans.lightweight.tasks;

import com.google.inject.Inject;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;

/**
 * Creates an icon service and preloads some icons.
 * 
 * @author Tomasz Jędrzejewski
 */
public class IconServiceTask implements ITask {
	private IconService iconService;
	
	@Inject
	public IconServiceTask(IconService iconService) {
		this.iconService = iconService;
	}

	@Override
	public void startup() {
		ClassLoader currentLoader = this.getClass().getClassLoader();
		
		this.iconService.preloadIcon("document-new", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/big/document-new.png"));
		this.iconService.preloadIcon("document-open", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/big/document-open.png"));
		this.iconService.preloadIcon("document-save", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/big/document-save.png"));
		this.iconService.preloadIcon("edit-undo", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/big/edit-undo.png"));
		this.iconService.preloadIcon("edit-redo", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/big/edit-redo.png"));
		
		// Medium tool icons
		this.iconService.preloadIcon("edit-select", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/edit-select.png"));
		this.iconService.preloadIcon("draw-freehand", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/draw-freehand.png"));
		this.iconService.preloadIcon("connect-tracks", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/connect-tracks.png"));
		this.iconService.preloadIcon("convert-to-straight", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/convert-to-straight-track.png"));
		this.iconService.preloadIcon("convert-to-curve", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/convert-to-curved-track.png"));
		this.iconService.preloadIcon("convert-to-free", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/convert-to-free-track.png"));
		this.iconService.preloadIcon("stops", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/medium/stops.png"));
		
		// Small utility icons
		this.iconService.preloadIcon("ui-close-small", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/small/ui-close.png"));
		
		// Load bigger icons
		this.iconService.preloadIcon("project-project", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/applications-engineering.png"));
		this.iconService.preloadIcon("project-world", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/applications-internet.png"));
		this.iconService.preloadIcon("project-infrastructure", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/tram.png"));
		this.iconService.preloadIcon("project-vehicles", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/system-software-update.png"));
		this.iconService.preloadIcon("project-timetable", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/view-time-schedule.png"));
		this.iconService.preloadIcon("project-assignments", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/resource-group.png"));
		this.iconService.preloadIcon("project-passenger", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/meeting-chair.png"));
		this.iconService.preloadIcon("project-stats", currentLoader.getResource("org/invenzzia/opentrans/gui/icons/bigger/office-chart-area.png"));
	}

	@Override
	public void shutdown() {
	}
}
