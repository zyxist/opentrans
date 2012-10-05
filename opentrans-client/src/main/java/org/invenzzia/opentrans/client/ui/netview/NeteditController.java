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
package org.invenzzia.opentrans.client.ui.netview;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.JComponent;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.gui.annotation.EventSubscriber;
import org.invenzzia.helium.gui.model.InformationModel;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
import org.invenzzia.opentrans.client.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.visitons.factory.SceneFactory;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.ICameraModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives the input from the camera component, such as mouse motion etc.
 * and updates the camera model if the component gets resized.
 * 
 * @author Tomasz Jędrzejewski
 */
@EventSubscriber
public class NeteditController implements ComponentListener, AdjustmentListener, IController<EditorView>, ICameraModelListener, ActionListener {
	private final Logger logger = LoggerFactory.getLogger(NeteditController.class);
	
	private CameraModel model;
	private CameraDrawer cameraDrawer;
	private EditorView editorView;
	private SchedulerManager schedulerManager;
	private InformationModel informationModel;
	private NetviewCommandTranslator commandTranslator;
	
	/**
	 * Manages updating the rendered data.
	 */
	private SceneFactory sceneFactory;
	
	private List<IOperation> operations = new LinkedList<>();
	private IOperationMode currentOperationMode = null;

	public NeteditController(SchedulerManager schedulerManager, NetviewCommandTranslator translator) {
		this.schedulerManager = schedulerManager;
		this.commandTranslator = translator;
	}
	
	public void setInformationModel(InformationModel im) {
		this.informationModel = im;
	}
	
	public InformationModel getInformationModel() {
		return this.informationModel;
	}
	
	@Inject
	public void setSceneFactory(SceneFactory sceneFactory) {
		this.sceneFactory = sceneFactory;
	}
	
	public SceneFactory getSceneFactory() {
		return this.sceneFactory;
	}
	
	public CameraModel getModel() {
		return this.model;
	}

	/**
	 * Registers a new operation displayed on the toolbar above the camera.
	 * 
	 * @param operation 
	 */
	public void addOperation(IOperation operation) {
		this.operations.add(operation);
		if(operation instanceof IOperationMode && null == this.currentOperationMode) {
			this.currentOperationMode = (IOperationMode) operation;
			this.commandTranslator.setCurrentOperationMode(this.currentOperationMode);
		}
	}
	
	/**
	 * Clears the current list of available operations for this network management object.
	 */
	public void clearOperations() {
		this.operations.clear();
		this.currentOperationMode = null;
		this.commandTranslator.setCurrentOperationMode(null);
		this.editorView.removeOperationButtons();
	}
	
	/**
	 * @return Immutable list of the operations attached to this network view. 
	 */
	public List<IOperation> getOperations() {
		return ImmutableList.copyOf(this.operations);
	}

	/**
	 * @return Currently selected operation mode. The modes are chosen from the toolbar with the toggle buttons.
	 */
	public IOperationMode getCurrentOperationMode() {
		return this.currentOperationMode;
	}
	
	@Override
	public void attachView(EditorView view) {
		this.editorView = view;
		this.editorView.addAdjustmentListener(this);
		
		this.model = editorView.getCameraModel();
		this.model.addCameraModelListener(this);
		
		if(null != view.getCameraDrawer()) {
			this.attachDrawer(view.getCameraDrawer());
		}
	}
	
	@Override
	public void detachView(EditorView view) {
		if(null != this.cameraDrawer) {
			this.detachDrawer(cameraDrawer);
		}
		this.model.removeCameraModelListener(this);
		this.editorView.removeAdjustmentListener(this);
		this.editorView = null;
		this.model = null;
	}
	
	
	public void attachDrawer(CameraDrawer object) {
		this.cameraDrawer = object;
		this.cameraDrawer.addComponentListener(this);
		RenderScheduler rsc = (RenderScheduler) this.schedulerManager.getScheduler("renderer");
		rsc.setCameraView(object);
		
		this.cameraDrawer.addMouseListener(this.commandTranslator);
		this.cameraDrawer.addMouseMotionListener(this.commandTranslator);
	}

	
	public void detachDrawer(CameraDrawer object) {
		RenderScheduler rsc = (RenderScheduler) this.schedulerManager.getScheduler("renderer");
		rsc.setCameraView(null);
		
		this.cameraDrawer.removeMouseListener(this.commandTranslator);
		this.cameraDrawer.removeMouseMotionListener(this.commandTranslator);
		this.cameraDrawer.removeComponentListener(this);
		this.cameraDrawer = null;
	}
	
	@Subscribe
	public void notifyWorldSizeChanged(WorldSizeChangedEvent event) {
		if(null != this.editorView) {
			this.editorView.updateScrollbars();
		}
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

	/**
	 * A listener responsible for receiving events about the updated camera model.
	 * We must update scrollbars then and send the notification to the scene manager,
	 * so that the new model data are reflected by the renderer.
	 * 
	 * @param model 
	 */
	@Override
	public void cameraUpdated(CameraModel model) {
		this.sceneFactory.onCameraUpdate();
		this.editorView.updateScrollbars();
		this.cameraDrawer.revalidate();
		this.cameraDrawer.repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent ae) {
		this.model.setPos((double) this.editorView.getHorizontalScrollValue(), (double) this.editorView.getVerticalScrollValue());
	}

	/**
	 * Handles the toggle buttons and updates the current operation mode.
	 * 
	 * @param actionEvent 
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		IOperation op = this.editorView.getOperationFor((JComponent) actionEvent.getSource());
		if(op instanceof IOperationMode) {
			if(null != this.currentOperationMode) {			
				if(op == this.currentOperationMode) {
					this.editorView.keepOperationModeState((IOperationMode)op);
					return;
				} else {
					this.currentOperationMode.modeDeactivated();
					this.editorView.setOperationModeState(this.currentOperationMode, false);
				}
			}

			this.logger.info("Selecting network view operation mode: {}", op.getName());
			
			if(null != this.informationModel) {
				this.informationModel.setStatus(((IOperationMode) op).getHelpText());
			}
			this.currentOperationMode = (IOperationMode) op;
			((IOperationMode)op).modeActivated();
			this.editorView.setOperationModeState(this.currentOperationMode, true);
			this.commandTranslator.setCurrentOperationMode(this.currentOperationMode);
		}
	}
}
