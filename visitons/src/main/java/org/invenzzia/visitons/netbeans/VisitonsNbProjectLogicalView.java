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
package org.invenzzia.visitons.netbeans;

import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.nodes.Node;

/**
 * Provides a logical view of the Simulation Netbeans project.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisitonsNbProjectLogicalView implements LogicalViewProvider
{
	private final VisitonsNbProject project;
	
	public VisitonsNbProjectLogicalView(VisitonsNbProject project)
	{
		this.project = project;
	} // end VisitonsNbProjectLogicalView();
	
	@Override
	public Node createLogicalView()
	{
		return null;
	} // end createLogicalView();
	
	@Override
	public Node findPath(Node root, Object target)
	{
		return null;
	} // end findPath();
} // end VisitonsNbProjectLogicalView;

