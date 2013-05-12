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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The operations of more complex edit modes can be described by state machines.
 * This class helps implementing edit modes as state machines. When the mode
 * is enabled, the implementor is responsible for choosing the initial mode.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract  class AbstractStateMachineEditMode extends AbstractTrackModeAPI implements IEditMode {
	private final Logger logger = LoggerFactory.getLogger(AbstractStateMachineEditMode.class);
	/**
	 * Current machine state.
	 */
	private IEditState currentState;
	
	/**
	 * Implementations shall use this method to advance the machine to the new
	 * state. This affects all the methods inherited from {@link IEditState}
	 * to be delegated to the new state.
	 * 
	 * @param newState New state
	 */
	public void setState(IEditState newState) {
		this.currentState = Preconditions.checkNotNull(newState, "The edit state cannot be NULL.");
		if(logger.isDebugEnabled()) {
			logger.debug("Edit state changed to: "+newState.getClass().getSimpleName());
		}
	}
	
	/**
	 * @return Returns the current edit state.
	 */
	public IEditState getState() {
		return this.currentState;
	}

	@Override
	public boolean captureMotionEvents() {
		return this.currentState.captureMotionEvents();
	}

	@Override
	public boolean captureDragEvents() {
		return this.currentState.captureDragEvents();
	}

	@Override
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.currentState.mouseMoves(worldX, worldY, altDown, ctrlDown);
	}
	
	@Override
	public void mouseDrags(double worldX, double worldY, double deltaX, double deltaY, boolean altDown, boolean ctrlDown) {
		this.currentState.mouseDrags(worldX, worldY, deltaX, deltaY, altDown, ctrlDown);
	}

	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.currentState.leftActionPerformed(worldX, worldY, altDown, ctrlDown);
	}

	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
		this.currentState.rightActionPerformed(worldX, worldY, altDown, ctrlDown);
	}
	
	@Override
	public void deletePressed(double worldX, double worldY) {
		this.currentState.deletePressed(worldX, worldY);
	}
}
