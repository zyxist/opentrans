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
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.invenzzia.helium.domain.annotation.Identifier;
import org.invenzzia.helium.domain.annotation.MinDouble;

/**
 * Describes a single vehicle class, whose parameters are typical for a whole group of vehicles.
 *
 * Vehicle class is a Java Bean object.
 *
 * @author Tomasz Jędrzejewski
 */
public class VehicleClass implements Serializable {
	@Min(value = 0)
	@Identifier
	private int id;
	@NotNull
	@Size(min = 2, max = 30)
	private String name;
	@Valid
	@NotNull
	private TransportMean meanOfTransport = null;
	@MinDouble(value = 1.0)
	private double length;
	@Min(value = 1)
	@Max(value = 10)
	private byte nodeNumber;
	@Min(value = 0)
	private int mass;
	@Min(value = 0)
	private int power;
	@Min(value = 0)
	@Max(value = 1000)
	private int capacity;
	@MinDouble(value = 1.0)
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
		this.meanOfTransport = mean;
	} // end setMeanOfTransport();

	public double getLength() {
		return this.length;
	} // end getLength();

	public void setLength(double length) {
		this.length = length;
	} // end setLength();

	public byte getNodeNumber() {
		return this.nodeNumber;
	} // end getNodeNumber();

	public void setNodeNumber(byte nodeNumber) {
		this.nodeNumber = nodeNumber;
	} // end setNodeNumber();

	public int getMass() {
		return this.mass;
	} // end getMass();

	public void setMass(int mass) {
		this.mass = mass;
	} // end setMass();

	public int getPower() {
		return this.power;
	} // end getPower();

	public void setPower(int power) {
		this.power = power;
	} // end setPower();

	public int getCapacity() {
		return this.capacity;
	} // end getCapacity();

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	} // end setCapacity();

	public double getMaxSpeed() {
		return this.maxSpeed;
	} // end getMaxSpeed();

	public void setMaxSpeed(double maxSpeed) {
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
