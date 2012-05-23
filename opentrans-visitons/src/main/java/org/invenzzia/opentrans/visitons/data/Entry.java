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
import org.invenzzia.opentrans.visitons.types.Time;

/**
 * A single entry of the course timetable for the given line. It links
 * a stop with an hour, when the serving vehicle should arrive at it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Entry implements Serializable {
	private Course course;
	
	private Platform stopPlatform;
	
	private Time arrivalTime;
		
	public Course getCourse() {
		return this.course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public Platform getStopPlatform() {
		return this.stopPlatform;
	}
	
	public void setStopPlatform(Platform stopPlatform) {
		this.stopPlatform = stopPlatform;
	}
}
