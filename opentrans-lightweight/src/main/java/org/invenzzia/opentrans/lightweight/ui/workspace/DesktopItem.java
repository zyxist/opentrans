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
 * Describes the single tab in the desktop manager.
 * 
 * @author Tomasz Jędrzejewski
 */
public final class DesktopItem {
	private final String title;
	private final Icon icon;
	private final JPanel content;
	
	public DesktopItem(String title, JPanel content) {
		this.title = title;
		this.icon = null;
		this.content = content;
	}
	
	public DesktopItem(String title, Icon icon, JPanel content) {
		this.title = title;
		this.icon = icon;
		this.content = content;
	}

	public String getTitle() {
		return this.title;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public JPanel getContent() {
		return this.content;
	}
}
