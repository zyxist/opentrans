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

package org.invenzzia.opentrans.lightweight.model;

/**
 * The method is called by the batch model, when the data from the
 * model thread become available, and the Event Dispatch Thread can
 * collect them.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IBatchModelListener<T extends AbstractBatchModel> {
	/**
	 * Called, when the model data are available. The method is
	 * guaranteed to be called in the Swing thread.
	 */
	public void modelDataAvailable(T model);
}
