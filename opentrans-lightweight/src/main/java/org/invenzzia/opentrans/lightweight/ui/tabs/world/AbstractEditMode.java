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

/**
 * Provides default implementations for most of the methods in {@link IEditMode}.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractEditMode extends AbstractTrackModeAPI implements IEditMode {

	@Override
	public boolean captureMotionEvents() {
		return false;
	}

	@Override
	public boolean captureDragEvents() {
		return false;
	}

	@Override
	public void mouseMoves(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
	}
	
	@Override
	public void leftActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
	}

	@Override
	public void rightActionPerformed(double worldX, double worldY, boolean altDown, boolean ctrlDown) {
	}
	
	@Override
	public void deletePressed(double worldX, double worldY) {
	}
}
