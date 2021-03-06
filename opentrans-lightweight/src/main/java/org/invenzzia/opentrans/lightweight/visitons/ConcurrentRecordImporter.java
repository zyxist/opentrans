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
import java.util.Collection;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.visitons.bindings.DefaultImporter;
import org.invenzzia.opentrans.visitons.network.IVertexRecord;
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
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, IVertexRecord... vertices) {
		this.defaultImporter.importAllMissingNeighbors(populatedUnit, vertices);
	}

	@Override
	@InModelThread(asynchronous = false)
	public void importAllMissingNeighbors(NetworkUnitOfWork populatedUnit, Collection<IVertexRecord> vertices) {
		this.defaultImporter.importAllMissingNeighbors(populatedUnit, vertices);
	}

	@Override
	public void importMissingNeighboursSmarter(NetworkUnitOfWork populatedUnit, IVertexRecord rootVertex) {
		this.delegateImporting(populatedUnit, rootVertex);
	}
	
	@InModelThread(asynchronous = false)
	private void delegateImporting(NetworkUnitOfWork populatedUnit, IVertexRecord rootVertex) {
		this.defaultImporter.importMissingNeighboursSmarter(populatedUnit, rootVertex);
	}
}
