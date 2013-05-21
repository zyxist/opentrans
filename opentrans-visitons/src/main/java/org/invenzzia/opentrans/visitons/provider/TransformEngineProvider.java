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

package org.invenzzia.opentrans.visitons.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import java.util.Set;
import org.invenzzia.opentrans.visitons.bindings.ActualImporter;
import org.invenzzia.opentrans.visitons.network.transform.IRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.TransformEngine;
import org.invenzzia.opentrans.visitons.network.transform.ops.IOperation;

/**
 * Builds the transformation engine by installing the operations in it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TransformEngineProvider implements Provider<TransformEngine> {
	@Inject @ActualImporter
	private IRecordImporter importer;
	@Inject
	private Set<IOperation> transformOperations;

	@Override
	public TransformEngine get() {
		TransformEngine engine = new TransformEngine(this.importer);
		for(IOperation operation: this.transformOperations) {
			engine.addOperation(operation);
		}
		return engine;
	}

}
