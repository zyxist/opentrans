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
package org.invenzzia.opentrans.client.projectmodel;

import org.invenzzia.helium.gui.ui.trees.INodeDescriptor;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldDescriptor implements INodeDescriptor<World> {
	
	@Override
	public Class<World> describes() {
		return World.class;
	}

	@Override
	public String getNameFor(World node) {
		return "Network view";
	}

	@Override
	public String getToolTipTextFor(World node) {
		return "Opens a map of the transportation network.";
	}
}
