/*
 * Copyright (C) 2011 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.visitons.netbeans.logicalview;

import java.awt.Image;

import org.invenzzia.visitons.project.VisitonsProject;
import org.invenzzia.visitons.project.nodes.IconManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MapNode extends AbstractNode
{
	protected Image icon;
	
	public MapNode(VisitonsProject project)
	{
		super(Children.LEAF);
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
} // end MapNode;

