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

package org.invenzzia.opentrans.lightweight.app;

import com.google.inject.Singleton;
import org.invenzzia.helium.exception.ModelException;
import org.invenzzia.opentrans.lightweight.binding.NewProject;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport;
import org.invenzzia.opentrans.visitons.data.Stop;
import org.invenzzia.opentrans.visitons.data.Vehicle;
import org.invenzzia.opentrans.visitons.data.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a pre-populated project for development purposes.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
@NewProject
public class FixtureProjectFactory implements IProjectFactory {
	private final Logger logger = LoggerFactory.getLogger(FixtureProjectFactory.class);
	
	@Override
	public Project createProject() {
		Project project = new Project();
		try {
			MeanOfTransport mot = new MeanOfTransport();
			mot.setName("Tram");
			project.getMeanOfTransportManager().addItem(mot);
			
			VehicleType vt = new VehicleType();
			vt.setName("NGT6");
			vt.setNumberOfSegments(3);
			vt.setLength(27.0);
			vt.getMeanOfTransport().set(mot);
			
			project.getVehicleTypeManager().addItem(vt);
			
			Vehicle vehicle = new Vehicle();
			vehicle.setName("2050");
			vehicle.getVehicleType().set(vt);
			
			project.getVehicleManager().addItem(vehicle);
			
			Stop stop = new Stop();
			stop.setName("Stop 1");
			
			project.getStopManager().addItem(stop);
		} catch(ModelException exception) {
			logger.error("An error occurred while initializing the project.", exception);
		}
		
		return project;
	}
}
