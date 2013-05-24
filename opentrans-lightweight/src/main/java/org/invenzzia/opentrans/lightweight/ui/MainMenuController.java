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

package org.invenzzia.opentrans.lightweight.ui;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.ui.dialogs.about.AboutDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.about.AboutDialogController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.lines.LineDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.lines.LineDialogController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.means.MeanOfTransportDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialog;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialogController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeController;
import org.invenzzia.opentrans.lightweight.ui.dialogs.vehicletype.VehicleTypeDialog;
import org.invenzzia.opentrans.lightweight.ui.toolbars.AbstractToolbar;
import org.invenzzia.opentrans.lightweight.ui.toolbars.ToolbarManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for all actions found in the main application menu.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MainMenuController {
	private final Logger logger = LoggerFactory.getLogger(MainMenuController.class);
	/**
	 * For managing the state of 'undo' and 'redo' buttons.
	 */
	@Inject
	private History<ICommand> history;
	/**
	 * Used for binding actions to menu items.
	 */
	@Inject
	private IActionScanner actionScanner;
	/**
	 * We need to open dialogs somehow.
	 */
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private EventBus eventBus;
	/**
	 * We need access to it to call {@link MainWindowController#handleClosing()} method.
	 */
	@Inject
	private MainWindowController mainWindowController;
	@Inject
	private DesktopManager desktopManager;
	@Inject
	private ToolbarManager toolbarManager;
	@Inject
	private Provider<ResizeDialogController> resizeDialogControllerProvider;
	@Inject
	private Provider<MeanOfTransportController> meanOfTransportControllerProvider;
	@Inject
	private Provider<VehicleTypeController> vehicleTypeControllerProvider;
	@Inject
	private Provider<LineDialogController> lineControllerProvider;
	@Inject
	private Provider<AboutDialogController> aboutDialogControllerProvider;
	/**
	 * The view scanned for menu items.
	 */
	private MainWindow view;
	
	/**
	 * Assigns the view to the controller and binds the actions.
	 * 
	 * @param mainWindow The view.
	 */
	public void setView(MainWindow mainWindow) {
		if(null != this.view) {
			this.actionScanner.clear(MainWindow.class, this.view);
		}
		this.view = mainWindow;
		if(null != this.view) {
			this.actionScanner.discoverActions(MainMenuController.class, this);
			this.actionScanner.bindComponents(MainWindow.class, this.view);
			this.updateButtonStates();
			this.buildTabSelectionItems();
			this.buildToolbarSelectionItems();
		}
	}
	
	/**
	 * The part of the 'Window' menu must be constructed dynamically: take all
	 * the tab factories from the desktop manager and present them as menu items,
	 * so that we could access them from menu.
	 */
	protected void buildTabSelectionItems() {
		this.desktopManager.forAllFactories(new Predicate<IDesktopPaneFactory>() {
			@Override
			public boolean apply(IDesktopPaneFactory factory) {
				JMenuItem item = new JMenuItem(factory.getDesktopItemName());
				item.addActionListener(new TabActionListener(factory.getContentType()));
				view.addTabSelectionItem(item);
				return true;
			}
		});
	}
	
	/**
	 * The part of the 'Window' menu must be constructed dynamically: take all
	 * the toolbars registered in the toolbar manager and present them as menu
	 * items, so that we could access them from menu.
	 */
	protected void buildToolbarSelectionItems() {
		this.toolbarManager.forAllToolbars(new Predicate<AbstractToolbar>() {

			@Override
			public boolean apply(AbstractToolbar toolbar) {
				
				boolean isActive = Preferences.userRoot().getBoolean(toolbar.getToolbarName(), true);
				toolbar.setActive(isActive);
				JCheckBoxMenuItem item = new JCheckBoxMenuItem(toolbar.getToolbarName());
				item.setState(isActive);
				item.addItemListener(new ToolbarItemListener(toolbar));
				
				view.addToolbarSelectionItem(item);
				
				return true;
			}
		});
	}

	@Action("quit")
	public void quitAction() {
		this.mainWindowController.handleClosing();
	}
	
	@Action("undo")
	public void undoAction() {
		try {
			this.history.undo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot undo", exception);
		}
	}
	
	@Action("redo")
	public void redoAction() {
		try {
			this.history.redo();
			this.updateButtonStates();
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot redo", exception);
		}
	}
	
	/**
	 * Shows the 'resize world' dialog.
	 */
	@Action("resizeWorld")
	public void resizeWorldAction() {
		ResizeDialog theDialog = this.dialogBuilder.createModalDialog(ResizeDialog.class);
		ResizeDialogController controller = this.resizeDialogControllerProvider.get();
		controller.setView(theDialog);
		theDialog.setVisible(true);
	}
	
	/**
	 * Shows the dialog for managing means of transport.
	 */
	@Action("meansOfTransport")
	public void meansOfTransportAction() {
		MeanOfTransportDialog dialog = this.dialogBuilder.createModalDialog(MeanOfTransportDialog.class);
		MeanOfTransportController controller = this.meanOfTransportControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}
	
	@Action("vehicleTypes")
	public void vehicleTypesAction() {
		VehicleTypeDialog dialog = this.dialogBuilder.createModalDialog(VehicleTypeDialog.class);
		VehicleTypeController controller = this.vehicleTypeControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
	}
	
	@Action("lines")
	public void linesAction() {
		LineDialog theDialog = this.dialogBuilder.createModalDialog(LineDialog.class);
		LineDialogController controller = this.lineControllerProvider.get();
		controller.setView(theDialog);
		theDialog.setVisible(true);
	}
	
	@Action("closeAllTabs")
	public void closeAllTabsAction() {
		this.desktopManager.destroyAllItems();
	}
	
	@Action("about")
	public void aboutAction() {
		AboutDialog dialog = this.dialogBuilder.createModalDialog(AboutDialog.class);
		AboutDialogController controller = this.aboutDialogControllerProvider.get();
		controller.setView(dialog);
		dialog.setVisible(true);
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
	
	/**
	 * For the tab selection menu items.
	 */
	class TabActionListener implements ActionListener {
		private final Class<? extends JPanel> key;
		
		public TabActionListener(Class<? extends JPanel> key) {
			this.key = Preconditions.checkNotNull(key);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(logger.isDebugEnabled()) {
				logger.debug("Activating the tab '"+this.key.getSimpleName()+"'");
			}
			desktopManager.setFocus(this.key);
		}
	}
	
	/**
	 * For the toolbar activation items.
	 */
	class ToolbarItemListener implements ItemListener {
		private final AbstractToolbar toolbar;
		
		public ToolbarItemListener(AbstractToolbar toolbar) {
			this.toolbar = Preconditions.checkNotNull(toolbar);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			this.toolbar.setActive(e.getStateChange() == ItemEvent.SELECTED);
			Preferences.userRoot().putBoolean(this.toolbar.getToolbarPreferenceKey(), this.toolbar.isActive());
			toolbarManager.update();
		}
	}
}
