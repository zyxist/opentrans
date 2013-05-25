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

package org.invenzzia.opentrans.lightweight.model.lists;

import org.invenzzia.opentrans.lightweight.model.EntityListModel;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.data.manager.MeanOfTransportManager;

/**
 * Data model for the item list that contains means of transport.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MeanOfTransportModel extends EntityListModel<MeanOfTransport, MeanOfTransportRecord, MeanOfTransportManager> {
	@Override
	protected MeanOfTransportManager getDataManager(Project project) {
		return project.getMeanOfTransportManager();
	}

	@Override
	protected MeanOfTransportRecord createRecord() {
		return new MeanOfTransportRecord();
	}
}
