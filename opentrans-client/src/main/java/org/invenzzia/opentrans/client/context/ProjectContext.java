/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.context;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import javax.swing.SwingUtilities;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.actions.ActionManagerService;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.model.InformationModel;
import org.invenzzia.helium.gui.ui.dock.DockModel;
import org.invenzzia.helium.gui.ui.dock.Dockable;
import org.invenzzia.helium.gui.ui.dock.KnownPositions;
import org.invenzzia.helium.gui.ui.menu.IMenuElementStorage;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.MenuView;
import org.invenzzia.helium.gui.ui.menu.element.Menu;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.helium.gui.ui.menu.element.Separator;
import org.invenzzia.helium.gui.ui.workspace.WorkspaceDockModel;
import org.invenzzia.opentrans.client.ProjectMenuActions;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
import org.invenzzia.opentrans.client.editor.opmodes.DrawingMode;
import org.invenzzia.opentrans.client.editor.opmodes.selection.SelectionMode;
import org.invenzzia.opentrans.client.projectmodel.WorldDescriptor;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerController;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerView;
import org.invenzzia.opentrans.client.ui.minimap.MinimapController;
import org.invenzzia.opentrans.client.ui.minimap.MinimapView;
import org.invenzzia.opentrans.client.ui.netview.CameraView;
import org.invenzzia.opentrans.client.ui.netview.EditorView;
import org.invenzzia.opentrans.client.ui.netview.NeteditController;
import org.invenzzia.opentrans.client.ui.netview.NetviewCommandTranslator;
import org.invenzzia.opentrans.client.ui.worldresize.WorldResizeController;
import org.invenzzia.opentrans.client.ui.worldresize.WorldResizeView;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.stream.GridStream;
import org.invenzzia.opentrans.visitons.world.World;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application context for OpenTrans client. Represents a case, where
 * a project is open.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ProjectContext extends AbstractContext {
	private final Logger logger = LoggerFactory.getLogger(ProjectContext.class);
	private VisitonsProject project;

	public ProjectContext(Application application, VisitonsProject project) {
		super(application);
		this.project = Preconditions.checkNotNull(project, "Cannot create a project context without a project.");
		
		this.container.addComponent(EditorView.class)
			.addComponent(CameraView.class)
			.addComponent(NeteditController.class)
			.addComponent(NetviewCommandTranslator.class)
			.addComponent(Renderer.class)
			.addComponent(CameraModel.class)
			.addComponent(VisitonsProject.class, this.project)
			.addComponent(ExplorerView.class)
			.addComponent(ExplorerController.class)
			.addComponent(World.class, this.project.getWorld())
			.addComponent(MinimapView.class)
			.addComponent(MinimapController.class)
			.addComponent(ProjectMenuActions.class)
			.addComponent(WorldResizeView.class)
			.addComponent(WorldResizeController.class)
			
			// Editor
			.addComponent(SelectionMode.class)
			.addComponent(DrawingMode.class)
			
			// Rendering
			.addComponent(GridStream.class)
			
			// Project model
			.addComponent(WorldDescriptor.class);
	}
	
	public VisitonsProject getProject() {
		return this.project;
	}
	
	@Override
	protected boolean startup() {
		this.logger.info("Project '{}' is being opened.", this.project.getName());
		this.container.start();
		
		this.logger.debug("Initializing project renderer.");
		SchedulerManager manager = this.container.getComponent(SchedulerManager.class);
		if(!manager.hasScheduler("renderer")) {
			RenderScheduler scheduler = new RenderScheduler("renderer");
			scheduler.setRenderer(this.constructRenderer());
			manager.addScheduler(scheduler);
		}
		manager.start("renderer");
		
		this.logger.debug("Initializing project views.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MutablePicoContainer container = ProjectContext.this.container;
				
				EditorView edView = container.getComponent(EditorView.class);
				ExplorerView exView = container.getComponent(ExplorerView.class);
				MinimapView minimapView = container.getComponent(MinimapView.class);
				MinimapController minimapController = container.getComponent(MinimapController.class);
				minimapView.setController(minimapController);

				DockModel dockModel = container.getComponent(WorkspaceDockModel.class);
				KnownPositions knownPositions = container.getComponent(KnownPositions.class);
				
				ProjectContext.this.initNetworkView();
				
				Dockable dockable = new Dockable(edView, "Network editor", "visitons-netedit");
				dockModel.resolvePath(knownPositions.selectPath(dockable, "editor"), dockable);
				dockable = new Dockable(minimapView, "Minimap");
				dockModel.resolvePath(knownPositions.selectPath(dockable, "minimap"), dockable);
				dockable = new Dockable(exView, "Project");
				dockModel.resolvePath(knownPositions.selectPath(dockable, "explorer"), dockable);

			}
		});		
		ActionManagerService actionManager = this.container.getComponent(ActionManagerService.class);
		actionManager.registerActions(this.container.getComponent(ProjectMenuActions.class));
		
		this.initProjectMenu(this.container.getComponent(MenuView.class).getModel());
		
		InformationModel infoModel = this.container.getComponent(InformationModel.class);
		infoModel.setStatus("Project '"+this.project.getName()+"' loaded.");
		infoModel.setTitle(this.project.getName());
		
		this.logger.info("Project '{}' has been opened.", this.project.getName());
		return true;
	}

	@Override
	protected boolean shutdown() {
		this.logger.info("Project '{}' is being closed.", this.project.getName());
		
		InformationModel infoModel = this.container.getComponent(InformationModel.class);
		infoModel.setStatus("No project loaded.");
		infoModel.setTitle("No project");
		
		ActionManagerService actionManager = this.container.getComponent(ActionManagerService.class);
		actionManager.unregisterActions(this.container.getComponent(ProjectMenuActions.class));

		SchedulerManager manager = this.container.getComponent(SchedulerManager.class);
		manager.stop("renderer");
			
		this.application.get(EventBus.class).post(new StatusChangeEvent("Project '"+this.project.getName()+"' closed."));
		this.logger.info("Project '{}' has been closed.", this.project.getName());
		return true;
	}

	/**
	 * Initializes the basic client menu structure. Note that we do not have to clean it manually
	 * at the end of the context, because the menu model automatically does that for us.
	 * 
	 * @param model 
	 */
	private void initProjectMenu(MenuModel model) {
		model.startBatchUpdate();
		try {
			IMenuElementStorage fileElement = model.getElement("file", IMenuElementStorage.class);
			fileElement.addElementBefore(new Separator("closeProjectSeparator"), "quitSeparator");
			fileElement.addElementBefore(new Position("closeProject", "Close project", "closeProject"), "quitSeparator");
			
			Menu projectMenu = new Menu("project", "Project");
			projectMenu.appendElement(new Position("settings", "Settings", "showSettings"));
			projectMenu.appendElement(new Separator("firstSeparator"));
			projectMenu.appendElement(new Position("worldSize", "World size", "showWorldSizeDialog"));
			projectMenu.appendElement(new Position("configureSimulation", "Configure simulation", "showSimulationConfigDialog"));
			
			Menu objectsMenu = new Menu("objects", "Objects");
			objectsMenu.appendElement(new Position("meansOfTransport", "Means of transport", "showMeansOfTransportGrid"));
			objectsMenu.appendElement(new Position("vehicleClasses", "Vehicle classes", "showVehicleClassesGrid"));
			objectsMenu.appendElement(new Position("vehicles", "Vehicles", "showVehiclesGrid"));
			objectsMenu.appendElement(new Separator("firstSeparator"));
			objectsMenu.appendElement(new Position("lines", "Lines", "showLinesGrid"));
			objectsMenu.appendElement(new Position("timetables", "Timetables", "showTimetablesGrid"));
			objectsMenu.appendElement(new Separator("secondSeparator"));
			objectsMenu.appendElement(new Position("stops", "Stops", "showStopsGrid"));
			objectsMenu.appendElement(new Position("depots", "Depots", "showDepotsGrid"));
			
			model.addElementBefore(projectMenu, "dockStatus");
			model.addElementBefore(objectsMenu, "dockStatus");
		} finally {
			model.stopBatchUpdate();
		}
	}
	
	public void initNetworkView() {
		EditorView edView = this.container.getComponent(EditorView.class);
		NeteditController controller = this.container.getComponent(NeteditController.class);
		
		controller.addOperation(this.container.getComponent(SelectionMode.class));
		controller.addOperation(this.container.getComponent(DrawingMode.class));
		
		edView.updateOperationButtons();
	}
	
	/**
	 * Temporary method for constructing the renderer. In the future, when the simulation window will be present,
	 * this must be moved to some kind of factory.
	 * 
	 * @return 
	 */
	private Renderer constructRenderer() {
		Renderer r = this.container.getComponent(Renderer.class);
		
		r.addRenderingStream(this.container.getComponent(GridStream.class));
		
		return r;
	}
}
