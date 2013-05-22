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

import org.invenzzia.helium.exception.CommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mode allows changing the track type of a track to the curve.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConvertToCurveMode extends AbstractEditMode {
	private final Logger logger = LoggerFactory.getLogger(ConvertToCurveMode.class);

	@Override
	public void modeEnabled(IEditModeAPI api) {
		logger.debug("ConnectTracksMode enabled.");
	
	}

	@Override
	public void modeDisabled() {
		logger.debug("ConnectTracksMode disabled.");
	}
	
	@Override
	protected void handleCommandExecutionError(CommandExecutionException exception) {
		logger.error("Exception occurred while saving the network unit of work.", exception);
	}
}
