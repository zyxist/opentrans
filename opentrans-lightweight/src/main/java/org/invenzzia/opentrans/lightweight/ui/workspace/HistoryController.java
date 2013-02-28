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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.workspace.HistoryPanel.IHistoryListener;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryController implements IHistoryListener {
	/**
	 * The history manager.
	 */
	@Inject
	private History<ICommand> history;
	/**
	 * Needed for model registration etc.
	 */
	@Inject
	private EventBus eventBus;
	/**
	 * For handling events.
	 */
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * Operated model.
	 */
	private HistoryModel model;
	/**
	 * Managed panel.
	 */
	private HistoryPanel view;

	/**
	 * Sets the view to operate one.
	 * 
	 * @param view 
	 */
	public void setView(HistoryPanel view) {
		if(null != this.view) {
			this.eventBus.unregister(this.model);
			this.eventBus.unregister(this);
			this.view.removeHistoryListeners();
			this.view.setHistoryModel(null);
			this.model = null;
		}
		this.view = view;
		if(null != this.view) {
			this.model = new HistoryModel(this.history.getHistory());
			this.view.setHistoryModel(this.model);
			this.view.addHistoryListener(this);
			this.setButtonState(this.history.getPastOperationNum());
			this.view.setSelectedCommand(this.history.getPastOperationNum());
			this.eventBus.register(this.model);
			this.eventBus.register(this);
		}
	}
	
	public HistoryPanel getView() {
		return this.view;
	}

	@Subscribe
	public void notifyHistoryChanged(HistoryChangedEvent<ICommand> event) {
		int selectedCommand = event.getHistory().getPastOperationNum();
		this.view.setSelectedCommand(selectedCommand);
		this.setButtonState(selectedCommand);
	}

	@Override
	public void undoClicked(HistoryPanel.HistoryEvent event) {
		try {
			this.history.undo();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot undo", exception);
		}
	}

	@Override
	public void redoClicked(HistoryPanel.HistoryEvent event) {
		try {
			this.history.redo();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot redo", exception);
		}
	}

	@Override
	public void clearHistoryClicked(HistoryPanel.HistoryEvent event) {
		this.history.clear();
	}

	@Override
	public void commandSelected(HistoryPanel.HistoryEvent event) {
		try {
			this.history.jumpTo(event.getCommand());
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot redo", exception);
		}
	}
	
	/**
	 * Manages the 'undo' and 'redo' button states.
	 * 
	 * @param selectedIdx 
	 */
	private void setButtonState(int selectedIdx) {
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
