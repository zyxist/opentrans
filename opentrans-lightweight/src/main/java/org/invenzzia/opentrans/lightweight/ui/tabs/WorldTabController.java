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

package org.invenzzia.opentrans.lightweight.ui.tabs;

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
import java.awt.event.MouseWheelEvent;
import javax.swing.event.MouseInputAdapter;
import org.invenzzia.opentrans.lightweight.concurrent.ModelThread;
import org.invenzzia.opentrans.lightweight.concurrent.RenderingThread;
import org.invenzzia.opentrans.lightweight.events.CameraUpdatedEvent;
import org.invenzzia.opentrans.lightweight.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField.IZoomListener;
import org.invenzzia.opentrans.lightweight.ui.component.ZoomField.ZoomChangeEvent;
import org.invenzzia.opentrans.lightweight.ui.netview.NetworkView;
import org.invenzzia.opentrans.visitons.network.Segment;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot.SegmentInfo;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class WorldTabController implements AdjustmentListener, IZoomListener {
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
	 * The managed tab.
	 */
	private WorldTab worldTab;
	/**
	 * Current mouse listener.
	 */
	private CameraMouseMotionListener mouseListener;
	
	public void setWorldTab(WorldTab worldTab) {
		if(null != this.worldTab) {
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
		}
	}
	/*
	public void cameraNeedsRefresh() {
		final CameraModelSnapshot snapshot = new CameraModelSnapshot(cameraModel);
		final VisibleSegmentSnapshot vss = new VisibleSegmentSnapshot();
		
		try {
			final World world = this.worldProvider.get();
			if(null != world) {
				this.modelThread.enqueueAndWait(new Runnable() {
					@Override
					public void run() {
						for(Segment segment: world.getVisibleSegments(snapshot)) {
							vss.addSegmentInfo(new SegmentInfo(segment, null));
						}
					}
				});
			}
			sceneManager.guard();
			try {
				sceneManager.batchUpdateResource(CameraModelSnapshot.class, snapshot);
				sceneManager.batchUpdateResource(VisibleSegmentSnapshot.class, vss);
			} finally {
				sceneManager.unguard();
			}

		} catch(InterruptedException exception) {
			
		}
	}
	*/

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
		
		@Override
		public void mousePressed(MouseEvent e) {
			this.draggedX = e.getX();
			this.draggedY = e.getY();
			this.posX = cameraModel.getPosX();
			this.posY = cameraModel.getPosY();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			int distX = e.getX() - this.draggedX;
			int distY = e.getY() - this.draggedY;
			
			cameraModel.setPos(this.posX - cameraModel.worldDistance(distX), this.posY - cameraModel.worldDistance(distY));
			eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(cameraModel)));
			worldTab.getNetworkView().updateScrollbarPositions();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3) {
				cameraModel.setPos(this.posX, this.posY);
				eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(cameraModel)));
				worldTab.getNetworkView().updateScrollbarPositions();
			}
		}
	}
}
