/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.network;

import com.google.common.base.Preconditions;
import org.invenzzia.helium.data.interfaces.IIdentifiable;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Vertex implements IIdentifiable {
	/**
	 * Unique numerical identifier of this vertex.
	 */
	private long id = IIdentifiable.NEUTRAL_ID;
	/**
	 * Where the vertex is located?
	 */
	private Segment segment;
	/**
	 * Position of the vertex within the segment.
	 */
	private double x;
	/**
	 * Position of the vertex within the segment.
	 */
	private double y;
	/**
	 * The vertex tangent.
	 */
	private double tangent;
	
	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		if(IIdentifiable.NEUTRAL_ID != this.id) {
			throw new IllegalStateException("Cannot change the ID of the vertex.");
		}
		this.id = id;
	}
	
	public Segment getSegment() {
		return this.segment;
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	public double tangent() {
		return this.tangent;
	}
	
	public void setSegment(Segment segment) {
		this.segment = Preconditions.checkNotNull(segment, "The vertex segment cannot be NULL.");
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setTangent(double tangent) {
		this.tangent = tangent;
	}
	
	/**
	 * Sets the new vertex position.
	 * 
	 * @param segment
	 * @param x
	 * @param y 
	 */
	public void setPosition(Segment segment, double x, double y) {
		this.segment = Preconditions.checkNotNull(segment, "The vertex segment cannot be NULL.");
		this.x = x;
		this.y = y;
	}
}
