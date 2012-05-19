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
import org.invenzzia.helium.domain.relation.IndexedRelationshipPerspective;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * A transportation network line.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Line implements ISimulationData, Serializable {
	/**
	 * Line number and identifier.
	 */
	private String number;
	/**
	 * The simulation this line belongs to.
	 */
	private Simulation simulation;
	/**
	 * The instances of this line for different timetables.
	 */
	private IndexedRelationshipPerspective<Line, Timetable, TimetableLine> timetableLines;
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return this.number;
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
	 * Returns the representation of this line in the given timetable.
	 * There we can find the information about the line timetable, courses
	 * and brigades.
	 * 
	 * @param tt The timetable context.
	 * @return Timetable-specific line information.
	 */
	public TimetableLine getTimetableRepresentation(Timetable tt) {
		return null;
	}
	
	/**
	 * Injector for the relationship perspective. Do not use explicitely.
	 * @param perspective 
	 */
	public void setTimetableLinesPerspective(IndexedRelationshipPerspective perspective) {
		this.timetableLines = (IndexedRelationshipPerspective<Line, Timetable, TimetableLine>) perspective;
	}
	
	public IndexedRelationshipPerspective<Line, Timetable, TimetableLine> getTimetableLines() {
		return this.timetableLines;
	}
}
