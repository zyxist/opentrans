/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
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

package org.invenzzia.opentrans.lightweight.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.visitons.network.World;

/**
 * For injecting the instances of {@link World}.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldProvider implements Provider<World> {
	@Inject
	private IProjectHolder projectHolder;

	@Override
	public World get() {
		return this.projectHolder.getCurrentProject().getWorld();
	}
}
