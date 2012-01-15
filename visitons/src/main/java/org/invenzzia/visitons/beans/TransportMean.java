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
package org.invenzzia.visitons.beans;

import java.io.Serializable;

/**
 * This Java Bean describes the characteristics of the given mean of transport.
 *
 * @author Tomasz Jędrzejewski
 */
public class TransportMean implements Serializable
{
	/**
	 * The autoincremented identifier.
	 */
	private int id;
	/**
	 * The name of the mean of transport.
	 */
	private String name;
	/**
	 * Friction coefficient used in the physics simulation.
	 */
	private double frictionCoefficient;
	/**
	 * Is overtaking allowed?
	 */
	private boolean overtakingAllowed;
	/**
	 * The coefficient for passing the curves.
	 */
	private double curveCoefficient;

	public TransportMean()
	{
		this.name = "New mean of transport";
		this.frictionCoefficient = 0.005;
		this.overtakingAllowed = false;
		this.curveCoefficient = 0.0;
	} // end TransportMean();
	
	public int getId()
	{
		return this.id;
	} // end getId();

	public void setId(int id)
	{
		this.id = id;
	} // end setId();
	
	public String getName()
	{
		return this.name;
	} // end getName();
	
	public void setName(String name)
	{
		this.name = name;
	} // end setName();
	
	public double getFrictionCoefficient()
	{
		return this.frictionCoefficient;
	} // end getFrictionCoefficient();
	
	public void setFrictionCoefficient(double frictionCoefficient)
	{
		this.frictionCoefficient = frictionCoefficient;
	} // end setFrictionCoefficient();
	
	public boolean isOvertakingAllowed()
	{
		return this.overtakingAllowed;
	} // end isOvertakingAllowed();
	
	public void setOvertakingAllowed(boolean isAllowed)
	{
		this.overtakingAllowed = isAllowed;
	} // end setOvertakingAllowed();
	
	public double getCurveCoefficient()
	{
		return this.curveCoefficient;
	} // end getCurveCoefficient();
	
	public void setCurveCoefficient(double curveCoefficient)
	{
		this.curveCoefficient = curveCoefficient;
	} // end setCurveCoefficient();
	
	public void copyFrom(TransportMean newObject)
	{
		this.name = newObject.name;
		this.frictionCoefficient = newObject.frictionCoefficient;
		this.curveCoefficient = newObject.curveCoefficient;
		this.overtakingAllowed = newObject.overtakingAllowed;
	} // end copyFrom();
} // end TransportMean;
