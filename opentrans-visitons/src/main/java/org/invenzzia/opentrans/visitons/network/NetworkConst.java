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

/**
 * A collection of constants related to the network structure. We do not use
 * enums here, because the network graph can be quite huge and we must save
 * the memory a bit.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkConst {
	public static final byte TRACK_STRAIGHT = 0;
	public static final byte TRACK_CURVED = 1;
	public static final byte TRACK_FREE = 2;
	
	public static final byte VERTEX_FREE = 0;
	public static final byte VERTEX_HALFFREE = 1;
	public static final byte VERTEX_SLAVE = 2;
	
	/**
	 * Indicates that we cannot snap to this track.
	 */
	public static final byte NO_SNAPPING = 0;
	/**
	 * Indicates that we can connect to the track by making a junction.
	 */
	public static final byte SNAP_CONNECT = 1;
	/**
	 * Indicates that we can adjust the track to match this one.
	 */
	public static final byte SNAP_ADJUST = 2;
	
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * only lenghtening or shortening the straight track along the tangent line.
	 */
	public static final byte MODE_DEFAULT = 0;
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * free movement allowed, the curve is adjusted to match the tangent.
	 */
	public static final byte MODE_ALT1 = 1;
	/**
	 * For moving the straight track-ending vertex that is connected to a curve:
	 * free movement allowed, the curve is adjusted to match the tangent (alternative
	 * scenario).
	 */
	public static final byte MODE_ALT2 = 2;
	/**
	 * The type value for the 'platform' track objects.
	 */
	public static final int TRACK_OBJECT_PLATFORM = 2;
	/**
	 * The type value for the 'vehicle' track object.
	 */
	public static final int TRACK_OBJECT_VEHICLE = 3;

	private NetworkConst() {
	}
}
