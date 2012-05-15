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
package org.invenzzia.opentrans.visitons.data;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.invenzzia.helium.domain.annotation.Identifier;
import org.invenzzia.helium.domain.annotation.RelationshipMaster;
import org.invenzzia.helium.domain.relation.RelationshipPerspective;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * The timetable describes, when each vehicle should arrive at the given
 * stop on its line. The simulation can have several timetables for different
 * days.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Timetable implements ISimulationData, Serializable {
	@Min(value = 0)
	@Identifier
	private int id;
	private String name;
	@Valid @RelationshipMaster
	private Simulation simulation;
	/**
	 * All lines in this timetable.
	 */
	private RelationshipPerspective<Timetable, TimetableLine> timetableLines;

	public int getId() {
		return this.id;
	}

	public void setId(int iterator) {
		this.id = iterator;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Do not call explicitely. Use relationship manager instead.
	 * @param simulation 
	 */
	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	/**
	 * Injector for the relationship perspective. Do not use explicitely.
	 * @param perspective 
	 */
	public void setTimetableLinesPerspective(RelationshipPerspective perspective) {
		this.timetableLines = (RelationshipPerspective<Timetable, TimetableLine>) perspective;
	}
	
	public RelationshipPerspective<Timetable, TimetableLine> getTimetableLines() {
		return this.timetableLines;
	}
}
