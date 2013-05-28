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

package org.invenzzia.opentrans.lightweight.ui.navigator;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorPanel.INavigatorListener;
import org.invenzzia.opentrans.lightweight.ui.navigator.NavigatorPanel.NavigatorEvent;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * The controller for the navigator panel responds for all the high-level events. It
 * also provides a simple API that allows controlling, what is displayed in the panel,
 * and which object is currently selected.
 * 
 * To change the displayed content, just install a new model in the controller. You
 * can do it at any time.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class NavigatorController implements INavigatorListener {
	/**
	 * Current view used by the panel.
	 */
	private NavigatorPanel view;
	/**
	 * Current model used by the panel.
	 */
	private NavigatorModel model;
	/**
	 * Currently selected object.
	 */
	private Object selectedObject;
	@Inject
	private IProjectHolder projectHolder;

	/**
	 * Binds the controller to the new view, and unbinds from the previous
	 * one.
	 * 
	 * @param view 
	 */
	public void setView(NavigatorPanel view) {
		if(null != this.view) {
			this.view.removeNavigatorListener(this);
			this.view.setModel(null);
		}
		this.view = view;
		if(null != this.view) {
			this.view.addNavigatorListener(this);
			this.view.setModel(this.model);
		}
	}

	/**
	 * Returns the current view.
	 * 
	 * @return 
	 */
	public NavigatorPanel getView() {
		return this.view;
	}

	/**
	 * Installs a new model, changing the displayed content.
	 * 
	 * @param model 
	 */
	public void setModel(NavigatorModel model) {
		this.model = model;
		if(null != this.model) {
			this.model.loadItems(this.projectHolder.getCurrentProject());
		}
		this.selectedObject = null;
		if(null != this.view) {
			this.view.setModel(this.model);
		}
	}
	
	/**
	 * Returns the current model.
	 * 
	 * @return 
	 */
	public NavigatorModel getModel() {
		return this.model;
	}
	
	/**
	 * Returns the currently selected object. The returned value may be <strong>NULL</strong>.
	 * 
	 * @return Currently selected object.
	 */
	public Object getSelectedObject() {
		return this.selectedObject;
	}

	/**
	 * When the history is changed, we must probably refresh the data in the tab
	 * in order to be up-to-date.
	 * 
	 * @param event History change event.
	 */
	@Subscribe
	public void notifyHistoryChanged(HistoryChangedEvent<ICommand> event) {
		if(null != this.model) {
			this.model.loadItems(this.projectHolder.getCurrentProject());
		}
	}

	@Override
	public void navigatorObjectSelected(NavigatorEvent event) {
		this.selectedObject = event.getSelectedObject();
	}
}
