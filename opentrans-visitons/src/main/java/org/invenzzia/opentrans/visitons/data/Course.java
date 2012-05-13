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
import java.util.List;
import java.util.Map.Entry;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * Represents a single course of the timetable line. The course can have
 * different termini than the primary settings of the line, thus being
 * a variant course or a shortened relation.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Course implements ISimulationData, Serializable {
	@Valid
	@NotNull
	private TimetableLine timetableLine;	
	@Valid
	@NotNull
	private Simulation simulation;
	@Valid
	@NotNull
	private Stop startTerminus;
	@Valid
	@NotNull
	private Stop endTerminus;
	
	private List<Entry> timetableEntries;
	
	
	public TimetableLine getTimetableLine() {
		return this.timetableLine;
	}
	
	public void setTimetableLine(TimetableLine line) {
		this.timetableLine = line;
	}
	
	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}
}
