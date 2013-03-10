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

package org.invenzzia.opentrans.visitons.data.manager;

import com.google.common.base.Preconditions;
import net.jcip.annotations.NotThreadSafe;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;

/**
 * Manages the list of means of transport within the project.
 * 
 * @author Tomasz Jędrzejewski
 */
@NotThreadSafe
public class MeanOfTransportManager extends AbstractDataManager<MeanOfTransport> implements IManagerMemento {
	/**
	 * The managing project.
	 */
	private final Project project;
	
	public MeanOfTransportManager(Project project) {
		super();
		this.project = Preconditions.checkNotNull(project);
	}
	
	@Override
	public void beforeRemove(MeanOfTransport mot) throws ModelException {
		if(mot.getVehicleTypes().size() > 0) {
			throw new ModelException("Cannot remove a mean of transport that contains vehicle types.");
		}
	}

	@Override
	public void restoreMemento(Object object) {
		MeanOfTransport mot = new MeanOfTransport();
		mot.restoreMemento(object, this.project);
		this.addObject(mot.getId(), mot);
	}
}
