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

package org.invenzzia.opentrans.visitons.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.SceneManager;

/**
 * The provider immediately injects a snapshot of the camera model to the scene manager,
 * because without that half of the code may blow up.
 * 
 * @author Tomasz Jędrzejewski
 */
public class CameraModelProvider implements Provider<CameraModel> {
	@Inject
	private SceneManager sceneManager;

	@Override
	public CameraModel get() {
		CameraModel model = new CameraModel();
		this.sceneManager.updateResource(CameraModelSnapshot.class, new CameraModelSnapshot(model));
		return model;
	}
}
