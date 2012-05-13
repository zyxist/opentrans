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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.invenzzia.helium.domain.annotation.Identifier;

/**
 * A single project may consist of several simulations with different initial
 * conditions. In addition, there can be defined "simulation templates" which
 * act as templates for creating other, concrete simulations.
 * 
 * The only part of the project that is shared among all the simulations is the
 * world map and infrastructure.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Simulation {
	@NotEmpty
	@Size(min = 2, max = 30)
	@Identifier
	private String id;
	@NotNull
	@NotEmpty
	@Size(min = 2, max = 30)
	private String name;
	private boolean template;
	@Valid
	private VisitonsProject project;
	
	public Simulation() {
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isTemplate() {
		return this.template;
	}
	
	public void setTemplate(boolean status) {
		this.template = status;
	}
	
	public VisitonsProject getVisitonsProject() {
		return this.project;
	}
	
	public void setVisitonsProject(VisitonsProject project) {
		this.project = project;
	}
}
