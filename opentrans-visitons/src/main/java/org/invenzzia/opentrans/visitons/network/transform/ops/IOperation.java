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

package org.invenzzia.opentrans.visitons.network.transform.ops;

import org.invenzzia.opentrans.visitons.network.transform.ITransformAPI;
import org.invenzzia.opentrans.visitons.network.transform.TransformEngine;

/**
 * Most primitive form of operation that makes no assumptions about the
 * internal structure. It is expected that the operation exposes some public
 * method for the transformation engine users. The interface below is
 * for the intercommunication with the transform engine.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IOperation {
	/**
	 * The method is called by {@link TransformEngine} to inject its API, when the
	 * operation is registered.
	 * 
	 * @param api The API of the transformation engine.
	 */
	public void setTransformAPI(ITransformAPI api);
}
