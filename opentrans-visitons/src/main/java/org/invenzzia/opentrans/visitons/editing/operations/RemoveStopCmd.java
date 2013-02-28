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

import org.invenzzia.helium.annotations.CommandDetails;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Stop.StopRecord;
import org.invenzzia.opentrans.visitons.data.manager.StopManager;
import org.invenzzia.opentrans.visitons.editing.common.AtomicRemoveCmd;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Remove stop")
public class RemoveStopCmd extends AtomicRemoveCmd<Stop, StopRecord, StopManager> {
	public RemoveStopCmd(Stop.StopRecord record) {
		super(record);
	}
	
	@Override
	protected StopManager getManager(Project project) {
		return project.getStopManager();
	}
}
