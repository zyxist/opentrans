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
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.editing.network.RenamePlatformCmd;

/**
 * The action allows renaming the platform.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Rename platform")
public class RenamePlatformAction extends AbstractPlatformAction implements IPopupAction {
	@Override
	protected void doExecute(IEditModeAPI api, PlatformRecord record) {
		PlatformRenameDialog dialog = this.dialogBuilder.createModalDialog(PlatformRenameDialog.class);
		dialog.setModel(record);
		dialog.setVisible(true);
		if(dialog.isConfirmed()) {
			try {
				this.history.execute(new RenamePlatformCmd(dialog.getPlatformName(), record));
			} catch(CommandExecutionException exception) {
				this.dialogBuilder.showError("Cannot rename", exception);
			}
		}
		dialog.dispose();
	}
}
