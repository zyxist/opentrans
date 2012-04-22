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
package org.invenzzia.opentrans.visitons;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Provides a concept of a "simulation project". The project consists of a world map and different simulations that could be edited and run
 * by the user.
 *
 * @author Tomasz Jędrzejewski
 */
public class VisitonsProject {

	/**
	 * Name of a file with project settings.
	 */
	public static final String PROJECT_FILE = "project.xml";
	/**
	 * Name of a file with world definition.
	 */
	public static final String MAP_FILE = "world.xml";
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
	@NotNull
	@NotEmpty
	@Size(min = 2, max = 30)
	private String name = "Project name";
	/**
	 * The project author.
	 */
	@NotNull
	@NotEmpty
	@Size(min=2, max = 30)
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
	 * The filesystem location of the project.
	 */
	@NotNull
	@NotEmpty
	@Size(min = 2, max = 300)
	private String path = "";

	/**
	 * @return The project name.
	 */
	public String getName() {
		return this.name;
	} // end getName();

	/**
	 * Sets the project name.
	 *
	 * @param name New name for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setName(String name) {
		this.name = name;
		return this;
	} // end setName();

	/**
	 * @return The project author.
	 */
	public String getAuthor() {
		return this.author;
	} // end getAuthor();

	/**
	 * Sets the project author.
	 *
	 * @param author New author for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setAuthor(String author) {
		this.author = author;
		return this;
	} // end setAuthor();

	/**
	 * @return The project website.
	 */
	public String getWebsite() {
		return this.name;
	} // end getWebsite();

	/**
	 * Sets the project website.
	 *
	 * @param website New website address for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setWebsite(String website) {
		this.website = website;
		return this;
	} // end setWebsite();

	/**
	 * @return The project notes.
	 */
	public String getNotes() {
		return this.notes;
	} // end getNotes();

	/**
	 * Sets the project notes.
	 *
	 * @param name New notes for the project
	 * @return Fluent interface.
	 */
	public VisitonsProject setNotes(String notes) {
		this.notes = notes;
		return this;
	} // end setNotes();
	
	/**
	 * @return Filesystem path.
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Sets the project filesystem path.
	 * 
	 * @param path
	 * @return Fluent interface.
	 */
	public VisitonsProject setPath(String path) {
		this.path = path;
		return this;
	}
} // end VisitonsProject;
