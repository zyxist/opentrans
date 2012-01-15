/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.invenzzia.visitons.beans.entities;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.utils.entities.IEntityStorage;
import org.invenzzia.utils.entities.Relationship;
import org.invenzzia.utils.exception.PersistenceException;
import org.invenzzia.utils.exception.ValidationException;
import org.invenzzia.visitons.beans.TransportMean;
import org.invenzzia.visitons.beans.VehicleClass;

/**
 * A storage for managing vehicle class entities and their relationships.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleClassEntityStorage implements IEntityStorage<VehicleClass>
{
	private int iterator;
	private Map<Integer, VehicleClass> entities;
	private Relationship<TransportMean, VehicleClass> transportMeanRelationship;
	
	public VehicleClassEntityStorage()
	{
		this.entities = new LinkedHashMap<>();
		this.transportMeanRelationship = new Relationship<>();
	} // end TransportMeanEntityStorage();

	@Override
	public Class<?>[] listenFor()
	{
		return new Class<?>[]{ TransportMean.class }; 
	} // end listenFor();

	@Override
	public Collection<VehicleClass> getByRelationship(Object obj)
	{
		return this.transportMeanRelationship.getServantsFor((TransportMean) obj);
	} // end getByRelationship();
	
	public VehicleClass getById(int id)
	{
		return this.entities.get(id);
	} // end getById();
	
	public Collection<VehicleClass> getAll()
	{
		return this.entities.values();
	} // end getAll();
	
	public boolean contains(VehicleClass vc)
	{
		return this.transportMeanRelationship.contains(vc);
	} // end contains();
	
	public Collection<VehicleClass> getByRelation(Object obj)
	{
		if(obj instanceof TransportMean)
		{
			return this.transportMeanRelationship.getServantsFor((TransportMean) obj);
		}
		return null;
	} // end getByRelation();
	
	public void persist(VehicleClass object) throws ValidationException, PersistenceException
	{
		if(null == object.getMeanOfTransport())
		{
			throw new ValidationException("The vehicle class "+object.getName()+" does not have a mean of transport selected.", "transportMean");
		}
		if(this.transportMeanRelationship.contains(object))
		{
			throw new PersistenceException("This vehicle class is already persisted.");
		}
		object.setId(this.iterator);
		this.entities.put(this.iterator++, object);
		this.transportMeanRelationship.put(object.getMeanOfTransport(), object);
	} // end persist();
	
	public void update(VehicleClass object, VehicleClass newObject) throws ValidationException, PersistenceException
	{
		if(null == object.getMeanOfTransport())
		{
			throw new ValidationException("The vehicle class "+newObject.getName()+" does not have a mean of transport selected.", "transportMean");
		}
		try
		{
			object.copyFrom(newObject);
		}
		finally
		{
			this.transportMeanRelationship.makeConsistent(object.getMeanOfTransport(), object);
		}
	} // end update();
	
	public void remove(VehicleClass o)
	{
		this.entities.remove(o.getId());
		this.transportMeanRelationship.remove(o);
	} // end remove();

	@Override
	public boolean canUpdate(Class<?> type, Object realObj, Object holder)
	{
		return true;
	} // end canUpdate();

	@Override
	public boolean canRemove(Class<?> type, Object realObj)
	{
		if(type == TransportMean.class)
		{
			return !this.transportMeanRelationship.containsMaster((TransportMean) realObj);
		}
		return true;
	} // end canRemove();

	@Override
	public void notifyUpdated(Class<?> type, Object realObj)
	{
	} // end notifyUpdated();

	@Override
	public void notifyRemoved(Class<?> type, Object realObj)
	{
	} // end notifyRemoved();
} // end VehicleClassEntityStorage;
