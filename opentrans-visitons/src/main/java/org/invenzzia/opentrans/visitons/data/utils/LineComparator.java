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
import org.invenzzia.opentrans.visitons.data.Line;
import org.invenzzia.opentrans.visitons.types.LineNumberComparator;

/**
 * Comparator for {@link Line} objects that allows them being sorted
 * by their line number.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LineComparator implements Comparator<Line>, Serializable {
	private LineNumberComparator comparator = LineNumberComparator.get();
	private static final LineComparator DEFAULT_COMPARATOR = new LineComparator();

	public static LineComparator get() {
		return DEFAULT_COMPARATOR;
	}

	@Override
	public int compare(Line o1, Line o2) {
		return this.comparator.compare(o1.getNumber(), o2.getNumber());
	}
}
