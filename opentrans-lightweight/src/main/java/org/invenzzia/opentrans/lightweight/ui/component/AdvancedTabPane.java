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

package org.invenzzia.opentrans.lightweight.ui.component;

import java.awt.Component;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * Extension of the original tab pane that allows closing the tabs and dragging
 * the tabs around.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AdvancedTabPane extends JTabbedPane {
	/**
	 * Icon for the tab close buttons.
	 */
	private Icon closeButtonIcon;
	/**
	 * Listeners that could handle tab management events.
	 */
	private Set<ITabListener> tabListeners = new LinkedHashSet<>();
	
	public void setCloseButtonIcon(Icon icon) {
		this.closeButtonIcon = icon;
	}
	
	public Icon getCloseButtonIcon() {
		return this.closeButtonIcon;
	}
	
	public void addTabListener(ITabListener listener) {
		this.tabListeners.add(listener);
	}
	
	public void removeTabListener(ITabListener listener) {
		this.tabListeners.remove(listener);
	}
	
	public void removeTabListeners() {
		this.tabListeners.clear();
	}

	/**
	 * Adds a new tab with the icon. Uses the customized header that supports
	 * closing the tab.
	 * 
	 * @param title Tab title.
	 * @param icon Tab icon.
	 * @param content Tab content.
	 * @return Tab index.
	 */
	public int createTab(String title, Icon icon, Component content) {
		TabHeader header = new TabHeader(title, icon);
		header.setManagingPane(this);
		header.setManagedComponent(content);
		header.setCloseIcon(this.closeButtonIcon);
		
		this.addTab(title, content);
		int idx = this.indexOfComponent(content);
		this.setTabComponentAt(idx, header);
		return idx;
	}
	
	/**
	 * Informs that the tab has been closed by the user.
	 * 
	 * @param tabComponent 
	 */
	public void tabCloseRequest(Component tabComponent) {		
		final TabEvent evt = new TabEvent(tabComponent, this.indexOfComponent(tabComponent));
		for(ITabListener listener: this.tabListeners) {
			listener.tabClosed(evt);
		}
		this.remove(tabComponent);
	}
	
	/**
	 * Allows receiving notification about closed and dragged tabs.
	 */
	public static interface ITabListener {
		public void tabClosed(TabEvent event);
		public void tabDragged(TabEvent event);
	}
	
	/**
	 * Carries information about a particular tab event.
	 */
	public static class TabEvent {
		private final Component tabComponent;
		private final int idx;
		
		public TabEvent(Component component, int idx) {
			this.tabComponent = component;
			this.idx = idx;
		}

		/**
		 * Returns the tab component that is affected by the event.
		 * @return Affected tab component.
		 */
		public Component getTabComponent() {
			return this.tabComponent;
		}

		/**
		 * Returns the index of the affected tab.
		 * 
		 * @return Affected tab index.
		 */
		public int getIdx() {
			return this.idx;
		}
	}
}
