/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.data.utils;

import java.io.Serializable;
import java.util.Comparator;
import org.invenzzia.opentrans.visitons.data.Route.RouteRecord;
import org.invenzzia.opentrans.visitons.types.RouteNumberComparator;

/**
 * For making ordered lists of routes.
 * 
 * @author Tomasz Jędrzejewski
 */
public class RouteRecordComparator implements Comparator<RouteRecord>, Serializable {
	private RouteNumberComparator comparator = RouteNumberComparator.get();
	private static final RouteRecordComparator DEFAULT_COMPARATOR = new RouteRecordComparator();

	public static RouteRecordComparator get() {
		return DEFAULT_COMPARATOR;
	}

	@Override
	public int compare(RouteRecord o1, RouteRecord o2) {
		return this.comparator.compare(o1.getNumber(), o2.getNumber());
	}
}
