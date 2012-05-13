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
import javax.validation.Valid;
import org.invenzzia.opentrans.visitons.ISimulationData;
import org.invenzzia.opentrans.visitons.Simulation;

/**
 * Brigade is an assignment of a single vehicle to a certain group of courses
 * in the timetable.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Brigade implements ISimulationData, Serializable {
	/**
	 * Primary line of the brigade.
	 */
	private Line line;
	/**
	 * Brigade identification number.
	 */
	private byte number;
	/**
	 * Courses assigned to this brigade. Note that they do not have to be from the primary line.
	 * A brigade of line A can also serve some courses on line B to optimize the costs.
	 */
	private List<Course> courses;
	/**
	 * The vehicle that serves this group of courses.
	 */
	private Vehicle vehicle;
	@Valid
	private Simulation simulation;

	public Line getLine() {
		return this.line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public byte getNumber() {
		return this.number;
	}

	public void setNumber(byte number) {
		this.number = number;
	}

	public Vehicle getVehicle() {
		return this.vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}
} // end Brigade;
