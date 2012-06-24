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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import org.invenzzia.helium.activeobject.SchedulerManager;
import org.invenzzia.helium.gui.events.StatusChangeEvent;
import org.invenzzia.helium.gui.model.InformationModel;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.opentrans.client.concurrent.RenderScheduler;
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
public class NeteditController implements ComponentListener, AdjustmentListener, IController<CameraView>, ICameraModelListener, ActionListener {
	private final Logger logger = LoggerFactory.getLogger(NeteditController.class);
	
	private final CameraModel model;
	private CameraView view;
	private EditorView editorView;
	private SchedulerManager schedulerManager;
	private EventBus eventBus;
	private InformationModel informationModel;
	
	private List<IOperation> operations = new LinkedList<>();
	private IOperationMode currentOperationMode = null;

	public NeteditController(CameraModel model, SchedulerManager schedulerManager, EventBus eventBus, InformationModel informationModel) {
		this.model = Preconditions.checkNotNull(model);
		this.schedulerManager = schedulerManager;
		this.eventBus = eventBus;
		this.informationModel = informationModel;
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
		}
	}
	
	/**
	 * Clears the current list of available operations for this network management object.
	 */
	public void clearOperations() {
		this.operations.clear();
		this.currentOperationMode = null;
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
			this.informationModel.setStatus(((IOperationMode) op).getHelpText());
			this.currentOperationMode = (IOperationMode) op;
			((IOperationMode)op).modeActivated();
			this.editorView.setOperationModeState(this.currentOperationMode, true);
		}
	}
}
