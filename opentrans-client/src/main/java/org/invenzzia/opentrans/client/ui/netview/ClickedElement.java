/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.ui.netview;

import org.invenzzia.opentrans.visitons.world.Segment;

/**
 * For the context pop-up menus: describes what has been clicked.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ClickedElement {
	private final double x;
	private final double y;
	private final Segment segment;
	
	public ClickedElement(Segment segment, double x, double y) {
		this.x = x;
		this.y = y;
		this.segment = segment;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public Segment getSegment() {
		return this.segment;
	}
}
