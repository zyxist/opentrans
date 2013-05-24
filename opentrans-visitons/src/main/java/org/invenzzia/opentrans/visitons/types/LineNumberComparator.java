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

package org.invenzzia.opentrans.visitons.types;

import java.io.Serializable;
import java.util.Comparator;
import net.jcip.annotations.Immutable;

/**
 * Defines the sorting order for line numbers.
 * 
 * @author Tomasz Jędrzejewski
 */
@Immutable
public class LineNumberComparator implements Comparator<LineNumber>, Serializable {
	private static final LineNumberComparator DEFAULT_INSTANCE = new LineNumberComparator();
	
	public static LineNumberComparator get() {
		return DEFAULT_INSTANCE;
	}
	
	@Override
	public int compare(LineNumber o1, LineNumber o2) {
		if(o1.isNumericalPresent() != o2.isNumericalPresent()) {
			if(o1.isNumericalPresent()) {
				return -1;
			}
			return 1;
		}
		// Numerical part goes in the first place.
		if(o1.isNumericalPresent()) {
			int result = o1.getNumerical() - o2.getNumerical();
			if(result != 0) {
				return result;
			}
		}
		if((o1.getAlphanumerical() != null) != (o2.getAlphanumerical() != null)) {
			if(o1.getAlphanumerical() != null) {
				return 1;
			}
			return -1;
		}		
		if(null != o1.getAlphanumerical()) {
			String a1 = o1.getAlphanumerical();
			String a2 = o2.getAlphanumerical();
			int length = Math.min(a1.length(), a2.length());
			for(int i = 0; i < length; i++) {
				int diff = a1.charAt(i) - a2.charAt(i);
				if(diff != 0) {
					return diff;
				}
			}
		}
		return 0;
	}
}
