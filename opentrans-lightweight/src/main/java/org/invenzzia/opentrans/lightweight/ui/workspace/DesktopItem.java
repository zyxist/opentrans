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

package org.invenzzia.opentrans.lightweight.ui.workspace;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Describes the single tab in the desktop manager, and carries its
 * data managed by the tab factory.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class DesktopItem {
	/**
	 * The tab title.
	 */
	private final String title;
	/**
	 * Optional tab icon.
	 */
	private final Icon icon;
	/**
	 * Displayed panel.
	 */
	private final JPanel content;
	/**
	 * Additional optional metadata associated with the panel.
	 */
	private final Object metadata;
	
	/**
	 * Creates a desktop item without an icon and with no metadata.
	 * 
	 * @param title Tab title.
	 * @param content The displayed tab content.
	 */
	public DesktopItem(String title, JPanel content) {
		this(title, content, null);
	}
	
	/**
	 * Creates a desktop item with no metadata.
	 * 
	 * @param title Tab title.
	 * @param icon The displayed tab icon.
	 * @param content The displayed tab content.
	 */
	public DesktopItem(String title, Icon icon, JPanel content) {
		this(title, icon, content, null);
	}
	
	/**
	 * Creates a dekstop item with metadata and without an icon.
	 * 
	 * @param title Tab title.
	 * @param content The displayed tab content.
	 * @param metadata Any extra metadata required by the tab factory to be preserved.
	 */
	public DesktopItem(String title, JPanel content, Object metadata) {
		this.title = title;
		this.icon = null;
		this.content = content;
		this.metadata = metadata;
	}
	
	/**
	 * Creates a dekstop item with the icon and the metadata.
	 * 
	 * @param title Tab title.
	 * @param icon The displayed tab icon.
	 * @param content The displayed tab content.
	 * @param metadata Any extra metadata required by the tab factory to be preserved.
	 */
	public DesktopItem(String title, Icon icon, JPanel content, Object metadata) {
		this.title = title;
		this.icon = icon;
		this.content = content;
		this.metadata = metadata;
	}

	/**
	 * Returns the title of the tab.
	 * 
	 * @return Tab title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Returns the icon of the tab.
	 * 
	 * @return Tab icon.
	 */
	public Icon getIcon() {
		return this.icon;
	}

	/**
	 * Returns the content of the tab.
	 * 
	 * @return 
	 */
	public JPanel getContent() {
		return this.content;
	}
	
	/**
	 * Returns the metadata associated with the tab casted to the specified type.
	 * The metadata must exist.
	 * 
	 * @param type Expected type of the metadata.
	 * @return The extra metadata.
	 */
	public <T> T getMetadata(Class<T> type) {
		if(null == this.metadata) {
			throw new IllegalStateException("The desktop item '"+this.title+"' has no associated metadata.");
		}
		return (T) this.metadata;
	}
}
