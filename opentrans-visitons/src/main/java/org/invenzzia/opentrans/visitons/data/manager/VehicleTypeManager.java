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

import net.jcip.annotations.NotThreadSafe;
import org.invenzzia.helium.data.AbstractDataManager;
import org.invenzzia.helium.data.interfaces.IManagerMemento;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.VehicleType;

/**
 * Manages the list of all vehicle types.
 * 
 * @author Tomasz Jędrzejewski
 */
@NotThreadSafe
public class VehicleTypeManager extends AbstractDataManager<VehicleType> implements IManagerMemento {
	@Override
	protected void beforeCreate(VehicleType vh) throws ModelException {
		if(!vh.getMeanOfTransport().isDefined()) {
			throw new ModelException("The vehicle type must be bound to some mean of transport.");
		}
		if(vh.getMeanOfTransport().get().getVehicleTypes().isAttached(vh)) {
			throw new ModelException("This vehicle type is already attached to the relation (?).");
		}
	}
	
	@Override
	protected void afterCreate(VehicleType vh) {
		try {
			vh.getMeanOfTransport().get().getVehicleTypes().attach(vh);
		} catch(ModelException exception) {
			throw new IllegalStateException("It shall not be possible to happen here.", exception);
		}
	}
	
	@Override
	public void updateItem(VehicleType vh) throws ModelException {
		if(vh.getMeanOfTransport().isChanged()) {
			MeanOfTransport previousMot = vh.getMeanOfTransport().getPrevious();
			MeanOfTransport currentMot = vh.getMeanOfTransport().get();
			if(null != previousMot) {
				previousMot.getVehicleTypes().detach(vh);
			}
			if(null != currentMot) {
				currentMot.getVehicleTypes().attach(vh);
			}
		}
	}
	
	@Override
	protected void beforeRemove(VehicleType vh) throws ModelException {
		if(!vh.getMeanOfTransport().isDefined()) {
			vh.getMeanOfTransport().get().getVehicleTypes().detach(vh);
		}
	}
	
	@Override
	public void restoreMemento(Object object) {
		VehicleType vt = new VehicleType();
		vt.restoreMemento(object);
		this.addObject(vt.getId(), vt);
	}
}
