/*
 * Visitons - public transport simulation engine
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
package org.invenzzia.opentrans.visitons.data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.invenzzia.helium.domain.annotation.RelationshipMaster;

/**
 * Each stop can consist of multiple platforms. A platform is a single point
 * of handling the vehicle. The more platforms, the more vehicles can be
 * served at the same time.
 * 
 * @author Tomasz Jędrzejewski
 */
public class Platform {
	@Valid @RelationshipMaster
	private Stop stop;
	@Min(value = 1)
	@Max(value = 100)
	private byte number;
}
