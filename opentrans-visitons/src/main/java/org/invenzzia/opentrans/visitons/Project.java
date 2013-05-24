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
package org.invenzzia.opentrans.visitons;

import org.invenzzia.helium.data.interfaces.IMemento;
import org.invenzzia.helium.data.interfaces.IRecord;
import org.invenzzia.opentrans.visitons.data.manager.LineManager;
import org.invenzzia.opentrans.visitons.data.manager.MeanOfTransportManager;
import org.invenzzia.opentrans.visitons.data.manager.StopManager;
import org.invenzzia.opentrans.visitons.data.manager.VehicleManager;
import org.invenzzia.opentrans.visitons.data.manager.VehicleTypeManager;
import org.invenzzia.opentrans.visitons.network.World;

class ProjectBase {
	protected String name;
	protected String author;
	protected String description;
	
	/**
	 * Returns the name of the project.
	 * 
	 * @return Project name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the project name.
	 * 
	 * @param name 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the project author.
	 * 
	 * @return Project author.
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Sets the project author.
	 * 
	 * @param author 
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the project description.
	 * 
	 * @return Project description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the project description.
	 * 
	 * @param description Project description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}


/**
 * Project is a concept that groups all the data about the simulation, and the
 * simulated world.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Project extends ProjectBase implements IMemento<Project> {

	
	/**
	 * The geographical data about the world and the infrastructure.
	 */
	private final World world;
	/**
	 * Manages the list of all stops.
	 */
	private final StopManager stopManager;
	/**
	 * Manages the available means of transport.
	 */
	private final MeanOfTransportManager meanOfTransportManager;
	/**
	 * Manages the available vehicle types.
	 */
	private final VehicleTypeManager vehicleTypeManager;
	/**
	 * Manages the available vehicles.
	 */
	private final VehicleManager vehicleManager;
	/**
	 * Manages the transportation lines.
	 */
	private final LineManager lineManager;

	public Project() {
		this.name = "New project";
		this.author = "Author";
		this.description = "Transportation network simulation project.";
		
		this.world = new World();
		this.stopManager = new StopManager(this);
		this.meanOfTransportManager = new MeanOfTransportManager(this);
		this.vehicleTypeManager = new VehicleTypeManager(this);
		this.vehicleManager = new VehicleManager(this);
		this.lineManager = new LineManager(this);
	}
	
	/**
	 * Returns the object that keeps all the geographical information.
	 * 
	 * @return World object with geographical info.
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Returns the object that manages the list of all stops in the simulation.
	 * 
	 * @return Stop manager.
	 */
	public StopManager getStopManager() {
		return this.stopManager;
	}
	
	/**
	 * Returns the object that manages the means of transport within the project.
	 * 
	 * @return Mean of transport manager.
	 */
	public MeanOfTransportManager getMeanOfTransportManager() {
		return this.meanOfTransportManager;
	}
	
	/**
	 * Returns the object that manages the vehicle types within the project.
	 * 
	 * @return Vehicle type manager.
	 */
	public VehicleTypeManager getVehicleTypeManager() {
		return this.vehicleTypeManager;
	}
	
	/**
	 * Returns the object that manages the vehicles within the project.
	 * 
	 * @return Vehicle manager.
	 */
	public VehicleManager getVehicleManager() {
		return this.vehicleManager;
	}
	
	/**
	 * Returns the object that manages the transportation lines within the project.
	 * 
	 * @return Line manager.
	 */
	public LineManager getLineManager() {
		return this.lineManager;
	}

	@Override
	public Object getMemento(Project project) {
		ProjectRecord record = new ProjectRecord();
		record.importData(this, this);
		return record;
	}

	@Override
	public void restoreMemento(Object object, Project project) {
		if(!(object instanceof ProjectRecord)) {
			throw new IllegalArgumentException("Invalid memento for Project class: "+object.getClass().getCanonicalName());
		}
		ProjectRecord record = (ProjectRecord) object;
		record.exportData(this, this);
	}

	/**
	 * The instances of this class can be used in GUI for manipulation, and then
	 * synchronized with the actual project.
	 */
	public static class ProjectRecord extends ProjectBase implements IRecord<Project, Project> {
		/**
		 * World size in X axis (number of segments).
		 */
		private int sizeX;
		/**
		 * World size in Y axis (number of segments).
		 */
		private int sizeY;
		
		/**
		 * Returns the world size in X axis.
		 * 
		 * @return Number of segments in X axis.
		 */
		public int getSizeX() {
			return this.sizeX;
		}
		
		/**
		 * Returns the world size in Y axis.
		 * 
		 * @return Number of segments in Y axis.
		 */
		public int getSizeY() {
			return this.sizeY;
		}

		@Override
		public void exportData(Project original, Project domainModel) {
			original.setName(this.getName());
			original.setAuthor(this.getAuthor());
			original.setDescription(this.getDescription());
		}

		@Override
		public void importData(Project original, Project domainModel) {
			this.setName(original.getName());
			this.setAuthor(original.getAuthor());
			this.setDescription(original.getDescription());
			this.sizeX = original.getWorld().getX();
			this.sizeY = original.getWorld().getY();
		}
	}
}
