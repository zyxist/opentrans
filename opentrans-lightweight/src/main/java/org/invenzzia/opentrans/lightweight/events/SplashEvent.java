/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.events;

import com.google.common.base.Preconditions;

/**
 * Events received by the splash screen during startup.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SplashEvent {
	private final int weight;
	private final String message;
	
	public SplashEvent(int weight, String message) {
		Preconditions.checkArgument(weight > 0);
		this.weight = weight;
		this.message = message;
	}
	
	public SplashEvent(int weight) {
		Preconditions.checkArgument(weight > 0);
		this.weight = weight;
		this.message = null;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public String getMessage() {
		return this.message;
	}
}
