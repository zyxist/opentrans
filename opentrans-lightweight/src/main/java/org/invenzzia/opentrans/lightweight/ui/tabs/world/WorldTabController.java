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

package org.invenzzia.opentrans.lightweight.ui.tabs.world;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.event.MouseInputAdapter;
import org.invenzzia.opentrans.lightweight.concurrent.ModelThread;
import org.invenzzia.opentrans.lightweight.concurrent.RenderingThread;
import org.invenzzia.opentrans.lightweight.events.CameraUpdatedEvent;
import org.invenzzia.opentrans.lightweight.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField.IZoomListener;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField.ZoomChangeEvent;
import org.invenzzia.opentrans.lightweight.ui.netview.NetworkView;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.WorldTab.IWorldTabListener;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.SceneManager;

/**
 * Handles user input for the world tab.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class WorldTabController implements AdjustmentListener, IZoomListener, IWorldTabListener {
	@Inject
	private CameraModel cameraModel;
	@Inject
	private Renderer renderer;
	@Inject
	private RenderingThread thread;
	@Inject
	private ModelThread modelThread;
	@Inject
	private SceneManager sceneManager;
	@Inject
	private EventBus eventBus;
	@Inject
	private Provider<World> worldProvider;
	/**
	 * Supported edit modes.
	 */
	@Inject
	private List<IEditMode> editModes;
	/**
	 * Currently active edit mode.
	 */
	private IEditMode currentEditMode;
	/**
	 * The managed tab.
	 */
	private WorldTab worldTab;
	/**
	 * Current mouse listener.
	 */
	private CameraMouseMotionListener mouseListener;
	
	public void setWorldTab(WorldTab worldTab) {
		if(null != this.worldTab) {
			if(null != this.currentEditMode) {
				this.currentEditMode.modeDisabled();
			}
			this.worldTab.removeWorldTabListener(this);
			this.worldTab.getNetworkView().removeAdjustmentListener(this);
			this.worldTab.getNetworkView().getCameraView().removeMouseListener(mouseListener);
			this.worldTab.getNetworkView().getCameraView().removeMouseMotionListener(mouseListener);
			this.worldTab.getZoomField().removeZoomListener(this);
			this.thread.setCameraView(null);
			this.eventBus.unregister(this);
		}
		this.worldTab = worldTab;
		if(null != this.worldTab) {
			if(null == this.mouseListener) {
				this.mouseListener = new CameraMouseMotionListener();
			}
			
			this.thread.setCameraView(this.worldTab.getNetworkView().getCameraView());
			this.worldTab.addWorldTabListener(this);
			this.worldTab.getNetworkView().addAdjustmentListener(this);
			this.worldTab.getNetworkView().getCameraView().setRenderer(this.renderer);
			this.worldTab.getNetworkView().getCameraView().addComponentListener(new CameraViewListener());
			this.worldTab.getNetworkView().getCameraView().addMouseListener(mouseListener);
			this.worldTab.getNetworkView().getCameraView().addMouseMotionListener(mouseListener);
			this.worldTab.getZoomField().addZoomListener(this);
			this.worldTab.getNetworkView().getCameraView().revalidate();
			this.worldTab.getNetworkView().injectSnapshot(new CameraModelSnapshot(this.cameraModel));
			
			this.worldTab.getNetworkView().updateScrollbars();
			this.eventBus.register(this);
			
			this.currentEditMode = this.editModes.get(this.worldTab.getSelectedMode());
			if(null == this.currentEditMode) {
				throw new IllegalStateException("Unknown edit mode: "+this.worldTab.getSelectedMode());
			}
			this.currentEditMode.modeEnabled();
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if(e.getValueIsAdjusting()) {
			NetworkView newview = this.worldTab.getNetworkView();
			this.cameraModel.setPos((double) newview.getHorizontalScrollBarValue(), (double) newview.getVerticalScrollBarValue());
			this.eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(this.cameraModel)));
		}
	}

	@Override
	public void zoomLevelChanged(ZoomChangeEvent event) {
		this.cameraModel.setMpp(0.5 / (event.getZoomLevel() / 100.0));
		this.eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(this.cameraModel)));
		this.worldTab.getNetworkView().updateScrollbarPositions();
	}
	
	@Subscribe
	public void notifyExternalWorldSizeChange(WorldSizeChangedEvent event) {
		this.worldTab.getNetworkView().updateScrollbarPositions();
	}
	
	@Subscribe
	public void notifyCameraUpdated(CameraUpdatedEvent event) {
		this.worldTab.getNetworkView().injectSnapshot(event.getSnapshot());
		this.worldTab.getNetworkView().updateScrollbars();
		this.worldTab.getNetworkView().updateScrollbarPositions();
	}

	@Override
	public void modeChanged(WorldTab.WorldTabEvent event) {
		if(null != this.currentEditMode) {
			this.currentEditMode.modeDisabled();
			this.currentEditMode = null;
		}
		this.currentEditMode = this.editModes.get(event.getMode());
		if(null != this.currentEditMode) {
			this.currentEditMode.modeEnabled();
		}
	}

	/**
	 * When the window changes its dimensions, we must update the camera view as well.
	 */
	class CameraViewListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			Component component = e.getComponent();
			
			cameraModel.setViewportDimensionPx(component.getWidth(), component.getHeight());
			eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(cameraModel)));
		}
	}
	
	/**
	 * This listener is registered directly in the camera view, it handles mouse dragging,
	 * and zooming.
	 */
	class CameraMouseMotionListener extends MouseInputAdapter {
		private int draggedX;
		private int draggedY;
		private double posX;
		private double posY;
		private boolean draggingEnabled = false;
		
		private int button;
		
		@Override
		public void mousePressed(MouseEvent e) {
			this.draggedX = e.getX();
			this.draggedY = e.getY();
			this.posX = cameraModel.getPosX();
			this.posY = cameraModel.getPosY();
			this.button = e.getButton();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(this.button == MouseEvent.BUTTON3) {
				int distX = e.getX() - this.draggedX;
				int distY = e.getY() - this.draggedY;

				cameraModel.setPos(this.posX - cameraModel.worldDistance(distX), this.posY - cameraModel.worldDistance(distY));
				eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(cameraModel)));
				worldTab.getNetworkView().updateScrollbarPositions();
				this.draggingEnabled = true;
			} else if(this.button == MouseEvent.BUTTON1) {
				if(currentEditMode.captureDragEvents()) {
					
				}
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			if(currentEditMode.captureMotionEvents()) {
				currentEditMode.mouseMoves(
					cameraModel.pix2worldX(e.getX()),
					cameraModel.pix2worldY(e.getY()),
					e.isAltDown(),
					e.isControlDown()
				);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// We do not want to emit dragging-related events to the edit mode, because this event
			// has already been consumed by the scroll process and it could distrupt the edition.
			if(e.getButton() == MouseEvent.BUTTON3 && !this.draggingEnabled) {
				currentEditMode.rightActionPerformed(cameraModel.pix2worldX(e.getX()),
					cameraModel.pix2worldY(e.getY()), e.isAltDown(), e.isControlDown());
			}
			if(e.getButton() == MouseEvent.BUTTON1) {
				currentEditMode.leftActionPerformed(cameraModel.pix2worldX(e.getX()),
					cameraModel.pix2worldY(e.getY()), e.isAltDown(), e.isControlDown());
			}
			this.draggingEnabled = false;
			this.button = 0;
		}
	}
}
