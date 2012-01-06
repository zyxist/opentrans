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
 * A presentation node for the Transportation project. It uses the NetBeans Nodes
 * API.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public class ProjectNode extends AbstractNode
{
	protected Image projectIcon;
	
	public ProjectNode(VisitonsProject project)
	{
		super(new StaticChildrenCollection(new Node[]{
			new WorldNode(project),
			new SituationsNode(project),
			new SimulationsNode(project)
		}), Lookups.singleton(project));
		this.setDisplayName(project.getName());
		
		IconManager iconManager = Lookup.getDefault().lookup(IconManager.class);
		this.projectIcon = iconManager.getIconFor("project");
	} // end ProjectNode();
	
	@Override
	public boolean canCut()
	{
		return false;
	} // end canCut();
	
	@Override
	public boolean canDestroy()
	{
		return true;
	} // end canDestroy();
	
	@Override
	public boolean canRename()
	{
		return true;
	} // end canRename();
	
	@Override
	public boolean canCopy()
	{
		return false;
	} // end canCopy();
	
	@Override
	public Image getIcon(int type)
	{
		return this.projectIcon;
	} // end getIcon();
	
	@Override
	public Image getOpenedIcon(int type)
	{
		return this.projectIcon;
	} // end getIcon();
} // end ProjectNode;
