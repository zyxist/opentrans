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

import java.awt.Cursor;
import org.invenzzia.opentrans.visitons.network.WorldRecord;

/**
 * Callback methods from the controller that can be used in the edit modes.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IEditModeAPI {
	/**
	 * Provides snapshot of the world data.
	 * @return 
	 */
	public WorldRecord getWorldRecord();
	/**
	 * Allows changing the shape of the cursor.
	 * 
	 * @param cursor The new cursor to draw.
	 */
	public void setCursor(Cursor cursor);
	/**
	 * Forwards a status message to the status bar with the hint for the user.
	 * 
	 * @param message Message content.
	 */
	public void setStatusMessage(String message);
	/**
	 * Installs a pop-up menu for the edit mode. The method shall be used, when the
	 * mode is enabled. The popup is removed automatically, when the mode that set it
	 * is disabled.
	 * 
	 * @param builder 
	 */
	public void setPopup(PopupBuilder builder);
	/**
	 * Shows the pop-up menu.
	 */
	public void showPopup();
}
