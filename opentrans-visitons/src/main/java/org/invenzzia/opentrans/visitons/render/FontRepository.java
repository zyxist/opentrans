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

package org.invenzzia.opentrans.visitons.render;

import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import org.invenzzia.opentrans.visitons.geometry.Geometry;

/**
 * If we want to display something, we must have a font repository, which is recalculated
 * if the zoom levels are changed.
 * 
 * @author Tomasz Jędrzejewski
 */
public class FontRepository {
	private Map<String, FontInfo> fontMap = new LinkedHashMap<>();
	private double currentZoom;
	
	/**
	 * Registers a new font 
	 * @param family
	 * @param defaultSize
	 * @param bold 
	 */
	public void addFont(String key, String family, int defaultSize, boolean bold) {
		this.fontMap.put(key, new FontInfo(family, defaultSize, bold));
	}
	
	/**
	 * Retrieves a font with the given key.
	 * 
	 * @param key
	 * @return Font stored under that key.
	 */
	public Font getFont(String key) {
		FontInfo info = this.fontMap.get(key);
		if(null == info) {
			throw new IllegalArgumentException("Invalid font name: '"+key+"'");
		}
		return info.font;
	}
	
	/**
	 * Recalculates all the fonts to give them a better size.
	 * 
	 * @param newZoom 
	 */
	public void recalculateFonts(double newZoom) {
		if(!Geometry.isZero(this.currentZoom - newZoom)) {
			this.currentZoom = newZoom;
			for(FontInfo fontInfo: this.fontMap.values()) {
				fontInfo.updateSize(newZoom);
			}
		}
	}
	
	class FontInfo {
		Font font;
		String family;
		boolean bold;
		int defaultSize;
		
		FontInfo(String family, int defaultSize, boolean bold) {
			this.family = family;
			this.defaultSize = defaultSize;
			this.bold = bold;
		}
		
		void updateSize(double newSize) {
			int result = (int) (this.defaultSize / newSize);
			if(result < 6) {
				result = 6;
			}
			this.font = new Font(this.family, bold ? Font.BOLD : Font.PLAIN, result);
		} 
	}
}
