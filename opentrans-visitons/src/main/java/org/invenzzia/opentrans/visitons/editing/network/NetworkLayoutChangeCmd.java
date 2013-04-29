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

package org.invenzzia.opentrans.visitons.editing.network;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import org.invenzzia.helium.data.interfaces.IIdentifiable;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.network.Track;
import org.invenzzia.opentrans.visitons.network.TrackRecord;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;

/**
 * The command carries the {@link NetworkUnitOfWork} to the model thread
 * and then applies all the changes to the original network model.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetworkLayoutChangeCmd implements ICommand {
	private final NetworkUnitOfWork uw;
	
	/**
	 * Creates a new command that modifies the network track layout.
	 * 
	 * @param uw Unit of work that describes the changes to apply.
	 */
	public NetworkLayoutChangeCmd(NetworkUnitOfWork uw) {
		this.uw = Preconditions.checkNotNull(uw);
	}

	@Override
	public void execute(Project project) throws Exception {
		World dieWelt = project.getWorld(); // Deutschland ist ein schones Land :).
		Iterator<TrackRecord> tri = this.uw.overTracks();
		while(tri.hasNext()) {
			TrackRecord tr = tri.next();
			if(tr.getId() < IIdentifiable.NEUTRAL_ID) {
				// New track
				Track track = new Track(tr);
			} else {
				// Existing track
			}
		}
		Iterator<VertexRecord> vri = this.uw.overVertices();
		while(vri.hasNext()) {
			VertexRecord vr = vri.next();
			if(vr.getId() < IIdentifiable.NEUTRAL_ID) {
				// New vertex
			} else {
				// Existing vertex
			}
		}
	}

	@Override
	public void undo(Project project) {
	}

	@Override
	public void redo(Project project) {
	}
}
