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
package org.invenzzia.visitons.project.nodes;

import java.awt.Image;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * Provides an easy access to the icon set.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
@ServiceProvider(service=IconManager.class)
public class IconManager
{
	/**
	 * The list of available icons read from the NB System Filesystem.
	 */
	private Map<String, Image> icons = null;
	/**
	 * The default icon.
	 */
	private String defaultIcon = "default";
	
	/**
	 * Creates the icon manager object and loads the icon set from the NB
	 * System Filesystem.
	 */
	public IconManager()
	{
		this.reload();
	} // end IconManager();
	
	/**
	 * Loads the icons from the NB System Filesystem API (Icons/Visitons folder).
	 * If the icon is already loaded, the image object is kept untouched.
	 * 
	 */
	public void reload()
	{
		FileObject visitonsIconSet = FileUtil.getConfigFile("Icons/Visitons");
		
		Map<String, Image> newMap = new LinkedHashMap<String, Image>();
		for(FileObject obj: visitonsIconSet.getChildren())
		{
			if(!obj.isData())
			{
				break;
			}
			try
			{
				if(null == this.icons || !this.icons.containsKey(obj.getName()))
				{
					newMap.put(obj.getName(), ImageIO.read(obj.getInputStream()));
				}
				else
				{
					newMap.put(obj.getName(), this.icons.get(obj.getName()));
				}
			}
			catch(IOException exception)
			{
				/* skip such an icon */
			}
		}
		this.icons = newMap;
	} // end reload();
	
	/**
	 * @param key The name of the default icon.
	 * @return Fluent interface.
	 */
	public IconManager setDefaultIconKey(String key)
	{
		if(this.icons.containsKey(key))
		{
			this.defaultIcon = key;
		}
		return this;
	} // end setDefaultIcon();
	
	/**
	 * @return The name of the default icon.
	 */
	public String getDefaultIconKey()
	{
		return this.defaultIcon;
	} // end getDefaultIconKey();
	
	/**
	 * Locates an icon for the given key. If the icon is not present, a default
	 * icon is returned. If the default icon does not exist, the method picks
	 * up a random default icon.
	 * 
	 * @param key The icon name.
	 * @return The icon image.
	 */
	public Image getIconFor(String key)
	{
		if(!this.icons.containsKey(key))
		{
			if(!this.icons.containsKey(this.defaultIcon))
			{
				this.pickUpRandomDefaultIcon();
			}
			key = this.defaultIcon;
			if(null == key)
			{
				return null;
			}
		}
		return this.icons.get(key);
	} // end getIconFor();
	
	/**
	 * Well, this is not a true randomness, but works :).
	 */
	protected void pickUpRandomDefaultIcon()
	{
		this.defaultIcon = null;
		for(String key: this.icons.keySet())
		{
			this.defaultIcon = key;
			break;
		}
	} // end pickUpRandomDefaultIcon();
} // end IconManager;
