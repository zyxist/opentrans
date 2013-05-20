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

package org.invenzzia.opentrans.visitons.network.transform.modifiers;

/**
 * Helper class for spawning filter objects in DSL-like manner.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Modifiers {
	private Modifiers() {
	}
	
	public static GetTrackFromVertexModifier extractTrack() {
		return new GetTrackFromVertexModifier();
	}
	
	public static GetOpenVertexFromTrackModifier extractOpenVertex() {
		return new GetOpenVertexFromTrackModifier();
	}
	
	public static SwapTracksModifier swapTracks() {
		return new SwapTracksModifier();
	}
	
	public static SwapVerticesModifier swapVertices() {
		return new SwapVerticesModifier();
	}
	
	public static CombinedModifier all(IModifier ... modifiers) {
		return new CombinedModifier(modifiers);
	}
}
