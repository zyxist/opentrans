/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons.render.listeners;

import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.ISceneManagerListener;
import org.invenzzia.opentrans.visitons.render.ISceneManagerOperations;
import org.invenzzia.opentrans.visitons.render.scene.CommittedTrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.EditableTrackSnapshot;

/**
 * When the camera model is being updated, we need to notify track snapshots
 * to recalculate the snape objects at the beginning of the next rendering
 * iteration.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SceneCameraListener implements ISceneManagerListener {
	@Override
	public Object[] getListenKeyHints() {
		return new Object[] { CameraModelSnapshot.class };
	}
	
	@Override
	public void notifyObjectChanged(ISceneManagerOperations ops, Object key) {
		EditableTrackSnapshot tracks = ops.getSceneResource(EditableTrackSnapshot.class, EditableTrackSnapshot.class);
		if(null != tracks) {
			tracks.markToRefresh();
		}
		CommittedTrackSnapshot world = ops.getSceneResource(CommittedTrackSnapshot.class, CommittedTrackSnapshot.class);
		if(null != world) {
			world.markToRefresh();
		}
	}
}
