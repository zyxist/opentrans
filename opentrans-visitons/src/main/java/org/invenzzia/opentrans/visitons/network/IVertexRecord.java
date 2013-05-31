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

import org.invenzzia.helium.data.interfaces.IIdentifiable;

/**
 * Common interface for junctions and vertices. Allows treating them as
 * the same entities by most operations. There is an important difference
 * for geometry stuff between vertices and their records. The records operate
 * on absolute units, whereas the coordinate system for {@link IVertex} objects
 * is relative towards the top-left corner of a segment. Rationale: vertices
 * must survive world size changes without problems, records are by definition
 * temporary and we can simply discard them. Although vertex records are used
 * as mementos in the history engine, this does not hurt us, because the operations
 * are atomic, so if the world size was A, B, when the network layout operation
 * was executed, then it will always be A, B during the undo/redo operations.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IVertexRecord extends IIdentifiable {
	/**
	 * Returns <strong>true</strong>, if this vertex has a destination ID, not temporary one. It means
	 * that we need to update an existing vertex, not create a new one.
	 * 
	 * @return True, if this vertex is persisted.
	 */
	public boolean isPersisted();
	/**
	 * @return Absolute X coordinate.
	 */
	public double x();
	/**
	 * @return Absolute Y coordinate.
	 */
	public double y();
	/**
	 * @return Tangent in this point.
	 */
	public double tangent();
	/**
	 * Returns the tangent for the given track in this vertex.
	 * 
	 * @param tr Track record.
	 * @return Tangent for this track.
	 */
	public double tangentFor(TrackRecord tr);
	
	/**
	 * Returns the tangent for the opposite track.
	 * 
	 * @param tr
	 * @return Tangent for the opposite track.
	 */
	public double oppositeTangentFor(TrackRecord tr);
	/**
	 * This method can be used, if the vertex has only one track connected. It returns the opposite value
	 * of the tangent, so that it can be used to derive a new track.
	 * 
	 * @return The opposite tangent to the one already set.
	 */
	public double getOpenTangent();
	/**
	 * Validates the tangent condition. Returns true, if changing the tangent for
	 * the given track to the specified value is possible.
	 * 
	 * @param tr
	 * @param tangent
	 * @return True, if change is possible and won't break anything.
	 */
	public boolean areTangentsOK();
	/**
	 * Sets the tangent for the specified track. Before applying this method, the
	 * user must ensure that the operation won't break the tangent condition.
	 * 
	 * @param tr
	 * @param tangent
	 * @return Fluent interface.
	 */
	public VertexRecord setTangentFor(TrackRecord tr, double tangent);
	/**
	 * @return True, if there are two tracks connected to this vertex.
	 */
	public boolean hasAllTracks();
	/**
	 * @return True, if there is exactly one track connected to this vertex.
	 */
	public boolean hasOneTrack();
	/**
	 * @return True, if there are no tracks connected to this vertex.
	 */
	public boolean hasNoTracks();
	/**
	 * If only one track is connected, the method returns it.
	 */
	public TrackRecord getTrack();
	/**
	 * Returns the track record connected to this vertex. Note that the track may not be imported
	 * from the world model. In this case, this method would return <strong>null</strong>, but the
	 * ID of the connected track could be obtained with {@link #getFirstTrackId()}. To extract the ID
	 * of the connected track regardless of the storage method, use {@link #getFirstTrackActualId()}.
	 * 
	 * @return First track record.
	 */
	public TrackRecord getFirstTrack();
	/**
	 * Returns the track record connected to this vertex. Note that the track may not be imported
	 * from the world model. In this case, this method would return <strong>null</strong>, but the
	 * ID of the connected track could be obtained with {@link #getSecondTrackId()}. To extract the ID
	 * of the connected track regardless of the storage method, use {@link #getSecondTrackActualId()}.
	 * 
	 * @return Second track record.
	 */
	public TrackRecord getSecondTrack();
	/**
	 * Returns the unimported track ID. See note to {@link #getFirstTrack()}.
	 * 
	 * @return ID of the first track, which is unimported from the world model.
	 */
	public long getFirstTrackId();
	/**
	 * Returns the unimported track ID. See note to {@link #getSecondTrack()}.
	 * 
	 * @return ID of the second track, which is unimported from the world model.
	 */
	public long getSecondTrackId();
	/**
	 * Returns the track ID regardless of the storage method. See note to {@link #getFirstTrack()}.
	 * 
	 * @return ID of the first track.
	 */
	public long getFirstTrackActualId();
	/**
	 * Returns the track ID regardless of the storage method. See note to {@link #getSecondTrack()}.
	 * 
	 * @return ID of the second track.
	 */
	public long getSecondTrackActualId();
	/**
	 * Returns true, if there are some unimported tracks connected to this vertex.
	 * 
	 * @return 
	 */
	public boolean hasUnimportedTracks();
	/**
	 * Tracks must know, how to remove themselves from the vertex.
	 * 
	 * @param track 
	 */
	public void removeTrack(TrackRecord track);
	/**
	 * This method is used while importing the track. It replaces the ID-based reference
	 * with the newly imported track.
	 * 
	 * @param tr 
	 */
	public void replaceReferenceWithRecord(TrackRecord tr);
}
