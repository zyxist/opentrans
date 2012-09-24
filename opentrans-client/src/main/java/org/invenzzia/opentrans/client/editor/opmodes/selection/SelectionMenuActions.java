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
package org.invenzzia.opentrans.client.editor.opmodes.selection;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.annotation.Action;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.mvc.ModelService;
import org.invenzzia.opentrans.client.ui.netview.ClickedElement;
import org.invenzzia.opentrans.visitons.factory.SceneFactory;
import org.invenzzia.opentrans.visitons.render.CameraModel;

/**
 * Actions for the context pop-up menu used in the 'selection'
 * mode.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SelectionMenuActions {
	private SceneFactory sceneFactory;
	private EventBus eventBus;
	private CameraModel cameraModel;
	/**
	 * The location of the last selected bitmap - user experience.
	 */
	private String lastBitmapDirectory = null;
	
	public SelectionMenuActions(EventBus eventBus, SceneFactory sceneFactory, ModelService service) {
		this.eventBus = Preconditions.checkNotNull(eventBus);
		this.sceneFactory = Preconditions.checkNotNull(sceneFactory);
		this.cameraModel = service.get(CameraModel.class);
	}
	
	@Action(id="setSegmentBitmap")
	public void setSegmentBitmapAction(ClickedElement element) {
		if(null != element) {
			JFileChooser chooser;
			if(null == this.lastBitmapDirectory) {
				chooser = new JFileChooser(System.getProperty("user.home"));
			} else {
				chooser = new JFileChooser(this.lastBitmapDirectory);
			}
			chooser.addChoosableFileFilter(new SegmentBitmapFilter());
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null)) {
				File theFile = chooser.getSelectedFile();
				element.getSegment().setImagePath(theFile.getAbsolutePath());
				this.sceneFactory.onSegmentPropertiesUpdate();
				eventBus.post(new StatusChangeEvent("New segment image has been set."));
			}
		} else {
			eventBus.post(new StatusChangeEvent("Please click on a segment to select a bitmap image for it."));
		}
	}
	
	@Action(id="removeSegmentBitmap")
	public void removeSegmentBitmapAction(ClickedElement element) {
		if(null != element) {
			element.getSegment().setImagePath(null);
			this.sceneFactory.onSegmentPropertiesUpdate();
			eventBus.post(new StatusChangeEvent("Segment image has been removed."));
		} else {
			eventBus.post(new StatusChangeEvent("Please click on a segment to select a bitmap image for it."));
		}
	}
	
	@Action(id="zoomIn")
	public void zoomInAction(ClickedElement element) {
		if(null != element) {
			this.cameraModel.centerAt(element.getX(), element.getY());
			this.cameraModel.setMpp(this.cameraModel.getMpp() / 2.0);
		}
	}
	
	@Action(id="zoomOut")
	public void zoomOutAction(ClickedElement element) {
		if(null != element) {
			this.cameraModel.centerAt(element.getX(), element.getY());
			this.cameraModel.setMpp(this.cameraModel.getMpp() * 2.0);
		}
	}
	
	/**
	 * Segment bitmap file chooser shall open just image files.
	 */
	class SegmentBitmapFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			if(file.isDirectory()) {
				return true;
			}
			
			switch(this.getExtension(file)) {
				case "jpg":
				case "jpeg":
				case "png":
				case "gif":
				case "tiff":
					return true;
			}
			return false;
		}
		
		/**
		 * Extracts the extension from the file name, converted to lower case.
		 * If the file has no extension, an empty string is returned.
		 * 
		 * @param file
		 * @return File extension.
		 */
		private String getExtension(File file) {
			String name = file.getName();
			int idx = name.lastIndexOf(".");
			if(-1 != idx) {
				return name.substring(idx).toLowerCase();
			}
			return "";
		}

		@Override
		public String getDescription() {
			return "Just images";
		}
	}
}
