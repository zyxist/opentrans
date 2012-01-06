/*
 * Visitons - transportation network simulation and visualization library.
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
package org.invenzzia.visitons.project;

import org.invenzzia.utils.persistence.DefaultPersistableManager;
import org.invenzzia.utils.persistence.IPersistableManager;
import org.invenzzia.visitons.visualization.World;

/**
 * This class represents a simulation project. This API is system-independent.
 * In order to look for NetBeans-specific code, look for the @link{org.invenzzia.visitons.netbeans}
 * package.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisitonsProject
{
	/**
	 * Name of a file with project settings.
	 */
	public static final String PROJECT_FILE = "project.xml";
	/**
	 * Name of a file with world definition.
	 */
	public static final String MAP_FILE = "world.xml";
	/**
	 * A directory where the situations are stored.
	 */
	public static final String SITUATION_DIR = "situations";
	/**
	 * A directory where the simulations are stored.
	 */
	public static final String SIMULATION_DIR = "simulations";
	/**
	 * Name of a directory where the segment images are stored.
	 */
	public static final String IMAGE_DIR = "images";
	
	/**
	 * Project name.
	 */
	private String name;
	/**
	 * The world object.
	 */
	protected World world;
	/**
	 * The manager for the situations.
	 */
	protected DefaultPersistableManager<String, Situation> situationManager;
	/**
	 * The manager for the simulations.
	 */
	protected DefaultPersistableManager<String, Simulation> simulationManager;
	
	public VisitonsProject()
	{
		this.world = new World();
		this.situationManager = new DefaultPersistableManager<>();
		this.simulationManager = new DefaultPersistableManager<>();
	} // end VisitonsProject();

	/**
	 * @return The project name.
	 */
	public String getName()
	{
		return this.name;
	} // end getName();
	
	/**
	 * Sets the project name.
	 * 
	 * @param name New name for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setName(String name)
	{
		this.name = name;
		return this;
	} // end setName();
	
	public World getWorld()
	{
		return this.world;
	} // end getWorld();

	public IPersistableManager<String, Situation> getSituationManager()
	{
		return this.situationManager;
	} // end getSituationManager();

	public IPersistableManager<String, Simulation> getSimulationManager()
	{
		return this.simulationManager;
	} // end getSimulationManager();
} // end VisitonsProject;
