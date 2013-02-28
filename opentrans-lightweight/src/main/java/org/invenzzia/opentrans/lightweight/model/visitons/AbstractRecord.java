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

package org.invenzzia.opentrans.lightweight.model.visitons;

import java.lang.ref.WeakReference;
import org.invenzzia.opentrans.visitons.exception.ModelException;

/**
 * In GUI, we do not want to operate on model objects directly, because
 * they are not owned by Swing thread. This is why we are making copies
 * that carry the data that are useful for us.
 * 
 * <p>The records may carry a weak reference to the original object, so
 * that we know, where we should synchronize to.
 * 
 * @param T original object type.
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractRecord<T> {
	private WeakReference<T> original;
	
	public AbstractRecord() {
		this.original = null;
	}
	
	public AbstractRecord(T obj) {
		this.original = new WeakReference(obj);
	}
	
	/**
	 * Exports the data back to the original object. Be careful in
	 * multithreaded environment!
	 * 
	 * @return True, if the export was successful and the original object exists.
	 */
	public boolean export() throws ModelException {
		T obj = this.original.get();
		if(null != obj) {
			this.performExport(obj);
			return true;
		}
		return false;
	}
	
	/**
	 * Performs the actual export operation.
	 * 
	 * @param object The object we shall export to.
	 */
	abstract protected void performExport(T object) throws ModelException;
	
	/**
	 * Returns the original object or NULL, if it has not been
	 * assigned or it is lost.
	 * 
	 * @return Original object.
	 */
	public T getOriginal() {
		return this.original.get();
	}
	
	/**
	 * Creates a new instance from this record, and populates it
	 * with the necessary data.
	 * 
	 * @param newInstance Raw instance of the original Visitons object.
	 */
	public void create(T newInstance) throws ModelException {
		this.original = new WeakReference(newInstance);
		this.performExport(newInstance);		
	}
}
