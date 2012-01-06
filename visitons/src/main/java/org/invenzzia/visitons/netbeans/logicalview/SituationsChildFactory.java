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

import java.util.List;
import org.invenzzia.utils.persistence.IPersistableManager;
import org.invenzzia.visitons.project.Situation;
import org.openide.nodes.ChildFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
class SituationsChildFactory extends ChildFactory<Situation>
{

	public SituationsChildFactory(IPersistableManager<String, Situation> situationManager)
	{
	} // end SituationsChildFactory();

	@Override
	protected boolean createKeys(List<Situation> list)
	{
		return true;
	} // end createKeys();
} // end SituationsChildFactory;

