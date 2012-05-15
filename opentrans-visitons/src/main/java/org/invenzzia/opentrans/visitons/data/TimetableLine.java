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
import org.invenzzia.helium.domain.annotation.RelationshipMaster;
import org.invenzzia.helium.domain.relation.RelationshipPerspective;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * The timetable-specific data about the given line. 
 * 
 * @author Tomasz Jędrzejewski
 */
public class TimetableLine implements ISimulationData, Serializable {
	@Valid
	private Line line;
	@Valid @RelationshipMaster(inversedBy="timetableLines")
	private Timetable timetable;	
	@Valid
	private Simulation simulation;
	/**
	 * All courses for this line in this timetable.
	 */
	private RelationshipPerspective<TimetableLine, Course> courses;

	public Line getLine() {
		return this.line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public Timetable getTimetable() {
		return this.timetable;
	}

	public void setTimetable(Timetable timetable) {
		this.timetable = timetable;
	}
	
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
	public void setCoursesPerspective(RelationshipPerspective perspective) {
		this.courses = (RelationshipPerspective<TimetableLine, Course>) perspective;
	}
	
	public RelationshipPerspective<TimetableLine, Course> getCourses() {
		return this.courses;
	}
}
