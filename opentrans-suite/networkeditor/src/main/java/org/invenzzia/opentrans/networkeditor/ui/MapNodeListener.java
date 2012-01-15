/*
 * OpenTrans - public transport simulator
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
package org.invenzzia.opentrans.networkeditor.ui;

import org.invenzzia.opentrans.visitonsbundle.listeners.IMapNodeListener;
import org.invenzzia.visitons.visualization.World;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 * This an implementation of a listener that listens for the Map node
 * actions.
 * 
 * @author Tomasz Jędrzejewski
 */
@ServiceProvider(service=IMapNodeListener.class)
public class MapNodeListener implements IMapNodeListener
{
	@Override
	public void openWorld(World world)
	{
		WindowManager manager = Lookup.getDefault().lookup(WindowManager.class);
		// The top component...
		NetworkEditorTopComponent component = (NetworkEditorTopComponent) manager.findTopComponent("NetworkEditorTopComponent");
		if(!component.isOpened())
		{
			component.open();
		}
		component.requestActive();
		component.getCamera().setWorld(world);
	} // end openWorld();
} // end MapNodeListener;
