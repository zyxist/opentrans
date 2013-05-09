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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;

/**
 * Default implementation that imports the records from the world model.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DefaultRecordImporter implements IRecordImporter {
	@Inject
	private Provider<World> worldProvider;
	
	@Override
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, VertexRecord ... vertices) {
		World world = this.worldProvider.get();
		for(VertexRecord rec: vertices) {
			if(rec.getFirstTrack() == null && rec.getFirstTrackId() != IIdentifiable.NEUTRAL_ID) {
				populatedUnit.importTrack(world, rec.getFirstTrackId());
			}
			if(rec.getSecondTrack() == null && rec.getSecondTrackId()!= IIdentifiable.NEUTRAL_ID) {
				populatedUnit.importTrack(world, rec.getSecondTrackId());
			}
		}
	}
}
