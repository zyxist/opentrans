/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.editing.operations;

import com.google.common.eventbus.EventBus;
import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.common.WorldResizeCmd;
import org.invenzzia.opentrans.visitons.exception.WorldException;

/**
 * Extends the world in the given direction.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Extend world")
public class ExtendWorldCmd extends WorldResizeCmd {
	public ExtendWorldCmd(int direction) {
		super(direction);
	}

	@Override
	public void execute(Project project, EventBus eventBus) throws Exception {
		this.performExtend(project.getWorld(), eventBus, this.direction);
	}

	@Override
	public void undo(Project project, EventBus eventBus) {
		try {
			this.performShrink(project.getWorld(), eventBus, this.direction);
		} catch(WorldException exception) {
			throw new IllegalStateException("Undo operation failed unexpectedly.", exception);
		}
	}

	@Override
	public void redo(Project project, EventBus eventBus) {
		this.performExtend(project.getWorld(), eventBus, this.direction);
	}
}
