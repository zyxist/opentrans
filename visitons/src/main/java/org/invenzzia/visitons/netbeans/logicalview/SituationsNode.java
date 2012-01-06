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
import org.openide.util.lookup.Lookups;

/**
 * A presentation node for the situation. It uses the NetBeans Nodes API.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SituationsNode extends AbstractNode
{
	protected Image openedIcon;
	protected Image closedIcon;
	
	public SituationsNode(VisitonsProject project)
	{
		super(Children.create(new SituationsChildFactory(project.getSituationManager()), true), Lookups.singleton(project));
		this.setDisplayName("Situations");
		
		IconManager iconManager = Lookup.getDefault().lookup(IconManager.class);
		this.openedIcon = iconManager.getIconFor("package-opened");
		this.closedIcon = iconManager.getIconFor("package");
	} // end SituationsNode();
	
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
		return this.closedIcon;
	} // end getIcon();
	
	@Override
	public Image getOpenedIcon(int type)
	{
		return this.openedIcon;
	} // end getIcon();
} // end SituationsNode;

