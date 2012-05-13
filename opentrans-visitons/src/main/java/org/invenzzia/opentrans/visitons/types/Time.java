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
package org.invenzzia.opentrans.visitons.types;

/**
 * Time type for the timetable: hour 0-23 and minute 0-59.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Time {
	private byte hour;
	private byte minute;
	
	public Time(String timeStr) {
		
	}
	
	public Time(byte hour, byte minute) {
		
	}
	
	public void setHour(byte hour) {
		this.hour = hour;
	}
	
	public void setMinute(byte minute) {
		this.minute = minute;
	}
	
	public byte getHour() {
		return this.hour;
	}
	
	public byte getMinute() {
		return this.minute;
	}
} // end Time;
