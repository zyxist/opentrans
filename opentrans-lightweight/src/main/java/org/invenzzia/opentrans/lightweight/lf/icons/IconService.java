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

package org.invenzzia.opentrans.lightweight.lf.icons;

import com.google.inject.Singleton;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Icon manager is a single access point for all the icons in the system.
 * The class is not thread-safe. It should be accessed from the Swing dispatch
 * thread only.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class IconService {
	/**
	 * Loaded icons.
	 */
	private Map<String, Icon> icons = new LinkedHashMap<>();

	/**
	 * Unloaded icons.
	 */
	private Map<String, URL> iconReferences = new LinkedHashMap<>();
	/**
	 * Icon returned if some other icon has not been found.
	 */
	private Icon unknownIcon;
	
	/**
	 * Loads an unknown icon which will be returned, if other icons are not 
	 * found.
	 * 
	 * @param unknownUrl 
	 */
	public void setUnknownIcon(URL unknownUrl) {
		this.unknownIcon = this.loadIcon(unknownUrl);
	}
	
	/**
	 * Performs an icon preloading.
	 * 
	 * @param id The string ID used for the management.
	 * @param iconUrl 
	 */
	public void preloadIcon(String id, URL iconUrl) {
		this.icons.put(id, this.loadIcon(iconUrl));
	}
	
	/**
	 * Marks that the icon with the given ID can be found under the given URL.
	 * It will be loaded if something will reference to it.
	 * 
	 * @param id
	 * @param iconUrl
	 */
	public void addIcon(String id, URL iconUrl) {
		this.iconReferences.put(id, iconUrl);
	}
	
	/**
	 * Returns the icon for the given icon ID. If the icon has not been found, an 'unknown' icon
	 * is returned instead. If the icon is not loaded yet, it is preloaded.
	 * 
	 * @param id The icon ID.
	 * @return The loaded icon.
	 */
	public Icon getIcon(String id) {
		Icon icon = this.icons.get(id);
		if(null == icon) {
			URL uri = this.iconReferences.get(id);
			if(null == uri) {
				return this.unknownIcon;
			}
			icon = this.loadIcon(uri);
			this.icons.put(id, icon);
		}
		return icon;
	}
	
	private Icon loadIcon(URL url) {
		return new ImageIcon(url);
	}
}
