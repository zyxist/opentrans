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
import org.invenzzia.opentrans.visitons.bindings.DefaultImporter;
import org.invenzzia.opentrans.visitons.network.transform.DefaultRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.IRecordImporter;
import org.invenzzia.opentrans.visitons.network.transform.NetworkUnitOfWork;
import org.invenzzia.opentrans.visitons.network.transform.TransformEngine;
import org.invenzzia.opentrans.visitons.network.transform.ops.BindVertices;
import org.invenzzia.opentrans.visitons.network.transform.ops.ConvertToCurvedTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.ConvertToFreeTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.ConvertToStraightTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.CreateNewTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.ExtendTrack;
import org.invenzzia.opentrans.visitons.network.transform.ops.MoveGroup;
import org.invenzzia.opentrans.visitons.network.transform.ops.MoveVertex;
import org.invenzzia.opentrans.visitons.network.transform.ops.SnapTrackToTrack;
import org.invenzzia.opentrans.visitons.provider.CameraModelProvider;
import org.invenzzia.opentrans.visitons.provider.SceneManagerProvider;
import org.invenzzia.opentrans.visitons.provider.TransformEngineProvider;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.HoverCollector;
import org.invenzzia.opentrans.visitons.render.Renderer;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.listeners.SceneCameraListener;
import org.invenzzia.opentrans.visitons.render.stream.DebugPointStream;
import org.invenzzia.opentrans.visitons.render.stream.GridStream;
import org.invenzzia.opentrans.visitons.render.stream.SegmentBitmapStream;
import org.invenzzia.opentrans.visitons.render.stream.TrackStream;

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
		this.bind(HoverCollector.class);
		
		this.bind(GridStream.class).in(Singleton.class);
		this.bind(SegmentBitmapStream.class).in(Singleton.class);
		this.bind(TrackStream.class).in(Singleton.class);
		this.bind(DebugPointStream.class).in(Singleton.class);
		this.bind(NetworkUnitOfWork.class);
		this.bind(TransformEngine.class).toProvider(TransformEngineProvider.class);
		
		VisitonsExtensions.bindTransformOperations(this.binder(),
			CreateNewTrack.class,
			ExtendTrack.class,
			MoveVertex.class,
			MoveGroup.class,
			SnapTrackToTrack.class,
			BindVertices.class,
			ConvertToFreeTrack.class,
			ConvertToCurvedTrack.class,
			ConvertToStraightTrack.class
		);
		
		this.bind(IRecordImporter.class).annotatedWith(DefaultImporter.class).to(DefaultRecordImporter.class).in(Singleton.class);

		VisitonsExtensions.bindSceneManagerListeners(this.binder(), SceneCameraListener.class);
	}
}
