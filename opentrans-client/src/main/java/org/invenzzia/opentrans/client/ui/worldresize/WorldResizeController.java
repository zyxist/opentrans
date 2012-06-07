/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.ui.worldresize;

import org.invenzzia.helium.gui.mvc.IController;

/**
 * The controller for the world resize responds to the extend/shrink
 * buttons and controls the state indicating, how we should change
 * them.
 * 
 * If a resizing event arrives, it delegates it to the model active
 * object which executes it in the appropriate thread.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldResizeController implements IController<WorldResizeView> {
	private WorldResizeView view;
	
	@Override
	public void attachView(WorldResizeView object) {
		this.view = object;
	}

	@Override
	public void detachView(WorldResizeView object) {
		this.view = null;
	}
}
