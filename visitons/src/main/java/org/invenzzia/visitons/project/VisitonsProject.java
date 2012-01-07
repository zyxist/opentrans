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
	private String name = "Project name";
	/**
	 * The project author.
	 */
	private String author = "";
	/**
	 * The project website.
	 */
	private String website = "";
	/**
	 * The project notes.
	 */
	private String notes = "";
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
	
	/**
	 * Constructs the project object. At the start, all the internal data structures
	 * are correctly initialized.
	 */
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
	
	/**
	 * @return The project author.
	 */
	public String getAuthor()
	{
		return this.author;
	} // end getAuthor();
	
	/**
	 * Sets the project author.
	 * 
	 * @param author New author for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setAuthor(String author)
	{
		this.author = author;
		return this;
	} // end setAuthor();
	
	/**
	 * @return The project website.
	 */
	public String getWebsite()
	{
		return this.name;
	} // end getWebsite();
	
	/**
	 * Sets the project website.
	 * 
	 * @param website New website address for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setWebsite(String website)
	{
		this.website = website;
		return this;
	} // end setWebsite();
	
	/**
	 * @return The project notes.
	 */
	public String getNotes()
	{
		return this.notes;
	} // end getNotes();
	
	/**
	 * Sets the project notes.
	 * 
	 * @param name New notes for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setNotes(String notes)
	{
		this.notes = notes;
		return this;
	} // end setNotes();
	
	/**
	 * @return The world object containing the geographical network structure
	 */
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
