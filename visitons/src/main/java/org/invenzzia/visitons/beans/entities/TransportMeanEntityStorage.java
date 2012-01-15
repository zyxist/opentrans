/*
 * Visitons - transportation network simulation and visualization library.
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.visitons.beans.entities;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.utils.entities.IEntityStorage;
import org.invenzzia.utils.exception.PersistenceException;
import org.invenzzia.utils.exception.ValidationException;
import org.invenzzia.visitons.beans.TransportMean;

/**
 * The storage for managing transport means.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TransportMeanEntityStorage implements IEntityStorage<TransportMean>
{
	private int iterator;
	private Map<Integer, TransportMean> entities;
	
	public TransportMeanEntityStorage()
	{
		this.entities = new LinkedHashMap<>();
	} // end TransportMeanEntityStorage();

	public Class<?>[] listenFor()
	{
		return null;
	} // end listenFor();
	
	public TransportMean getById(int id)
	{
		return this.entities.get(id);
	} // end getById();
	
	public Collection<TransportMean> getAll()
	{
		return this.entities.values();
	} // end getAll();

	public Collection<TransportMean> getByRelationship(Object obj)
	{
		return null;
	} // end getByRelationship();
	
	public boolean contains(TransportMean vc)
	{
		TransportMean mean = this.entities.get(vc.getId());
		if(null != mean && vc == mean)
		{
			return true;
		}
		return false;
	} // end contains();
	
	public Collection<TransportMean> getByRelation(Object obj)
	{
		return null;
	} // end getByRelation();
	
	public void persist(TransportMean o) throws ValidationException, PersistenceException
	{
		TransportMean tm = (TransportMean) o;
		tm.setId(this.iterator);
		this.entities.put(this.iterator++, tm);
	} // end persist();
	
	public void update(TransportMean o, TransportMean newObject) throws ValidationException, PersistenceException
	{
		o.copyFrom(newObject);
	} // end update();
	
	public void remove(TransportMean o)
	{
		this.entities.remove(o.getId());
	} // end remove();

	public boolean canUpdate(Class<?> type, Object realObj, Object holder)
	{
		return true;
	} // end canUpdate();

	public boolean canRemove(Class<?> type, Object realObj)
	{
		return true;
	} // end canRemove();

	public void notifyUpdated(Class<?> type, Object realObj)
	{
		// null
	} // end notifyUpdated();

	public void notifyRemoved(Class<?> type, Object realObj)
	{
		// null
	} // end notifyRemoved();
} // end TransportMeanEntityStorage;
