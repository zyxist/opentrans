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
package org.invenzzia.opentrans.client.ui.netedit;

import com.google.common.base.Preconditions;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.ICameraModelListener;

/**
 * Receives the input from the camera component, such as mouse motion etc.
 * and updates the camera model if the component gets resized.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NeteditController implements ComponentListener, AdjustmentListener, IController<CameraView>, ICameraModelListener {
	private final CameraModel model;
	private CameraView view;
	private EditorView editorView;
	private SchedulerManager schedulerManager;

	public NeteditController(CameraModel model, SchedulerManager schedulerManager) {
		this.model = Preconditions.checkNotNull(model);
		this.schedulerManager = schedulerManager;
	}
	
	public CameraModel getModel() {
		return this.model;
	}
	
	public void attachEditorView(EditorView view) {
		this.editorView = view;
		this.editorView.addAdjustmentListener(this);
	}
	
	public void detachEditorView(EditorView view) {
		this.editorView.removeAdjustmentListener(this);
		this.editorView = null;
	}
	
	@Override
	public void attachView(CameraView object) {
		this.view = object;
		this.view.addComponentListener(this);
		RenderScheduler rsc = (RenderScheduler) this.schedulerManager.getScheduler("renderer");
		rsc.setCameraView(object);
	}

	@Override
	public void detachView(CameraView object) {
		RenderScheduler rsc = (RenderScheduler) this.schedulerManager.getScheduler("renderer");
		rsc.setCameraView(null);
		
		this.view.removeComponentListener(this);
		this.view = null;
	}

	@Override
	public void componentResized(ComponentEvent ce) {
		JComponent c = (JComponent) ce.getComponent();
		this.model.setViewportDimensionPx(c.getWidth(), c.getHeight());
	}

	@Override
	public void componentMoved(ComponentEvent ce) {
		
	}

	@Override
	public void componentShown(ComponentEvent ce) {
		JComponent c = (JComponent) ce.getComponent();
		this.model.setViewportDimensionPx(c.getWidth(), c.getHeight());
	}

	@Override
	public void componentHidden(ComponentEvent ce) {
	}

	@Override
	public void cameraUpdated(CameraModel model) {
		this.editorView.updateScrollbars();
		this.view.revalidate();
		this.view.repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent ae) {
		this.model.setPos((double) this.editorView.getHorizontalScrollValue(), (double) this.editorView.getVerticalScrollValue());
	}
}
