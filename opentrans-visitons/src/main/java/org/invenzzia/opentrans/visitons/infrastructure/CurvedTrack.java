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
package org.invenzzia.opentrans.visitons.infrastructure;

import com.google.common.base.Preconditions;
import org.invenzzia.opentrans.visitons.geometry.LineOps;

/**
 * One of three primitives for building tracks: a regular curve, part
 * of the arc.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CurvedTrack extends AbstractTrack<CurvedTrack> {
	/**
	 * Arc central point: X
	 */
	protected double centX;
	/**
	 * Arc central point: Y
	 */
	protected double centY;
	/**
	 * Promised values of the center and middle points, if the vertex change is applied.
	 */
	protected double promise[];

	public CurvedTrack(long id) {
		super(id);
	}
	
	/**
	 * @return Arc central point: X 
	 */
	public double centX() {
		return this.centX;
	}
	
	/**
	 * @return Arc central point Y 
	 */
	public double centY() {
		return this.centY;
	}
	
	@Override
	public boolean isVertexChangeAllowed(IVertex vertex, double x, double y) {
		/*
		IVertex anotherVertex;
		if(vertex == this.vertices[0]) {
			anotherVertex = this.vertices[1];
		} else {
			anotherVertex = this.vertices[0];
		}
		*/
		IVertex v1, v2;
		if(vertex == this.vertices[0]) {
			v1 = this.vertices[1];
			v2 = this.vertices[0];
		} else {
			v1 = this.vertices[0];
			v2 = this.vertices[1];
		}
		double L1[] = new double[6];
		v1.getTangent(0, L1);
		LineOps.toOrthogonal(0, L1, v1.x(), v1.y());
		double A2 = 2 * (v1.x() - v2.x());
		double B2 = 2 * (v1.y() - v2.y());
		double C2 = -(Math.pow(v1.x(), 2) - Math.pow(v2.x(), 2) + Math.pow(v1.y(), 2) - Math.pow(v2.y(), 2));
		
		this.promise = new double[4];
		
		// Calculate the center of the circle we cut the arc from.
		this.promise[0] = (L1[1] * C2 - L1[2] * B2) / (B2 * L1[0] - L1[1] * A2);
		this.promise[1] = - ((L1[2] + L1[0] * this.promise[0]) / L1[1]);
		
		// Now, calculate the middle point.
		LineOps.toGeneral(v1.x(), v1.y(), x, y, 3, L1);
		System.out.format("LA: %f, %f, %f\n", L1[3], L1[4], L1[5]);
		LineOps.toOrthogonal(3, L1, this.promise[0], this.promise[1]);
		System.out.format("LB: %f, %f, %f\n", L1[3], L1[4], L1[5]);
		v1.getTangent(0, L1);
		this.promise[2] = (L1[1] * L1[5] - L1[2] * L1[4]) / (L1[4] * L1[0] - L1[1] * L1[3]);
		this.promise[3] = - ((L1[2] + L1[0] * this.promise[2]) / L1[1]);
		
		System.out.format("C: (%f, %f); M: (%f, %f)\n", this.promise[0], this.promise[1], this.promise[2], this.promise[3]);
		
		int middleLocation = LineOps.onWhichSide(v1.x(), v1.y(), this.promise[0], this.promise[1], this.promise[2], this.promise[3]);
		int testedLocation = LineOps.onWhichSide(v1.x(), v1.y(), this.promise[0], this.promise[1], x, y);
		
		if(middleLocation != testedLocation) {
			this.promise = null;
			return false;
		}
		
		// If both vertices are connected just to our curved track, we cannot edit the curve anymore
		// because we can't calculate the tangents.
		return (this.vertices[0].getTrackCount() != 1 || this.vertices[1].getTrackCount() != 1);
	}
	
	@Override
	public void verticesUpdated() {
		this.centX = this.promise[0];
		this.centY = this.promise[1];
		
		this.middleX = this.promise[2];
		this.middleY = this.promise[3];
		this.promise = null;
	/*	IVertex v1, v2;
		if(this.vertices[0].getTrackCount() == 1) {
			v1 = this.vertices[1];
			v2 = this.vertices[0];
		} else if(this.vertices[1].getTrackCount() == 1) {
			v1 = this.vertices[0];
			v2 = this.vertices[1];
		} else {
			// not supported yet - blow up by now.
			v1 = null;
			v2 = null;
		}
		double L1[] = new double[6];
		v1.getTangent(0, L1);
		LineOps.toOrthogonal(0, L1, v1.x(), v1.y());
		double A2 = 2 * (v1.x() - v2.x());
		double B2 = 2 * (v1.y() - v2.y());
		double C2 = -(Math.pow(v1.x(), 2) - Math.pow(v2.x(), 2) + Math.pow(v1.y(), 2) - Math.pow(v2.y(), 2));
		
		// Calculate the center of the circle we cut the arc from.
		this.centX = (L1[1] * C2 - L1[2] * B2) / (B2 * L1[0] - L1[1] * A2);
		this.centY = - ((L1[2] + L1[0] * this.centX) / L1[1]);
		
		// Now, calculate the middle point.
		LineOps.toGeneral(v1.x(), v1.y(), v2.x(), v2.y(), 3, L1);
		LineOps.toOrthogonal(3, L1, this.centX, this.centY);

		this.middleX = (L1[1] * L1[5] - L1[2] * L1[4]) / (L1[4] * L1[0] - L1[1] * L1[3]);
		this.middleY = - ((L1[2] + L1[0] * this.middleX) / L1[1]);
		*/
	}
	
	/**
	 * If we do not update the vertices, free the memory after the promise.
	 */
	@Override
	public void verticesNotUpdated() {
		this.promise = null;
	}

	@Override
	public CurvedTrack fork() {
		CurvedTrack ct = new CurvedTrack(this.getId());
		ct.vertices[0] = this.vertices[0];
		ct.vertices[1] = this.vertices[1];
		ct.centX = this.centX;
		ct.centY = this.centY;
		return ct;
	}

	@Override
	public void copyFrom(CurvedTrack copy) {
		Preconditions.checkArgument(copy.getId() == this.getId(), "Cannot copy from a track with a different ID.");
		this.vertices[0] = copy.vertices[0];
		this.vertices[1] = copy.vertices[1];
		this.centX = copy.centX;
		this.centY = copy.centY;
	}
	
	@Override
	public void getTangentInVertex(int vertex, int from, double tan[]) {
		if(this.vertices[vertex].getTrackCount() == 1) {
			// We must calculate...
			LineOps.toOrthogonal(this.centX, this.centY, this.vertices[vertex].x(), this.vertices[vertex].y(), from, tan);
		} else {
			// We can take from that vertex.
			this.vertices[vertex].getTangent(from, tan);
			LineOps.toOrthogonal(from, tan, this.vertices[vertex].x(), this.vertices[vertex].y());
		}
	}
}
