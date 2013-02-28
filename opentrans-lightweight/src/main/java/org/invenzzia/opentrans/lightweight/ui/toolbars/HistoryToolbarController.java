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

package org.invenzzia.opentrans.lightweight.ui.toolbars;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.ToolbarAction;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * The controller manages the two buttons 'Undo' and 'Redo' visible on the
 * history toolbar.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class HistoryToolbarController extends AbstractToolbarController {
	@Inject
	private History<ICommand> history;
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * The current view.
	 */
	private HistoryToolbar view;

	/**
	 * Sets the toolbar view.
	 * 
	 * @param toolbar 
	 */
	public void setView(HistoryToolbar toolbar) {
		this.view = toolbar;
		this.installListeners(toolbar);
		this.updateButtonStates();
	}
	
	public HistoryToolbar getView() {
		return this.view;
	}
	
	@ToolbarAction("undo")
	public void undoAction() {
		try {
			this.history.undo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot undo", exception);
		}
	}
	
	@ToolbarAction("redo")
	public void redoAction() {
		try {
			this.history.redo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot redo", exception);
		}
	}
	
	@Subscribe
	public void notifyHistoryChanges(HistoryChangedEvent<ICommand> event) {
		this.updateButtonStates();
	}

	/**
	 * Manages the 'undo' and 'redo' button states.
	 * 
	 * @param selectedIdx 
	 */
	private void updateButtonStates() {
		int selectedIdx = this.history.getPastOperationNum();
		if(0 == selectedIdx) {
			this.view.setUndoEnabled(false);
			if(this.history.getPastOperationNum() + this.history.getFutureOperationNum() == 0) {
				this.view.setRedoEnabled(false);
			} else {
				this.view.setRedoEnabled(true);
			}
		} else if(this.history.getPastOperationNum() + this.history.getFutureOperationNum() == selectedIdx) {
			this.view.setUndoEnabled(true);
			this.view.setRedoEnabled(false);
		} else {
			this.view.setUndoEnabled(true);
			this.view.setRedoEnabled(true);
		}
	}
}
