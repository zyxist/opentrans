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
package org.invenzzia.opentrans.visitonsbundle.logicalview;

import java.awt.Image;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.invenzzia.visitons.project.VisitonsProject;
import org.invenzzia.opentrans.visitonsbundle.IconManager;
import org.invenzzia.opentrans.visitonsbundle.listeners.IMapNodeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *  * A presentation node for the map. It uses the NetBeans Nodes API.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MapNode extends AbstractNode
{
	protected Image icon;
	protected VisitonsProject project;
	
	public MapNode(VisitonsProject project)
	{
		super(Children.LEAF);
		
		this.project = project;
		this.setDisplayName("Map");
		
		IconManager iconManager = Lookup.getDefault().lookup(IconManager.class);
		this.icon = iconManager.getIconFor("map");
	} // end ProjectNode();
	
	@Override
	public boolean canCut()
	{
		return false;
	} // end canCut();
	
	@Override
	public boolean canDestroy()
	{
		return false;
	} // end canDestroy();
	
	@Override
	public boolean canRename()
	{
		return false;
	} // end canRename();
	
	@Override
	public boolean canCopy()
	{
		return false;
	} // end canCopy();
	
	@Override
	public Image getIcon(int type)
	{
		return this.icon;
	} // end getIcon();
	
	@Override
	public Action[] getActions(boolean popup)
	{
		return new Action[] {
			new AbstractAction()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					IMapNodeListener cookie = Lookup.getDefault().lookup(IMapNodeListener.class);
					if(null != cookie)
					{
						cookie.openWorld(MapNode.this.project.getWorld());
					}
				} // end actionPerformed();		
			} // end Action;
		};
	} // end getActions();
	
	@Override
	public Action getPreferredAction()
	{
		return this.getActions(false)[0];
	} // end getPreferredAction();
} // end MapNode;