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
import java.util.Collection;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
import org.invenzzia.opentrans.visitons.network.JunctionRecord;
import org.invenzzia.opentrans.visitons.network.NetworkConst;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
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
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, Collection<IVertexRecord> vertices) {
		World world = this.worldProvider.get();
		for(IVertexRecord rec: vertices) {
			if(null != rec) {
				this.processSingleRecord(populatedUnit, world, rec);
			}
		}
	}
	
	@Override
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, IVertexRecord ... vertices) {
		World world = this.worldProvider.get();
		for(IVertexRecord rec: vertices) {
			if(null != rec) {
				this.processSingleRecord(populatedUnit, world, rec);
			}
		}
	}
	
	private void processSingleRecord(NetworkUnitOfWork populatedUnit, World world, IVertexRecord rec) {
		if(rec.getFirstTrack() == null && rec.getFirstTrackId() != IIdentifiable.NEUTRAL_ID) {
			TrackRecord tr = populatedUnit.importTrack(world, rec.getFirstTrackId());
			this.importJunctions(populatedUnit, world, tr);
		}
		if(rec.getSecondTrack() == null && rec.getSecondTrackId()!= IIdentifiable.NEUTRAL_ID) {
			TrackRecord tr = populatedUnit.importTrack(world, rec.getSecondTrackId());
			this.importJunctions(populatedUnit, world, tr);
		}
	}

	@Override
	public void importMissingNeighboursSmarter(NetworkUnitOfWork populatedUnit, IVertexRecord rootVertex) {
		World world = this.worldProvider.get();
		this.processSingleRecord(populatedUnit, world, rootVertex);
		
		// Oh, it turns out that this 'smartness' does not have to be so smart. I like simplicity.
		if(null != rootVertex.getFirstTrack()) {
			IVertexRecord lev1a = rootVertex.getFirstTrack().getOppositeVertex(rootVertex);		
			if(lev1a.hasAllTracks()) {
				this.processSingleRecord(populatedUnit, world, lev1a);
			}
		}
		if(null != rootVertex.getSecondTrack()) {
			IVertexRecord lev1b = rootVertex.getSecondTrack().getOppositeVertex(rootVertex);
			if(lev1b.hasAllTracks()) {
				this.processSingleRecord(populatedUnit, world, lev1b);
			}
		}
	}
	
	private void importJunctions(NetworkUnitOfWork populatedUnit, World world, TrackRecord tr) {
		for(JunctionRecord jr: tr.getJunctions()) {
			this.processSingleRecord(populatedUnit, world, jr);
			IVertexRecord opposite = jr.getSlaveTrack().getOppositeVertex(jr);
			if(jr.getSlaveTrack().getType() != NetworkConst.TRACK_FREE && !(opposite instanceof JunctionRecord)) {
				this.processSingleRecord(populatedUnit, world, opposite);
			}
		}
	}
}
