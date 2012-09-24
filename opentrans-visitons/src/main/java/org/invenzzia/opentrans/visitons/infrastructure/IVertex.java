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
package org.invenzzia.opentrans.visitons.infrastructure;

import java.util.Map;

/**
 * Description here.
 *
 * @author Tomasz Jędrzejewski
 */
public interface IVertex<T extends IVertex> extends ICopiable<T> {
	/**
	 * @return Unique vertex ID.
	 */
	public long getId();
	/**
	 * Sets the unique vertex ID. This method shall be used by the {@link Graph} internal code.
	 * @param id 
	 */
	public void setId(long id);
	/**
	 * @return X coordinate in the segment units.
	 */
	public double x();
	/**
	 * @return Y coordinate in the segment units.
	 */
	public double y();
	
	public int getTrackCount();
	
	public ITrack[] getTracks();
	
	public void setTrack(int id, ITrack track);
	
	public void registerUpdate(double x, double y);
	
	public boolean isUpdatePossible();
	
	public void applyUpdate();
	
	public void rollbackUpdate();
	
	public void markAsDeleted();
	
	public boolean isDeleted();
}
