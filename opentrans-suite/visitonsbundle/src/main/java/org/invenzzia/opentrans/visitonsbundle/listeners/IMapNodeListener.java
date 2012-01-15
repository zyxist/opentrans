/*
 * Visitons - transportation network simulation and visualization library.
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
package org.invenzzia.opentrans.visitonsbundle.listeners;

import org.invenzzia.visitons.visualization.World;
import org.openide.nodes.Node;

/**
 * By implementing this cookie you can decide, what the application
 * should do once we click on the "Map" node in the project.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IMapNodeListener extends Node.Cookie
{
	/**
	 * The user clicked on the map node.
	 * 
	 * @param world The world represented by this node.
	 */
	public void openWorld(World world);
} // end IMapNodeListener;

