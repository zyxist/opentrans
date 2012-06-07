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
import javax.swing.SwingUtilities;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.exception.CardNotFoundException;
import org.invenzzia.helium.gui.ui.card.Card;
import org.invenzzia.helium.gui.ui.card.CardView;
import org.invenzzia.helium.gui.ui.menu.IMenuElementStorage;
import org.invenzzia.helium.gui.ui.menu.MenuController;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.element.Menu;
import org.invenzzia.helium.gui.ui.menu.element.Position;
import org.invenzzia.helium.gui.ui.menu.element.Separator;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
import org.invenzzia.opentrans.client.projectmodel.WorldDescriptor;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerController;
import org.invenzzia.opentrans.client.ui.explorer.ExplorerView;
import org.invenzzia.opentrans.client.ui.netedit.CameraView;
import org.invenzzia.opentrans.client.ui.netedit.EditorView;
import org.invenzzia.opentrans.client.ui.netedit.NeteditController;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.Renderer;
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
	private Card networkEditorCard;
	private Card explorerCard;

	public ProjectContext(Application application, VisitonsProject project) {
		super(application);
		this.project = Preconditions.checkNotNull(project, "Cannot create a project context without a project.");
		
		this.container.addComponent(EditorView.class)
			.addComponent(CameraView.class)
			.addComponent(NeteditController.class)
			.addComponent(Renderer.class)
			.addComponent(CameraModel.class)
			.addComponent(VisitonsProject.class, this.project)
			.addComponent(ExplorerView.class)
			.addComponent(ExplorerController.class)
			.addComponent(World.class, this.project.getWorld())
			
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
			scheduler.setRenderer(this.container.getComponent(Renderer.class));
			manager.addScheduler(scheduler);
		}
		manager.start("renderer");
		
		this.logger.debug("Initializing project views.");
		EditorView edView = this.container.getComponent(EditorView.class);
		CardView cardView = this.container.getComponent(CardView.class);
		this.networkEditorCard = cardView.createCard(edView);
		
		ExplorerView exView = this.container.getComponent(ExplorerView.class);
		this.explorerCard = cardView.createCard(exView);
		
		this.initProjectMenu(this.container.getComponent(MenuController.class).getModel());
		
		this.application.getEventBus().post(new StatusChangeEvent("Project '"+this.project.getName()+"' loaded."));
		
		this.logger.info("Project '{}' has been opened.", this.project.getName());
		return true;
	}

	@Override
	protected boolean shutdown() {
		this.logger.info("Project '{}' is being closed.", this.project.getName());
		final CardView cardView = this.container.getComponent(CardView.class);
			
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if(cardView.hasCard(ProjectContext.this.networkEditorCard)) {
						cardView.removeCard(ProjectContext.this.networkEditorCard);
					}
					if(cardView.hasCard(ProjectContext.this.explorerCard)) {
						cardView.removeCard(ProjectContext.this.explorerCard);
					}
				} catch(CardNotFoundException exception) {
					ProjectContext.this.logger.error("Cannot stop the project context: 'network editor' card not found.");
				}
					
			}	
		});

		SchedulerManager manager = this.container.getComponent(SchedulerManager.class);
		manager.stop("renderer");
			
		this.application.getEventBus().post(new StatusChangeEvent("Project '"+this.project.getName()+"' closed."));
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
			
			model.addElementBefore(projectMenu, "help");
			model.addElementBefore(objectsMenu, "help");
		} finally {
			model.stopBatchUpdate();
		}
	}
}
