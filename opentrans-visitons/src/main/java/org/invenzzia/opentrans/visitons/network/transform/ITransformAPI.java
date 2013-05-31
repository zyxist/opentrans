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

package org.invenzzia.opentrans.visitons.network.transform;

import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.WorldRecord;

/**
 * The API that might be used by the operations to implement their stuff.
 *
 * @author Tomasz Jędrzejewski
 */
public interface ITransformAPI {
	
	public WorldRecord getWorld();
	
	public IRecordImporter getRecordImporter();
	/**
	 * Returns the current network unit of work.
	 * 
	 * @return Network unit of work.
	 */
	public NetworkUnitOfWork getUnitOfWork();
	/**
	 * Calculates the parameters of a straight line.
	 * 
	 * @param tr 
	 */
	public void calculateStraightLine(TrackRecord tr);
	/**
	 * Calculates the parameters of a curve. The extra arguments are the two vertices
	 * of the curve, and they determine the order of drawing.
	 * 
	 * @param tr Curved track.
	 */
	public void calculateCurve(TrackRecord tr);
	/**
	 * Performs the calculations that find the parameters of the free (doubly-curved) track
	 * that matches the tangents in vertices v1 and v2. The calculations do not change the
	 * vertex tangents.
	 * 
	 * @param tr Updated track record.
	 */
	public void calculateFreeCurve(TrackRecord tr);
	/**
	 * This operation shall be applied to three vertices connected by:
	 * 
	 * <ul>
	 *  <li>(v1, v2) - straight line</li>
	 *  <li>(v2, v3) - curved line</li>
	 * </ul>
	 * 
	 * We assume that v1 has been freely moved and we must adjust the location of v2 vertex
	 * to match the constraints of both straight line and a curved line. We do this by putting
	 * v2 and v3 on a virtual circle, with the middle in the intersection point of (v3 tangent line,
	 * v1 tangent line) and move v2 along this circle.
	 * 
	 * @param v1 Moved vertex connected to a straight line.
	 * @param v2 Adjusted vertex connecting the curve and the straight line.
	 * @param v3 Stationary point that begins the curve.
	 */
	public void curveFollowsStraightTrack(VertexRecord v1, VertexRecord v2, VertexRecord v3);
	/**
	 * In this transformation we assume that the bound vertex is connected just to the curved
	 * track. We find the centre of the arc and the proper direction, so that the curve leads
	 * from the previous track to this point.
	 * 
	 * @param curvedTrack
	 * @param boundVertex The point that updated its position.
	 */
	public void curveFollowsPoint(TrackRecord curvedTrack, VertexRecord boundVertex);
	/**
	 * Matches the curved track and straight track to fit together.
	 * 
	 * @param curvedTrack The adjusted curved track.
	 * @param straightTrack The adjusted straight track.
	 * @param v1 First vertex: connected just to a curve.
	 * @param v2 Second vertex: between curved and straight track.
	 */
	public boolean matchStraightTrackAndCurve(TrackRecord curvedTrack, TrackRecord straightTrack, IVertexRecord v1, VertexRecord v2);
	/**
	 * Sometimes we have a free position, and we want to cast it to some tangent, in order to apply
	 * the new positions of the vertex.
	 * 
	 * @param v1 Vertex to update along its tangent line.
	 * @param x Free position: X
	 * @param y Free position: Y
	 */
	public void castFreePositionToTangent(VertexRecord v1, double x, double y);
}
