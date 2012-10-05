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
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.IconManagerService;
import org.invenzzia.helium.gui.actions.ActionManagerService;
import org.invenzzia.helium.gui.actions.SimpleActionManager;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.model.InformationModel;
import org.invenzzia.helium.gui.mvc.ControllerService;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.helium.gui.mvc.IView;
import org.invenzzia.helium.gui.mvc.ModelService;
import org.invenzzia.helium.gui.mvc.ViewService;
import org.invenzzia.helium.gui.ui.dock.DockModel;
import org.invenzzia.helium.gui.ui.dock.Dockable;
import org.invenzzia.helium.gui.ui.dock.KnownPositions;
import org.invenzzia.helium.gui.ui.menu.IMenuElementStorage;
import org.invenzzia.helium.gui.ui.menu.MenuController;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.element.Menu;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.helium.gui.ui.menu.element.Separator;
import org.invenzzia.helium.gui.ui.workspace.WorkspaceDockModel;
import org.invenzzia.opentrans.client.ProjectMenuActions;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
import org.invenzzia.opentrans.client.editor.opmodes.DrawingMode;
import org.invenzzia.opentrans.client.editor.opmodes.selection.SelectionMenuActions;
import org.invenzzia.opentrans.client.editor.opmodes.selection.SelectionMode;
import org.invenzzia.opentrans.client.projectmodel.WorldDescriptor;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerController;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerView;
import org.invenzzia.opentrans.client.ui.minimap.MinimapController;
import org.invenzzia.opentrans.client.ui.minimap.MinimapView;
import org.invenzzia.opentrans.client.ui.netview.EditorView;
import org.invenzzia.opentrans.client.ui.netview.NeteditController;
import org.invenzzia.opentrans.client.ui.netview.NetviewActionInterceptor;
import org.invenzzia.opentrans.client.ui.netview.NetviewCommandTranslator;
import org.invenzzia.opentrans.client.ui.worldresize.WorldResizeController;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.invenzzia.opentrans.visitons.factory.SceneFactory;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.stream.GridStream;
import org.invenzzia.opentrans.visitons.render.stream.SegmentBitmapStream;
import org.invenzzia.opentrans.visitons.render.stream.TrackStream;
import org.invenzzia.opentrans.visitons.world.World;
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
	
	/**
	 * For registrations within the docking system.
	 */
	private KnownPositions knownPositions;
	/**
	 * Access global models.
	 */
	private ModelService modelService;
	/**
	 * Service, where we shall register extra views.
	 */
	private ViewService viewService;
	/**
	 * Service, where we shall register extra controllers.
	 */
	private ControllerService controllerService;
	/**
	 * Registration of the project-specific threads: the renderer and the model loop.
	 */
	private SchedulerManager schedulerManager;
	/**
	 * For registering actions.
	 */
	private ActionManagerService actionManager;
	/**
	 * The renderer instance.
	 */
	private Renderer renderer;

	public ProjectContext(Application application) {
		super(application);
		this.initProjectContainer();
	}
	
	@Inject
	public void setKnownPositions(KnownPositions kp) {
		this.knownPositions = kp;
	}

	@Inject
	public void setSchedulerManager(SchedulerManager schedulerManager) {
		this.schedulerManager = schedulerManager;
	}
	
	@Inject
	public void setControllerService(ControllerService service) {
		this.controllerService = service;
	}
	
	@Inject
	public void setModelService(ModelService service) {
		this.modelService = service;
	}

	@Inject
	public void setViewService(ViewService service) {
		this.viewService = service;
	}

	@Inject
	public void setActionManager(ActionManagerService actionManager) {
		this.actionManager = actionManager;
	}
	
	/**
	 * This method must be called before starting this context. It passes the processed project
	 * to the context. Once set, the project cannot be changed for this context.
	 * 
	 * @param project 
	 */
	public void setVisitonsProject(VisitonsProject project) {
		if(null == this.project) {
			this.project = Preconditions.checkNotNull(project, "Cannot set an empty project.");
			this.registerManagedCachedClasses(new Object[][] {
				{ VisitonsProject.class, this.project },
				{ World.class, this.project.getWorld() }
			});
		}
	}

	/**
	 * Initializes the DI scope for this context.
	 */
	private void initProjectContainer() {
		this.registerManagedClasses(new Object[][] {
			{ NeteditController.class },
			{ NetviewCommandTranslator.class },
			{ CameraModel.class },
			{ ExplorerController.class },
			{ MinimapController.class },
			{ ProjectMenuActions.class },
			{ WorldResizeController.class },
			{ SimpleActionManager.class },
			{ NetviewActionInterceptor.class },
			{ SelectionMode.class },
			{ DrawingMode.class },
			{ GridStream.class },
			{ SegmentBitmapStream.class },
			{ TrackStream.class },
			{ Renderer.class },
			
			// Classes for the operation modes in the editor
			{ SelectionMenuActions.class },
		});
		this.registerManagedCachedClasses(new Object[][] { 
			{ WorldDescriptor.class },
			{ SceneFactory.class },
			{ SceneManager.class }
		});
	}
	
	public VisitonsProject getProject() {
		return this.project;
	}
	
	@Override
	protected boolean startup() {
		if(null == this.project) {
			this.logger.error("Cannot start the project: no project defined.");
			return false;
		}
		
		this.logger.info("Project '{}' is being opened.", this.project.getName());
		this.container.start();
		
		this.logger.debug("Initializing project models.");
		this.initModels();
		
		this.logger.debug("Initializing project renderer.");
		this.initRenderer();
		
		this.logger.debug("Initializing project GUI.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ProjectContext.this.initGUI();
			}
		});
		this.logger.debug("Initializing project actions.");
		this.initActions();
		
		this.logger.debug("Initializing project notifications.");
		this.initNotifications();

		
		this.logger.info("Project '{}' has been opened.", this.project.getName());
		return true;
	}

	@Override
	protected boolean shutdown() {
		this.logger.info("Project '{}' is being closed.", this.project.getName());
		
		this.logger.debug("Stopping project models.");
		this.shutdownModels();
		
		this.logger.debug("Stopping the rendering.");
		this.shutdownRendering();
		
		this.logger.debug("Unregistering project hooks.");
		this.shutdownProjectHooks();
		
		ActionManagerService actionManager = this.container.getComponent(ActionManagerService.class);
		actionManager.unregisterActions(this.container.getComponent(ProjectMenuActions.class));

		SchedulerManager manager = this.container.getComponent(SchedulerManager.class);
		manager.stop("renderer");
			
		this.application.get(EventBus.class).post(new StatusChangeEvent("Project '"+this.project.getName()+"' closed."));
		this.logger.info("Project '{}' has been closed.", this.project.getName());
		return true;
	}
	
	/**
	 * Initialize extra models necessary for the communications between threads, etc. This is the first
	 * thing we must do before configuring anything else.
	 */
	private void initModels() {
		CameraModel theCamera = this.get(CameraModel.class);
		this.modelService.addModel(theCamera);
		
		// Put everything we need to the scene manager, so that the renderer could work.	
		SceneFactory factory = this.get(SceneFactory.class);
		factory.setVisitonsProject(this.project);
		factory.setCameraModel(theCamera);
		factory.onCameraUpdate();
	}
	
	private void initRenderer() {
		if(!this.schedulerManager.hasScheduler("renderer")) {
			RenderScheduler scheduler = new RenderScheduler("renderer");
			scheduler.setRenderer(this.renderer = this.constructRenderer());
			this.schedulerManager.addScheduler(scheduler);
		}
		this.schedulerManager.start("renderer");
	}
	
	/**
	 * Performs the initialization of GUI engine: creates the basic views and places them in the
	 * docking system.
	 */
	private void initGUI() {
		// First, controllers.
		MinimapController minimapController = this.get(MinimapController.class);
		NeteditController neteditController = this.get(NeteditController.class);
		ExplorerController explorerController = this.get(ExplorerController.class);
		
		neteditController.setInformationModel(this.modelService.get(InformationModel.class));
		
		this.initNetworkController(neteditController);
		this.controllerService.addAll(new IController[] { neteditController, minimapController, explorerController });
		
		// Next, views
		
		EditorView edView = new EditorView(this.get(IconManagerService.class));
		edView.getCameraDrawer().setRenderer(this.renderer);
		edView.setCameraModel(this.modelService.get(CameraModel.class));
		edView.setController(neteditController);
		edView.updateScrollbars();

		ExplorerView exView = new ExplorerView();
		exView.setController(explorerController);
		MinimapView minimapView = new MinimapView();
		minimapView.setController(minimapController);
		
		this.viewService.addAll(new IView[] { edView, exView, minimapView });
		
		// Finally, the docking system registration.
		DockModel dockModel = this.modelService.get(WorkspaceDockModel.class);
		Dockable dockable = new Dockable(edView, "Network editor", "visitons-netedit");
		dockModel.resolvePath(this.knownPositions.selectPath(dockable, "editor"), dockable);
		dockable = new Dockable(minimapView, "Minimap");
		dockModel.resolvePath(this.knownPositions.selectPath(dockable, "minimap"), dockable);
		dockable = new Dockable(exView, "Project");
		dockModel.resolvePath(this.knownPositions.selectPath(dockable, "explorer"), dockable);
		
		// Finally, send the notification that we know the viewport size.
		SceneFactory factory = this.get(SceneFactory.class);
		factory.onCameraUpdate();
	}

	/**
	 * Initializes the UI actions for the project.
	 */
	private void initActions() {
		this.actionManager.registerActions(this.get(ProjectMenuActions.class));
		this.initProjectMenu(this.modelService.get(MenuModel.class));
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
	
	/**
	 * Last initialization step - puts some information about the project.
	 */
	private void initNotifications() {
		InformationModel infoModel = this.modelService.get(InformationModel.class);
		infoModel.setStatus("Project '"+this.project.getName()+"' loaded.");
		infoModel.setTitle(this.project.getName());
	}

	/**
	 * Initializes the network view.
	 */
	private void initNetworkController(NeteditController controller) {		
		controller.addOperation(this.get(SelectionMode.class));
		controller.addOperation(this.get(DrawingMode.class));
	}

	/**
	 * Temporary method for constructing the renderer. In the future, when the simulation window will be present,
	 * this must be moved to some kind of factory.
	 * 
	 * @return Constructed and properly initialized renderer.
	 */
	private Renderer constructRenderer() {
		Renderer r = this.get(Renderer.class);
		r.addRenderingStream(this.get(SegmentBitmapStream.class));
		r.addRenderingStream(this.get(GridStream.class));
		
		TrackStream ts = this.get(TrackStream.class);
		ts.setRecognizedTrackSnapshotKey("edit");
		
		r.addRenderingStream(ts);
		
		return r;
	}
	
	private void shutdownModels() {
		
	}
	
	private void shutdownRendering() {
		this.schedulerManager.stop("renderer");
	}
	
	private void shutdownProjectHooks() {
		InformationModel infoModel = this.modelService.get(InformationModel.class);
		infoModel.setStatus("No project loaded.");
		infoModel.setTitle("No project");
	}
}
