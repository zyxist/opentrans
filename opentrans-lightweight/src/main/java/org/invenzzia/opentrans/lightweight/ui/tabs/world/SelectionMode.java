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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selection mode allows selecting group of objects and moving them.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(SelectionMode.class);
	
	@Override
	public void modeEnabled() {
		logger.debug("SelectionMode enabled.");
	}

	@Override
	public void modeDisabled() {
		logger.debug("SelectionMode disabled.");
	}
}
