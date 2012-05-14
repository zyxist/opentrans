/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.exception;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.exceptions.ApplicationException;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * Different parts of the data model can throw this exception, if we
 * tried to obtain some simulation-specific data from an entity and
 * there was no registered entry for the given simulation.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NoSimulationDataException extends ApplicationException {
	private final Simulation simulation;
	
	public NoSimulationDataException(String message, Simulation simulation) {
		super(message);
		this.simulation = Preconditions.checkNotNull(simulation, "This exception must have a simulation defined.");
	}
	
	public final Simulation getSimulation() {
		return this.simulation;
	}
}
