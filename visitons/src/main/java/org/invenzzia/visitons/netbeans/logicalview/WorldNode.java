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
package org.invenzzia.visitons.netbeans.logicalview;

import java.awt.Image;

import org.invenzzia.visitons.project.VisitonsProject;
import org.invenzzia.visitons.project.nodes.IconManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * A presentation node for the world. It uses the NetBeans Nodes
 * API.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldNode extends AbstractNode
{
	protected Image icon;
	
	public WorldNode(VisitonsProject project)
	{	
		super(new StaticChildrenCollection(new Node[]{
			new MapNode(project),
			new InfrastructureNode(project)
		}), Lookups.singleton(project));
		this.setDisplayName("World");
		
		IconManager iconManager = Lookup.getDefault().lookup(IconManager.class);
		this.icon = iconManager.getIconFor("world");
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
	public Image getOpenedIcon(int type)
	{
		return this.icon;
	} // end getOpenedIcon();
} // end WorldNode;

