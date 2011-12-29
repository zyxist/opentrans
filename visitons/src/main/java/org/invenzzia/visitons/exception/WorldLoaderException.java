/*
 * Visitons - transportation network simulation and visualization library.
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.invenzzia.visitons.exception;

/**
 * The runtime exception for the world loader indicating that for
 * some reason it cannot build the world from the given source.
 *
 * @author zyxist
 */
public class WorldLoaderException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WorldLoaderException(String message)
	{
		super(message);
	} // end WorldLoaderException();
	
	public WorldLoaderException(String message, Throwable nestedException)
	{
		super(message, nestedException);
	} // end WorldLoaderException();
} // end WorldLoaderException;