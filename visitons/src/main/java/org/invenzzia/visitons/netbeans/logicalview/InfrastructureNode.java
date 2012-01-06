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
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *  * A presentation node for the infrastructure. It uses the NetBeans Nodes API.
 * 
 * @author Tomasz Jędrzejewski
 */
public class InfrastructureNode extends AbstractNode
{
	protected Image icon;
	
	public InfrastructureNode(VisitonsProject project)
	{
		super(Children.LEAF);
		this.setDisplayName("Infrastructure");
		
		IconManager iconManager = Lookup.getDefault().lookup(IconManager.class);
		this.icon = iconManager.getIconFor("lightbulb");
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
} // end InfrastructureNode;

