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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.inject.Singleton;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.invenzzia.opentrans.lightweight.ui.component.AdvancedTabPane;
import org.invenzzia.opentrans.lightweight.ui.component.AdvancedTabPane.ITabListener;
import org.invenzzia.opentrans.lightweight.ui.component.AdvancedTabPane.TabEvent;

/**
 * The manager that has an ability to manage the tabs on the desktop.
 * It assumes that there can be only one tab of a given type in the tab pane,
 * so the tabs are identified by unique keys. A key is a class that describes
 * the content of the pane (it extends the {@link JPanel} class).
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class DesktopManager implements ITabListener {
	/**
	 * Map of factories that are able to initialize and destroy the given tab.
	 */
	private Map<Class<? extends JPanel>, IDesktopPaneFactory> factories;
	/**
	 * Map of currently opened tabs.
	 */
	private Map<Class<? extends JPanel>, DesktopItem> keyItems;
	/**
	 * The managed Swing tabbed pane.
	 */
	private AdvancedTabPane managedPane;
	
	public DesktopManager() {
		this.factories = new LinkedHashMap<>();
		this.keyItems = new LinkedHashMap<>();
	}
	
	/**
	 * Installs a tabbed pane that will be managed by this desktop manager.
	 * Calling this method is necessary to perform any further operation.
	 * 
	 * @param pane The pane, where the tabs will be added to.
	 */
	public void setManagedPane(AdvancedTabPane pane) {
		if(null != this.managedPane) {
			this.managedPane.removeTabListener(this);
		}
		this.managedPane = pane;
		if(null != this.managedPane) {
			this.managedPane.addTabListener(this);
		}
	}
	
	/**
	 * Returns the managed Swing component.
	 * 
	 * @return Managed Swing tabbed pane.
	 */
	public JTabbedPane getManagedPane() {
		return this.managedPane;
	}
	
	/**
	 * Each tab type has a factory that knows, how to initialize it and how to create
	 * controllers for it. This method allows registering a factory for the tab type.
	 * 
	 * @param key Unique key of the tab.
	 * @param factory Factory that initializes the tab.
	 */
	public void registerFactory(Class<? extends JPanel> key, IDesktopPaneFactory factory) {
		if(this.factories.containsKey(key)) {
			throw new IllegalArgumentException("Specified desktop tab key already exists.");
		}
		this.factories.put(key, factory);
	}
	
	/**
	 * Creates the new tab from the registered type of panel.
	 * 
	 * @param key The key of the panel.
	 */
	public void createItem(Class<? extends JPanel> key) {
		if(null == this.managedPane) {
			throw new IllegalStateException("The managed pane is not registered.");
		}
		
		IDesktopPaneFactory factory = this.factories.get(key);
		if(null == factory) {
			throw new IllegalArgumentException("The factory with the specified key '"+key.getSimpleName()+"' does not exist.");
		}
		DesktopItem di = Preconditions.checkNotNull(factory.createDesktopItem(), "The desktop item factory '"+key.getSimpleName()+"' returned NULL.");
		this.keyItems.put(key, di);
		this.managedPane.createTab(di.getTitle(), null, di.getContent());
		this.setFocus(key);
	}
	
	/**
	 * Checks, if the tab under the given key is currently created.
	 * 
	 * @param key Tab key.
	 * @return True, if the tab exists at the moment.
	 */
	public boolean isCreated(Class<? extends JPanel> key) {
		return this.keyItems.containsKey(key);
	}
	
	/**
	 * Removes the tab associated with the given key.
	 * 
	 * @param key Unique tab key.
	 */
	public void destroyItem(Class<? extends JPanel> key) {
		if(null == this.managedPane) {
			throw new IllegalStateException("The managed pane is not registered.");
		}
		IDesktopPaneFactory factory = this.factories.get(key);
		if(null == factory) {
			throw new IllegalArgumentException("The factory with the specified key '"+key.getSimpleName()+"' does not exist.");
		}
		DesktopItem di = this.keyItems.get(key);
		factory.destroyDesktopItem(di);
		this.managedPane.remove(di.getContent());
		this.keyItems.remove(key);
	}

	/**
	 * Sets the focus on the tab identified by the given key. If it does not exist,
	 * it is created.
	 * 
	 * @param key Unique tab key.
	 */
	public void setFocus(Class<? extends JPanel> key) {
		if(null == this.managedPane) {
			throw new IllegalStateException("The managed pane is not registered.");
		}
		DesktopItem di = this.keyItems.get(key);
		if(null == di) {
			this.createItem(key);
		} else {
			this.managedPane.setSelectedIndex(this.managedPane.indexOfComponent(di.getContent()));
		}
	}
	
	/**
	 * Executes the given predicate for all the desktop item factories.
	 * 
	 * @param predicate 
	 */
	public void forAllFactories(Predicate<IDesktopPaneFactory> predicate) {
		for(IDesktopPaneFactory factory: this.factories.values()) {
			predicate.apply(factory);
		}
	}

	/**
	 * Removes all tabs currently opened on the tab pane.
	 */
	public void destroyAllItems() {
		if(null == this.managedPane) {
			throw new IllegalStateException("The managed pane is not registered.");
		}
		for(Map.Entry<Class<? extends JPanel>, DesktopItem> di: this.keyItems.entrySet()) {
			IDesktopPaneFactory factory = this.factories.get(di.getKey());
			factory.destroyDesktopItem(di.getValue());
		}
		this.managedPane.removeAll();
		this.keyItems.clear();
	}

	/**
	 * Responds on the tab event related to closing an active tab. It updates the internal
	 * state of the desktop manager.
	 * 
	 * @param event 
	 */
	@Override
	public void tabClosed(TabEvent event) {
		Component component = event.getTabComponent();
		Class<? extends JPanel> keyToRemove = null;
		for(Map.Entry<Class<? extends JPanel>, DesktopItem> di: this.keyItems.entrySet()) {
			if(di.getValue().getContent() == component) {
				IDesktopPaneFactory factory = this.factories.get(di.getKey());
				factory.destroyDesktopItem(di.getValue());
				keyToRemove = di.getKey();
				break;
			}
		}
		if(null != keyToRemove) {
			this.keyItems.remove(keyToRemove);
		}
	}

	@Override
	public void tabDragged(TabEvent event) {

	}
}
