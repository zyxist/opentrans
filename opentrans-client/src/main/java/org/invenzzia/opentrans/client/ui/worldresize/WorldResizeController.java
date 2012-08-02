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
package org.invenzzia.opentrans.client.ui.worldresize;

import com.google.common.base.Preconditions;
import javax.swing.JOptionPane;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The controller for the world resize responds to the extend/shrink
 * buttons and controls the state indicating, how we should change
 * them.
 * 
 * If a resizing event arrives, it delegates it to the model active
 * object which executes it in the appropriate thread.
 * 
 * @author Tomasz Jędrzejewski
 */
public class WorldResizeController implements IController<WorldResizeView> {
	private final Logger logger = LoggerFactory.getLogger(WorldResizeController.class);
	
	private WorldResizeView view;
	private VisitonsProject model;
	private int currentMode;
	
	public WorldResizeController(VisitonsProject model) {
		this.model = Preconditions.checkNotNull(model);
	}
	
	@Override
	public void attachView(WorldResizeView object) {
		this.view = object;
		this.view.setModel(this.model.getWorld());
		this.view.refreshData();
		this.currentMode = this.view.getMode();
		this.logger.debug("View attached.");
	}

	@Override
	public void detachView(WorldResizeView object) {
		this.view.setModel(null);
		this.view = null;
		this.logger.debug("View detached.");
	}
	
	public void switchStateToExtend() {
		if(null != this.view) {
			this.logger.info("Switching the mode to 'extend'.");
			
			this.view.setMode(WorldResizeView.MODE_EXTEND);
			this.currentMode = this.view.getMode();
		}
	}
	
	public void switchStateToShrink() {
		if(null != this.view) {
			this.logger.info("Switching the mode to 'shrink'.");
			
			this.view.setMode(WorldResizeView.MODE_SHRINK);
			this.currentMode = this.view.getMode();
		}
	}
	
	public void leftResize() {
		try {
			if(WorldResizeView.MODE_EXTEND == this.currentMode) {
				this.model.getWorld().extendHorizontally(World.HorizontalDir.LEFT);
			} else {
				this.model.getWorld().shrinkHorizontally(World.HorizontalDir.LEFT);
			}
			this.view.refreshData();
		} catch(WorldException exception) {
			JOptionPane.showMessageDialog(this.view, exception.getMessage(), "Cannot resize", JOptionPane.OK_OPTION);
		}
	}
	
	public void rightResize() {
		try {
			if(WorldResizeView.MODE_EXTEND == this.currentMode) {
				this.model.getWorld().extendHorizontally(World.HorizontalDir.RIGHT);
			} else {
				this.model.getWorld().shrinkHorizontally(World.HorizontalDir.RIGHT);
			}
			this.view.refreshData();
		} catch(WorldException exception) {
			JOptionPane.showMessageDialog(this.view, exception.getMessage(), "Cannot resize", JOptionPane.OK_OPTION);
		}
	}
	
	public void topResize() {
		try {
			if(WorldResizeView.MODE_EXTEND == this.currentMode) {
				this.model.getWorld().extendVertically(World.VerticalDir.UP);
			} else {
				this.model.getWorld().shrinkVertically(World.VerticalDir.UP);
			}
			this.view.refreshData();
		} catch(WorldException exception) {
			JOptionPane.showMessageDialog(this.view, exception.getMessage(), "Cannot resize", JOptionPane.OK_OPTION);
		}
	}
	
	public void bottomResize() {
		try {
			if(WorldResizeView.MODE_EXTEND == this.currentMode) {
				this.model.getWorld().extendVertically(World.VerticalDir.DOWN);
			} else {
				this.model.getWorld().shrinkVertically(World.VerticalDir.DOWN);
			}
			this.view.refreshData();
		} catch(WorldException exception) {
			JOptionPane.showMessageDialog(this.view, exception.getMessage(), "Cannot resize", JOptionPane.OK_OPTION);
		}
	}
}
