/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.editing.common;

import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.network.World;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class WorldResizeCmd implements ICommand {
	public static final int RESIZE_NORTH = 0;
	public static final int RESIZE_EAST = 1;
	public static final int RESIZE_SOUTH = 2;
	public static final int RESIZE_WEST = 3;
	/**
	 * Direction of the resize operation.
	 */
	protected final int direction;
	
	/**
	 * Constructs the resizing command.
	 * 
	 * @param direction The direction of resize.
	 */
	public WorldResizeCmd(int direction) {
		this.direction = direction;
	}

	/**
	 * Extends the world in the given direction.
	 * 
	 * @param world
	 * @param direction 
	 */
	protected void performExtend(World world, int direction) {
		switch(direction) {
			case RESIZE_NORTH:
				world.extendVertically(World.VerticalDir.UP);
				break;
			case RESIZE_SOUTH:
				world.extendVertically(World.VerticalDir.DOWN);
				break;
			case RESIZE_WEST:
				world.extendHorizontally(World.HorizontalDir.LEFT);
				break;
			case RESIZE_EAST:
				world.extendHorizontally(World.HorizontalDir.RIGHT);
				break;
		}
	}
	
	/**
	 * Shrinks the world in the given direction.
	 * 
	 * @param world
	 * @param direction 
	 */
	protected void performShrink(World world, int direction) throws WorldException {
		switch(direction) {
			case RESIZE_NORTH:
				world.shrinkVertically(World.VerticalDir.UP);
				break;
			case RESIZE_SOUTH:
				world.shrinkVertically(World.VerticalDir.DOWN);
				break;
			case RESIZE_WEST:
				world.shrinkHorizontally(World.HorizontalDir.LEFT);
				break;
			case RESIZE_EAST:
				world.shrinkHorizontally(World.HorizontalDir.RIGHT);
				break;
		}
	}
}
