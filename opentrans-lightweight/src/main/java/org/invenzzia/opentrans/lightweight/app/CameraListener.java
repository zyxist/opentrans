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

package org.invenzzia.opentrans.lightweight.app;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.concurrent.ModelThread;
import org.invenzzia.opentrans.lightweight.events.CameraUpdatedEvent;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.events.NewProjectEvent;
import org.invenzzia.opentrans.visitons.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.visitons.network.Segment;
import org.invenzzia.opentrans.visitons.network.World;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;

/**
 * This listener listens for all the events that might affect the camera
 * state, and properly responds to them. 
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class CameraListener {
	@Inject
	private EventBus eventBus;
	@Inject
	private CameraModel cameraModel;
	@Inject
	private SceneManager sceneManager;
	@Inject
	private Provider<World> worldProvider;
	@Inject
	private ModelThread modelThread;

	@Subscribe
	public void notifyProjectLoaded(NewProjectEvent event) {
		ProjectRecord record = event.getProject();
		this.cameraModel.setWorldSize(record.getSizeX(), record.getSizeY());
		this.eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(this.cameraModel)));
	}
	
	@Subscribe
	public void notifyWorldSizeChanged(WorldSizeChangedEvent event) {
		this.cameraModel.setWorldSize(event.getSizeX(), event.getSizeY());
		this.eventBus.post(new CameraUpdatedEvent(new CameraModelSnapshot(this.cameraModel)));
	}

	@Subscribe
	@InModelThread(asynchronous = true)
	public void notifyCameraUpdated(final CameraUpdatedEvent event) {
		final CameraModelSnapshot snapshot = event.getSnapshot();
		final VisibleSegmentSnapshot vss = new VisibleSegmentSnapshot();
		final World world = this.worldProvider.get();
		if(null != world) {
			for(Segment segment: world.getVisibleSegments(snapshot)) {
				vss.addSegmentInfo(new VisibleSegmentSnapshot.SegmentInfo(segment, null));
			}
			sceneManager.guard();
			try {
				sceneManager.batchUpdateResource(CameraModelSnapshot.class, snapshot);
				sceneManager.batchUpdateResource(VisibleSegmentSnapshot.class, vss);
				world.exportScene(sceneManager, cameraModel, true);
			} finally {
				sceneManager.unguard();
			}
		}
	}
}
