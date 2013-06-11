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
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Common code for all actions that operate on vehicles.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractVehicleAction implements IPopupAction {
	@Inject
	protected IDialogBuilder dialogBuilder;
	@Inject
	protected History<ICommand> history;
	/**
	 * The vehicle record to update.
	 */
	private VehicleRecord vehicleRecord;
	
	/**
	 * This method shall be called before activating the popup.
	 * 
	 * @param record 
	 */
	public void setVehicleRecord(VehicleRecord record) {
		this.vehicleRecord = record;
	}
	
	@Override
	public void execute(IEditModeAPI api, double x, double y) {
		if(null == this.vehicleRecord) {
			this.dialogBuilder.showError("Internal error", "Attempt to initialize a vehicle dialog without a vehicle.");
		} else {
			this.doExecute(api, this.vehicleRecord);
		}
		this.vehicleRecord = null;
	}
	
	/**
	 * Action code goes here.
	 * 
	 * @param api
	 * @param record 
	 */
	protected abstract void doExecute(IEditModeAPI api, VehicleRecord record);
}
