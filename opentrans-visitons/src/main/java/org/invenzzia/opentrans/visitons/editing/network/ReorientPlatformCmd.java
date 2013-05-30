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

package org.invenzzia.opentrans.visitons.editing.network;

import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.Platform;
import org.invenzzia.opentrans.visitons.data.Platform.PlatformRecord;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.editing.common.AbstractReorientObjectCmd;

/**
 * Allows changing the orientation of the platform.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ReorientPlatformCmd extends AbstractReorientObjectCmd<PlatformRecord, Platform> {
	public ReorientPlatformCmd(PlatformRecord record) {
		super(record, "Change platform orientation in stop '"+record.getStop().getName()+"'");
	}

	@Override
	protected byte advanceOrientation(byte oldOrientation) {
		return (byte)((oldOrientation + 1) % 2);
	}

	@Override
	protected Platform getEntity(Project project) {
		Stop stop = project.getStopManager().findById(this.record.getStop().getId());
		return stop.getPlatform(this.record.getNumber());
	}
}
