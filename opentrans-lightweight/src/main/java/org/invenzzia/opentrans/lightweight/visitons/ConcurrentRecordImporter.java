/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.visitons;

import com.google.inject.Inject;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.bindings.DefaultImporter;
import org.invenzzia.opentrans.visitons.network.VertexRecord;
import org.invenzzia.opentrans.visitons.network.transform.IRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;

/**
 * Record importer that delegates the calls to the model thread, using the
 * default Visitons importer.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ConcurrentRecordImporter implements IRecordImporter {
	@Inject
	@DefaultImporter
	private IRecordImporter defaultImporter;

	@Override
	@InModelThread(asynchronous = false)
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, VertexRecord... vertices) {
		this.defaultImporter.importAllMissingNeighbors(populatedUnit, vertices);
	}
}
