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

package org.invenzzia.opentrans.visitons;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.invenzzia.opentrans.visitons.provider.CameraModelProvider;
import org.invenzzia.opentrans.visitons.provider.SceneManagerProvider;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.listeners.SceneCameraListener;

/**
 * The module that defines the dependencies for Visi
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisitonsModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(CameraModel.class).toProvider(CameraModelProvider.class).in(Singleton.class);
		this.bind(SceneManager.class).toProvider(SceneManagerProvider.class).in(Singleton.class);
		this.bind(Renderer.class);
		
		VisitonsExtensions.bindSceneManagerListeners(this.binder(), SceneCameraListener.class);
	}
}
