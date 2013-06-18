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
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.ClearBitmapCmd;
import org.invenzzia.opentrans.visitons.network.Segment;

/**
 * Allows clearing the background bitmap.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Clear bitmap")
public class ClearBitmapAction implements IPopupAction {
	@Inject
	private History<ICommand> history;
	@Inject
	private IDialogBuilder dialogBuilder;

	@Override
	public void execute(IEditModeAPI api, double x, double y) {
		try {
			this.history.execute(new ClearBitmapCmd(
				(int) Math.floor(x / Segment.SIZE_D),
				(int) Math.floor(y / Segment.SIZE_D)
			));
			api.setStatusMessage("Bitmap loaded.");
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Error while clearing the bitmap", exception);
		}
	}
}
