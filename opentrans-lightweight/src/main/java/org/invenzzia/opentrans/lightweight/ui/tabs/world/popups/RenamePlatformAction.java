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

import com.google.inject.Inject;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.PopupAction;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.network.RenamePlatformCmd;

/**
 * The action allows renaming the platform.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Rename platform")
public class RenamePlatformAction implements IPopupAction {
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private History<ICommand> history;
	/**
	 * The platform record to update.
	 */
	private PlatformRecord platformRecord;
	
	/**
	 * This method shall be called before activating the popup.
	 * @param record 
	 */
	public void setPlatformRecord(PlatformRecord record) {
		this.platformRecord = record;
	}
	
	@Override
	public void execute(IEditModeAPI api, double x, double y) {
		if(null == this.platformRecord) {
			this.dialogBuilder.showError("Internal error", "Attempt to initialize a platform rename dialog without a platform record.");
		} else {
			PlatformRenameDialog dialog = this.dialogBuilder.createModalDialog(PlatformRenameDialog.class);
			dialog.setModel(this.platformRecord);
			dialog.setVisible(true);
			if(dialog.isConfirmed()) {
				try {
					this.history.execute(new RenamePlatformCmd(dialog.getPlatformName(), this.platformRecord));
				} catch(CommandExecutionException exception) {
					this.dialogBuilder.showError("Cannot rename", exception);
				}
			}
			dialog.dispose();
		}
		this.platformRecord = null;
	}
}
