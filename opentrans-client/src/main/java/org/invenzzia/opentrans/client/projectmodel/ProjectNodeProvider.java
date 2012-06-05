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
package org.invenzzia.opentrans.client.projectmodel;

import org.invenzzia.helium.gui.ui.trees.ISubnodeProvider;
import org.invenzzia.opentrans.visitons.VisitonsProject;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectNodeProvider implements ISubnodeProvider {
	private Object nodes[];
	
	public ProjectNodeProvider(VisitonsProject project) {
		this.nodes = new Object[] {
			project.getWorld()
		};
	}

	@Override
	public Object getChild(Object parent, int childIdx) {
		return this.nodes[childIdx];
	}

	@Override
	public int getChildCount(Object parent) {
		return this.nodes.length;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		for(int i = 0; i < this.nodes.length; i++) {
			if(this.nodes[i] == child) {
				return i;
			}
		}
		return -1;
	}
}
