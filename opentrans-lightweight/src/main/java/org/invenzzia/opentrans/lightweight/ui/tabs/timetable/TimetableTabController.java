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
package org.invenzzia.opentrans.lightweight.ui.tabs.timetable;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.IProjectHolder;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.model.selectors.RouteSelectionModel;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.routes.RouteDialogController;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Controller for the whole timetable tab: it handles all the button actions
 * and is responsible for handling the list of courses for a currently
 * selected route.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TimetableTabController {
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private Provider<RouteDialogController> lineDialogControllerProvider;
	@Inject
	private IProjectHolder projectHolder;
	@Inject
	private History<ICommand> history;
	@Inject
	private TimetableTabModel model;
	@Inject
	private Provider<RouteSelectionModel> routeSelectionModelProvider;
	/**
	 * Managed view
	 */
	private TimetableTab view;
	/**
	 * The currently used model for selecting the routes.
	 */
	private RouteSelectionModel routeSelectionModel;
	
	public void setView(TimetableTab view) {
		this.view = Preconditions.checkNotNull(view);
		
		this.actionScanner.discoverActions(TimetableTabController.class, this);
		this.actionScanner.bindComponents(TimetableTab.class, this.view);
		
		this.routeSelectionModel = this.routeSelectionModelProvider.get();
		this.model.addSelectionModel(routeSelectionModel);
		this.model.addBatchModelListener(this.view);
		this.view.setRouteSelectionModel(this.routeSelectionModel);
		
		this.model.updateData();
	}
	
	public TimetableTab getView() {
		return this.view;
	}
}
