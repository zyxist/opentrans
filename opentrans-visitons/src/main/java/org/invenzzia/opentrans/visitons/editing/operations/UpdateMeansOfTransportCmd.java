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
import org.invenzzia.helium.data.UnitOfWork;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.data.manager.MeanOfTransportManager;
import org.invenzzia.opentrans.visitons.editing.common.AbstractUnitOfWorkCmd;

/**
 * Atomic history operation that provides a batch update of all means of
 * transport available within the project. Note that the remove can be safely
 * performed only on means of transport that do not have vehicle types assigned.
 * 
 * @param R The type of records stored in the data manager.
 * @author Tomasz Jędrzejewski
 */
@CommandDetails(name = "Update means of transport")
public class UpdateMeansOfTransportCmd extends AbstractUnitOfWorkCmd<MeanOfTransport, MeanOfTransportRecord, MeanOfTransportManager> {

	/**
	 * Creates a new command that would replay the contents of the specified unit of work.
	 * 
	 * @param unitOfWork 
	 */
	public UpdateMeansOfTransportCmd(UnitOfWork<MeanOfTransportRecord> unitOfWork) {
		super(unitOfWork);
	}

	@Override
	protected MeanOfTransportManager getManager(Project project) {
		return project.getMeanOfTransportManager();
	}

	@Override
	protected MeanOfTransport createNewDataObject() {
		return new MeanOfTransport();
	}
}
