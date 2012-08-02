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
package org.invenzzia.opentrans.client.ui.minimap;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.opentrans.client.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class MinimapController implements IController<MinimapView>, MouseMotionListener {
	private final Logger logger = LoggerFactory.getLogger(MinimapController.class);
	private MinimapView view;
	private EventBus eventBus;
	private VisitonsProject project;
	
	public MinimapController(EventBus bus, VisitonsProject project) {
		this.eventBus = bus;
		this.project = project;
	}
	
	@Override
	public void attachView(MinimapView object) {
		this.view = object;
		this.view.setModel(this.project.getWorld());
		this.eventBus.register(this);
		this.view.addMouseMotionListener(this);
		
		this.logger.debug("Minimap view attached.");
	}

	@Override
	public void detachView(MinimapView object) {
		this.logger.debug("Minimap view detached.");
		
		this.view.removeMouseMotionListener(this);
		this.view = null;
		this.eventBus.unregister(this);
	}
	
	@Subscribe
	public void notifyWorldSizeChanged(WorldSizeChangedEvent event) {
		if(null != this.view) {
			this.view.refreshView();
		}
	}

	@Override
	public void mouseDragged(MouseEvent me) {
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		if(null != this.view) {
			this.view.setCursorLocationForHighlight(me.getX(), me.getY());
			this.view.repaint();
		}
	}
}
