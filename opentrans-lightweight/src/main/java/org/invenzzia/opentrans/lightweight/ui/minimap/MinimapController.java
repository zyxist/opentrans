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

package org.invenzzia.opentrans.lightweight.ui.minimap;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.lightweight.events.CameraUpdatedEvent;
import org.invenzzia.opentrans.lightweight.ui.component.Minimap;
import org.invenzzia.opentrans.lightweight.ui.component.Minimap.ISegmentSelectionListener;
import org.invenzzia.opentrans.lightweight.ui.component.Minimap.SegmentSelectionEvent;
import org.invenzzia.opentrans.visitons.events.WorldEvent;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;

/**
 * This controller manages the minimap visible in the main window. Its responsibility
 * is to listen for certain world size change events and provide an appropriate data
 * model for it. In addition, it also listens for the click actions on the minimap,
 * so that we can quickly jump to the specified area.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class MinimapController implements ISegmentSelectionListener {
	@Inject
	private CameraModel cameraModel;
	@Inject
	private EventBus eventBus;
	@Inject
	private Provider<World> worldProvider;
	/**
	 * The managed minimap
	 */
	private Minimap minimap;
	
	public void setView(Minimap view) {
		if(null != this.minimap) {
			this.minimap.removeSegmentSelectionListener(this);
			this.eventBus.unregister(this);
		}
		this.minimap = view;
		if(null != this.minimap) {
			this.eventBus.register(this);
			this.minimap.addSegmentSelectionListener(this);
		}
	}

	@Override
	public void segmentSelected(SegmentSelectionEvent evt) {
		this.cameraModel.centerAt(
			evt.getX() * CameraModel.SEGMENT_SIZE + CameraModel.HALF_SEGMENT_SIZE,
			evt.getY() * CameraModel.SEGMENT_SIZE + CameraModel.HALF_SEGMENT_SIZE
		);
		this.eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(this.cameraModel)));
	}
	
	/**
	 * If the history information is changed, we should refresh the content of minimap,
	 * because some commands that affect the minimap content might have happened.
	 * 
	 * @param event 
	 */
	@Subscribe
	@InSwingThread(asynchronous = true)
	public void notifyWorldEvents(WorldEvent event) {
		this.minimap.setData(event.getWorld().getSegmentUsage());
		this.minimap.repaint();
	}
	
	/**
	 * When the camera is updated, we must refresh the minimap content.
	 * 
	 * @param event 
	 */
	@Subscribe
	@InSwingThread(asynchronous = true)
	public void notifyCameraUpdated(CameraUpdatedEvent event) {
		CameraModelSnapshot snapshot = event.getSnapshot();
		this.minimap.setViewport(snapshot);
		this.minimap.updateData();
		this.minimap.repaint();
	//	final World world = this.worldProvider.get();
	//	this.updateMinimap(world);
	}
	
	/**
	 * Performs an asynchonous update of the minimap content from the
	 * given world instance. The method is executed in the model thread
	 * and once the data is collected, it passes the control back to
	 * Swing, and updates the view.
	 * 
	 * @param world 
	 */
	/*
	@InModelThread(asynchronous = true)
	public void updateMinimap(World world) {
		this.applyWorldDataInformation(world.exportSegmentUsage());
	}
	
	@InSwingThread(asynchronous = true)
	protected void applyWorldDataInformation(boolean segmentUsage[][]) {
		minimap.setData(segmentUsage);
		minimap.repaint();
	}*/
}
