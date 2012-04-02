/*
 * Visitons - public transport simulation engine
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
package org.invenzzia.opentrans.visitons.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;

/**
 * Describes a single vehicle class, whose parameters are typical for a whole group of vehicles.
 *
 * Vehicle class is a Java Bean object.
 *
 * @author Tomasz Jędrzejewski
 */
public class VehicleClass implements Serializable {
	private int id;
	private String name;
	private TransportMean meanOfTransport = null;
	private double length;
	private byte nodeNumber;
	private int mass;
	private int power;
	private int capacity;
	private double maxSpeed;

	public VehicleClass() {
		this.name = "";
	} // end VehicleClass();

	public int getId() {
		return this.id;
	} // end getId();

	public void setId(int iterator) {
		this.id = iterator;
	} // end setId();

	public String getName() {
		return this.name;
	} // end getName();

	public void setName(String name) {
		this.name = name;
	} // end setName();

	public TransportMean getMeanOfTransport() {
		return this.meanOfTransport;
	} // end getMeanOfTransport();

	public void setMeanOfTransport(TransportMean mean) throws PropertyVetoException {
		if(null == mean) {
			throw new PropertyVetoException("Cannot reset the mean of transport reference to NULL.",
				new PropertyChangeEvent(this, "meanOfTransport", this.meanOfTransport, meanOfTransport));
		}
		this.meanOfTransport = mean;
	} // end setMeanOfTransport();

	public double getLength() {
		return this.length;
	} // end getLength();

	public void setLength(double length) throws PropertyVetoException {
		if(length <= 0.0) {
			throw new PropertyVetoException("Cannot set a negative length", new PropertyChangeEvent(this, "length", this.length, length));
		}
		this.length = length;
	} // end setLength();

	public byte getNodeNumber() {
		return this.nodeNumber;
	} // end getNodeNumber();

	public void setNodeNumber(byte nodeNumber) throws PropertyVetoException {
		if(nodeNumber <= 0) {
			throw new PropertyVetoException("Cannot set a negative node number", new PropertyChangeEvent(this, "nodeNumber", this.nodeNumber, nodeNumber));
		}
		this.nodeNumber = nodeNumber;
	} // end setNodeNumber();

	public int getMass() {
		return this.mass;
	} // end getMass();

	public void setMass(int mass) throws PropertyVetoException {
		if(mass <= 0) {
			throw new PropertyVetoException("Cannot set a negative mass", new PropertyChangeEvent(this, "mass", this.mass, mass));
		}
		this.mass = mass;
	} // end setMass();

	public int getPower() {
		return this.power;
	} // end getPower();

	public void setPower(int power) throws PropertyVetoException {
		if(power <= 0) {
			throw new PropertyVetoException("Cannot set a negative engine power", new PropertyChangeEvent(this, "power", this.power, power));
		}
		this.power = power;
	} // end setPower();

	public int getCapacity() {
		return this.capacity;
	} // end getCapacity();

	public void setCapacity(int capacity) throws PropertyVetoException {
		if(capacity <= 0) {
			throw new PropertyVetoException("Cannot set a negative capacity", new PropertyChangeEvent(this, "capacity", this.capacity, capacity));
		}
		this.capacity = capacity;
	} // end setCapacity();

	public double getMaxSpeed() {
		return this.maxSpeed;
	} // end getMaxSpeed();

	public void setMaxSpeed(double maxSpeed) throws PropertyVetoException {
		if(maxSpeed <= 0.0) {
			throw new PropertyVetoException("Cannot set a negative maximum speed", new PropertyChangeEvent(this, "maxSpeed", this.maxSpeed, maxSpeed));
		}
		this.maxSpeed = maxSpeed;
	} // end setMaxSpeed();

	public void copyFrom(VehicleClass newObject) {
		this.meanOfTransport = newObject.meanOfTransport;
		this.capacity = newObject.capacity;
		this.length = newObject.length;
		this.mass = newObject.mass;
		this.maxSpeed = newObject.maxSpeed;
		this.name = newObject.name;
		this.nodeNumber = newObject.nodeNumber;
		this.power = newObject.power;
	} // end copyFrom();
} // end VehicleClass;
