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

import java.util.Collection;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;

/**
 * The implementation of this interface allows the transformer to import
 * necessary extra data from the world model. By default it is implemented
 * by the network unit of work, but we might want to make a delegate that
 * would perform appropriate imports in a separate thread or something else.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IRecordImporter {
	/**
	 * For each vertex, this method should analyze the tracks, and if there is only
	 * their ID, the appropriate track records should be created.
	 * 
	 * @param populatedUnit
	 * @param vertices 
	 */
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, IVertexRecord ... vertices);
	/**
	 * For each vertex, this method should analyze the tracks, and if there is only
	 * their ID, the appropriate track records should be created.
	 * 
	 * @param populatedUnit
	 * @param vertices 
	 */
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, Collection<IVertexRecord> vertices);
	/**
	 * Imports the data for one of the cases for {@link Transformations#moveVertexToPosition()}. See the long
	 * comment in that method to get to know more. Note that the passed vertex <strong>MUST</strong> have two
	 * tracks connected. And don't try to argue with that, because some implementations won't be so polite to
	 * inform you about that with some precondition checking!
	 * 
	 * @param populatedUnit 
	 * @param rootVertex The initial vertex that we are sure we know.
	 */
	public void importMissingNeighboursSmarter(NetworkUnitOfWork populatedUnit, IVertexRecord rootVertex);
}
