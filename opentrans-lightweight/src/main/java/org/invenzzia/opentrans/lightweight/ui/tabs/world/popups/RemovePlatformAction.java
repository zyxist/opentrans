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

package org.invenzzia.opentrans.lightweight.ui.tabs.world.popups;

import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.opentrans.lightweight.annotations.PopupAction;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.editing.network.RemovePlatformCmd;

/**
 * Popup menu action for removing a platform.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Remove platform")
public class RemovePlatformAction extends AbstractPlatformAction {
	@Override
	protected void doExecute(IEditModeAPI api, PlatformRecord record) {
		try {
			this.history.execute(new RemovePlatformCmd(record));
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot remove", exception);
		}
	}
}
