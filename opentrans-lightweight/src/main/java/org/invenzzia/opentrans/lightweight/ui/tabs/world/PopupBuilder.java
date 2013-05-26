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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

import com.google.inject.Provider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.invenzzia.opentrans.lightweight.annotations.PopupAction;

/**
 * Fluent API for constructing popup menus.
 * 
 * @author Tomasz Jędrzejewski
 */
public class PopupBuilder {
	private JPopupMenu menu;
	private final List<PopupActionListener> actions;
	
	public static PopupBuilder create() {
		return new PopupBuilder();
	}
	
	public PopupBuilder() {
		this.menu = new JPopupMenu();
		this.actions = new LinkedList<>();
	}
	
	public PopupBuilder action(String desc, IPopupAction action) {
		JMenuItem item = new JMenuItem(desc);
		
		PopupActionListener pal = new PopupActionListener(action);
		this.actions.add(pal);
		item.addActionListener(pal);
		menu.add(item);
		
		return this;
	}
	
	public PopupBuilder action(IPopupAction action) {
		PopupAction annotation = action.getClass().getAnnotation(PopupAction.class);
		if(null == annotation) {
			throw new IllegalArgumentException("The popup action class '"+action.getClass().getSimpleName()+"' is not annotated with @PopupAction.");
		}
		JMenuItem item = new JMenuItem(annotation.text());
		
		PopupActionListener pal = new PopupActionListener(action);
		this.actions.add(pal);
		item.addActionListener(pal);
		menu.add(item);
		
		return this;
	}
	
	public PopupBuilder action(String desc, Provider<? extends IPopupAction> provider) {
		return this.action(desc, provider.get());
	}
	
	public PopupBuilder action(Provider<? extends IPopupAction> provider) {
		return this.action(provider.get());
	}
	
	public PopupBuilder sep() {
		menu.addSeparator();
		return this;
	}
	
	/**
	 * This method shall be used by the {@link IEditModeAPI#showPopup()} to pass the world
	 * coordinates to the actions.
	 * 
	 * @param x World coordinates
	 * @param y World coordinates
	 */
	public void setCoordinates(IEditModeAPI api, double x, double y) {
		for(PopupActionListener pal: this.actions) {
			pal.setCoordinates(api, x, y);
		}
	}

	public JPopupMenu getMenu() {
		return this.menu;
	}
}

class PopupActionListener implements ActionListener {
	private final IPopupAction action;
	private double x;
	private double y;
	private IEditModeAPI api;
	
	public PopupActionListener(IPopupAction action) {
		this.action = action;
	}
	
	public void setCoordinates(IEditModeAPI api, double x, double y) {
		this.api = api;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.action.execute(this.api, this.x, this.y);
	}
}
