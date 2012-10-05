/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.factory;

import com.google.common.collect.ImmutableList;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.invenzzia.opentrans.visitons.infrastructure.ITrack;
import org.invenzzia.opentrans.visitons.infrastructure.StraightTrack;
import org.invenzzia.opentrans.visitons.infrastructure.graph.EditableGraph;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.CameraModelSnapshot;
import org.invenzzia.opentrans.visitons.render.SceneManager;
import org.invenzzia.opentrans.visitons.render.scene.TrackSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.TrackSnapshot.DrawableStraightTrack;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot;
import org.invenzzia.opentrans.visitons.render.scene.VisibleSegmentSnapshot.SegmentInfo;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * This factory contains the logic for producing scene information snapshots from
 * the Visitons model upon updating certain data.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SceneFactory {
	private final SceneManager sceneManager;
	private VisitonsProject project;
	private CameraModel cameraModel;
	/**
	 * List of visible segments updated when we move the camera.
	 */
	private List<Segment> visibleSegments;
	/**
	 * Bitmap segments are lazily-loaded only for the visible segments. This map controls the list of currently loaded bitmaps.
	 */
	private Map<String, Image> visibleBitmapImages;
	/**
	 * Errors found during the preparing the scene.
	 */
	private List<String> errors;
	
	public SceneFactory(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		
		this.errors = new LinkedList<>();
		this.visibleBitmapImages = new LinkedHashMap<>();
		this.visibleSegments = new LinkedList<>();
	}
	
	public void setCameraModel(CameraModel cameraModel) {
		this.cameraModel = cameraModel;
	}
	
	public void setVisitonsProject(VisitonsProject visitonsProject) {
		this.project = visitonsProject;
	}
	
	/**
	 * Sometimes, scene errors might occur. They are not critical (usually lack of an external resource),
	 * so they are just listed, so that GUI could make use of it.
	 * 
	 * @return Immutable collection of error messages. 
	 */
	public List<String> getSceneErrors() {
		return ImmutableList.copyOf(this.errors);
	}
	
	/**
	 * Performs the scenario for changing the camera model.
	 */
	public void onCameraUpdate() {
		this.sceneManager.updateResource(CameraModelSnapshot.class, new CameraModelSnapshot(this.cameraModel));
		// Prepare the visible segment information.
		this.findVisibleSegments();
		
		VisibleSegmentSnapshot segments = new VisibleSegmentSnapshot();
		for(Segment s: this.visibleSegments) {
			segments.addSegmentInfo(new SegmentInfo(s, this.visibleBitmapImages.get(s.getImagePath())));
		}
		
		this.sceneManager.updateResource(VisibleSegmentSnapshot.class, segments);
	}
	
	/**
	 * Performs the update, when the world size has been changed.
	 */
	public void onWorldSizeUpdate() {
		this.onCameraUpdate();
	}
	
	/**
	 * Performs the update, when the properties of one of the visible segments are changed.
	 */
	public void onSegmentPropertiesUpdate() {
		// Find visible segments just to refresh the list of bitmap images.
		this.findVisibleSegments();
		VisibleSegmentSnapshot segments = new VisibleSegmentSnapshot();
		for(Segment s: this.visibleSegments) {
			segments.addSegmentInfo(new SegmentInfo(s, this.visibleBitmapImages.get(s.getImagePath())));
		}
		this.sceneManager.updateResource(VisibleSegmentSnapshot.class, segments);
	}
	
	/**
	 * Performs the update for the scenario of changing the infrastructure graph.
	 */
	public void onInfrastructureGraphUpdate() {
		
	}
	
	/**
	 * Visible infrastructure objects have been changed.
	 */
	public void onInfrastructureObjectUpdate() {
		
	}
	
	/**
	 * Visible vehicles have been updated.
	 */
	public void onVehicleUpdate() {
		
	}
	
	protected void findVisibleSegments() {	
		int whereStartsX = (int) Math.round(Math.floor(this.cameraModel.getPosX() / 1000.0));
		int whereStartsY = (int) Math.round(Math.floor(this.cameraModel.getPosY() / 1000.0));
		
		int whereEndsX = (int) Math.round(Math.floor((this.cameraModel.getPosX() + this.cameraModel.getViewportWidth()) / 1000.0));
		int whereEndsY = (int) Math.round(Math.floor((this.cameraModel.getPosY() + this.cameraModel.getViewportHeight()) / 1000.0));
		
		int size = (int)((whereEndsX - whereStartsX + 1) * (whereEndsY - whereStartsY + 1));
		
		Set<Segment> previouslyVisibleSegments = new HashSet<>(this.visibleSegments);
		this.visibleSegments.clear();
		World world = this.project.getWorld();		
		
		for(int x = whereStartsX; x <= whereEndsX; x++) {
			for(int y = whereStartsY; y <= whereEndsY; y++) {
				Segment s = world.findSegment(x, y);
				if(null != s) {
					this.visibleSegments.add(s);
				
					if(null != s.getImagePath() && !this.visibleBitmapImages.containsKey(s.getImagePath())) {
						Image img = null;
						try {
							img = ImageIO.read(new File(s.getImagePath()));
						} catch(IOException exception) {
							this.errors.add(String.format("Cannot load '%s' bitmap image.", s.getImagePath()));
						}
						this.visibleBitmapImages.put(s.getImagePath(), img);
					}
					previouslyVisibleSegments.remove(s);
				}
			}
		}
		for(Segment s: previouslyVisibleSegments) {
			this.visibleBitmapImages.remove(s.getImagePath());
		}
	}
	
	public void onEditableModelUpdate(EditableGraph eg, String key) {
		if(null == eg) {
			this.sceneManager.updateResource(key, null);
		} else {
			TrackSnapshot ts = new TrackSnapshot();
			for(ITrack t: eg.getTracks()) {
				if(t instanceof StraightTrack) {
					ts.addDrawableTrack(new DrawableStraightTrack((StraightTrack) t));
				}
			}
			this.sceneManager.updateResource(key, ts);
		}
	}
}
