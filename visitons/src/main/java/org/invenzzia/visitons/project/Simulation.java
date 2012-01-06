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
package org.invenzzia.visitons.project;

import java.util.Properties;

import org.invenzzia.utils.exception.ValidationException;
import org.invenzzia.utils.persistence.IPersistable;

/**
 * Description here.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public class Simulation implements IPersistable<String>
{
	@Override
	public String getKey()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end getKey();

	@Override
	public void setKey(String key)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end setKey();

	@Override
	public void validate() throws ValidationException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end validate();

	@Override
	public void restore(Properties props) throws ValidationException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end restore();

	@Override
	public Properties persist()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	} // end persist();
	
} // end Simulation;

