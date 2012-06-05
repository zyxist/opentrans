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
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.context.AbstractContext;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.exception.CardNotFoundException;
import org.invenzzia.helium.gui.ui.card.Card;
import org.invenzzia.helium.gui.ui.card.CardView;
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
		RenderScheduler scheduler = new RenderScheduler("renderer");
		scheduler.setRenderer(this.container.getComponent(Renderer.class));
		manager.addScheduler(scheduler);
		manager.start("renderer");
		
		this.logger.debug("Initializing project views.");
		EditorView edView = this.container.getComponent(EditorView.class);
		CardView cardView = this.container.getComponent(CardView.class);
		this.networkEditorCard = cardView.createCard(edView);
		
		ExplorerView exView = this.container.getComponent(ExplorerView.class);
		cardView.createCard(exView);
		
		this.application.getEventBus().post(new StatusChangeEvent("Project '"+this.project.getName()+"' loaded."));
		
		this.logger.info("Project '{}' has been opened.", this.project.getName());
		return true;
	}

	@Override
	protected boolean shutdown() {
		try {
			this.logger.info("Project '{}' is being closed.", this.project.getName());
			CardView cardView = this.container.getComponent(CardView.class);
			cardView.removeCard(this.networkEditorCard);
			cardView.removeCard(this.explorerCard);

			SchedulerManager manager = this.container.getComponent(SchedulerManager.class);
			manager.stop("renderer");
			
			this.application.getEventBus().post(new StatusChangeEvent("Project '"+this.project.getName()+"' closed."));

			this.logger.info("Project '{}' has been closed.", this.project.getName());
			return true;
		} catch(CardNotFoundException exception) {
			this.logger.error("Cannot stop the project context: 'network editor' card not found.");
			return false;
		}
	}

}
