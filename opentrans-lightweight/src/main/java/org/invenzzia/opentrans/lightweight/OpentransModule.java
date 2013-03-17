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

package org.invenzzia.opentrans.lightweight;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import javax.swing.*;
import org.invenzzia.helium.history.History;
import org.invenzzia.helium.history.IHistoryStrategy;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.app.VisitonsHistoryStrategy;
import org.invenzzia.opentrans.lightweight.controllers.*;
import org.invenzzia.opentrans.lightweight.interceptor.ModelThreadInterceptor;
import org.invenzzia.opentrans.lightweight.interceptor.SwingThreadInterceptor;
import org.invenzzia.opentrans.lightweight.lf.icons.IconService;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.providers.*;
import org.invenzzia.opentrans.lightweight.tasks.*;
import org.invenzzia.opentrans.lightweight.ui.DefaultDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.MainWindowController;
import org.invenzzia.opentrans.lightweight.ui.component.JReportingSlider;
import org.invenzzia.opentrans.lightweight.ui.providers.WorkspacePanelProvider;
import org.invenzzia.opentrans.lightweight.ui.tabs.ProjectTabFactory;
import org.invenzzia.opentrans.lightweight.ui.tabs.WorldTabFactory;
import org.invenzzia.opentrans.lightweight.ui.tabs.infrastructure.InfrastructureTabFactory;
import org.invenzzia.opentrans.lightweight.ui.tabs.vehicles.VehicleEditorController;
import org.invenzzia.opentrans.lightweight.ui.tabs.vehicles.VehicleTabController;
import org.invenzzia.opentrans.lightweight.ui.tabs.vehicles.VehicleTabFactory;
import org.invenzzia.opentrans.lightweight.ui.toolbars.ToolbarManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.DesktopManager;
import org.invenzzia.opentrans.lightweight.ui.workspace.IDesktopPaneFactory;
import org.invenzzia.opentrans.lightweight.ui.workspace.WorkspaceController;
import org.invenzzia.opentrans.lightweight.ui.workspace.WorkspacePanel;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.network.World;

/**
 * The Google Guice module that manages the bindings related to GUI.
 * In the future, we may think about splitting it into some smaller
 * modules and produce a nice API for common extension points.
 * 
 * @author Tomasz Jędrzejewski
 */
public class OpentransModule extends AbstractModule {

	@Override
	protected void configure() {
		Application application = new Application();

		this.bind(Application.class).toInstance(application);
		this.bind(IProjectHolder.class).toInstance(application);
		this.bind(EventBus.class).in(Singleton.class);
		this.bind(IconService.class).in(Singleton.class);
		this.bind(ToolbarManager.class);
		this.bind(DesktopManager.class).toProvider(DesktopManagerProvider.class).in(Singleton.class);
		
		this.bind(IDialogBuilder.class).to(DefaultDialogBuilder.class);
		
		this.bind(new TypeLiteral<History<ICommand>>(){}).toProvider(HistoryProvider.class).in(Singleton.class);
		this.bind(new TypeLiteral<IHistoryStrategy<ICommand>>(){}).to(VisitonsHistoryStrategy.class).in(Singleton.class);
		
		// Interceptors that ease working with the concurrency model.
		ModelThreadInterceptor mti = new ModelThreadInterceptor();
		this.requestInjection(mti);
		this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(InModelThread.class), mti);
		
		SwingThreadInterceptor sti = new SwingThreadInterceptor();
		this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(InSwingThread.class), sti);

		// Bind controller utils
		this.bind(IActionScanner.class).to(DefaultActionScanner.class);
		MapBinder<Class, IActionScannerComponentHandler> componentHandlerBinder =
			MapBinder.newMapBinder(this.binder(), Class.class, IActionScannerComponentHandler.class);
		componentHandlerBinder.addBinding(JButton.class).to(ActionButtonHandler.class).in(Singleton.class);
		componentHandlerBinder.addBinding(JMenuItem.class).to(ActionButtonHandler.class).in(Singleton.class);
		
		// Bind controller utils
		this.bind(IFormScanner.class).to(DefaultFormScanner.class);
		MapBinder<Class, IFormScannerComponentHandler> fieldHandlerBinder =
			MapBinder.newMapBinder(this.binder(), Class.class, IFormScannerComponentHandler.class);
		fieldHandlerBinder.addBinding(JTextField.class).to(TextFieldHandler.class).in(Singleton.class);
		fieldHandlerBinder.addBinding(JCheckBox.class).to(CheckboxHandler.class).in(Singleton.class);
		fieldHandlerBinder.addBinding(JComboBox.class).to(ComboBoxHandler.class).in(Singleton.class);
		fieldHandlerBinder.addBinding(JReportingSlider.class).to(ReportingSliderHandler.class).in(Singleton.class);

		// Models
		this.bind(BrandingModel.class);

		// Controllers
		this.bind(MainWindowController.class);
		this.bind(WorkspaceController.class);
		this.bind(VehicleTabController.class);
		this.bind(VehicleEditorController.class);

		// Toolbars
		this.bind(WorkspacePanel.class).toProvider(WorkspacePanelProvider.class);

		// Bind the startup tasks.
		this.bind(CreateGuiTask.class);
		this.bind(IconServiceTask.class);
		this.bind(ProjectTask.class);
		this.bind(LoadConfigurationTask.class);

		// Bind desktop pane factories
		Multibinder<IDesktopPaneFactory> factoryBinder = Multibinder.newSetBinder(this.binder(), IDesktopPaneFactory.class);
		factoryBinder.addBinding().to(ProjectTabFactory.class);
		factoryBinder.addBinding().to(WorldTabFactory.class);
		factoryBinder.addBinding().to(InfrastructureTabFactory.class);
		factoryBinder.addBinding().to(VehicleTabFactory.class);

		// Bind visitons items
		this.bind(World.class).toProvider(WorldProvider.class);
	}

}
