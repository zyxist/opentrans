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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentListener;
import java.util.Map;
import javax.swing.*;
import org.invenzzia.helium.gui.IconManagerService;
import org.invenzzia.helium.gui.mvc.IView;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.ICameraModelListener;

/**
 * Editor view decorates the camera with a ruler and scroll bars.
 * 
 * @author Tomasz Jędrzejewski
 */
public class EditorView extends JPanel implements IView<NeteditController>, ICameraModelListener {
	private NeteditController controller;
	private Ruler horizontalRuler;
	private Ruler verticalRuler;
	/**
	 * Horizontal scroll bar for the camera.
	 */
	private JScrollBar horizontalBar;
	/**
	 * Vertical scroll bar for the camera.
	 */
	private JScrollBar verticalBar;
	/**
	 * Toolbar with operation buttons.
	 */
	private JToolBar toolBar;	
	/**
	 * Operation button mapping; these buttons are displayed above the camera viewport.
	 */
	private BiMap<IOperation, JComponent> operationButtons = HashBiMap.create();
	/**
	 * The nested view displayed in the middle of this one.
	 */
	private CameraDrawer cameraDrawer;
	/**
	 * Camera viewport model - what we are observing?
	 */
	private CameraModel model;
	/**
	 * Resolving toolbar button icons.
	 */
	private IconManagerService iconManager;
	/**
	 * Is the view attached to a component?
	 */
	private boolean attached = false;
	
	public EditorView() {
		super(new GridBagLayout());
		this.horizontalRuler = new Ruler(Ruler.HORIZONTAL);
		this.horizontalRuler.setPreferredSize(600);
		
		this.verticalRuler = new Ruler(Ruler.VERTICAL);
		this.verticalRuler.setPreferredSize(600);

		this.horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL);
		this.horizontalBar.setMinimum(0);
		this.horizontalBar.setPreferredSize(new Dimension(600, 18));
		
		this.verticalBar = new JScrollBar(JScrollBar.VERTICAL);
		this.verticalBar.setMinimum(0);
		this.verticalBar.setPreferredSize(new Dimension(18, 600));
		
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		this.toolBar.setPreferredSize(new Dimension(1200, 30));
		
		this.updateScrollbars();
		
		this.cameraDrawer = new CameraDrawer();
		this.cameraDrawer.setPreferredSize(new Dimension(1200, 700));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 3;
		c.gridheight = 1;
		c.gridx = c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		this.add(this.toolBar, c);

		c.gridwidth = c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		this.add(this.horizontalRuler, c);
		c.gridy = 3;
		c.anchor = GridBagConstraints.PAGE_END;
		this.add(this.horizontalBar, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		this.add(this.verticalRuler, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(this.verticalBar, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(this.cameraDrawer, c);
	}
	
	public EditorView(IconManagerService iconManager) {
		this();
		this.setIconManagerService(iconManager);
	}
	
	public void setCameraModel(CameraModel model) {
		if(null != this.model) {
			this.horizontalRuler.setModel(null);
			this.verticalRuler.setModel(null);
			this.cameraDrawer.setModel(null);
			this.updateScrollbars();
			this.model.removeCameraModelListener(this);
		}
		this.model = model;
		if(null != this.model) {
			this.horizontalRuler.setModel(model);
			this.verticalRuler.setModel(model);
			this.cameraDrawer.setModel(model);
			this.updateScrollbars();
			this.model.addCameraModelListener(this);
		}
	}
	
	public CameraModel getCameraModel() {
		return this.model;
	}
	
	@Override
	public NeteditController getController() {
		return this.controller;
	}
	
	@Override
	public final void setController(NeteditController controller) {
		if(null != this.controller) {
			this.removeOperationButtons();
			this.controller.detachView(this);
			this.attached = false;
		}
		this.controller = controller;
		if(null != this.controller) {
			this.attached = true;
			this.updateOperationButtons();
			this.controller.attachView(this);
		}
	}
	
	public final void setCameraDrawer(CameraDrawer view) {
		this.cameraDrawer = view;
	}
	
	public final CameraDrawer getCameraDrawer() {
		return this.cameraDrawer;
	}
	
	public final void setIconManagerService(IconManagerService iconManager) {
		this.iconManager = iconManager;
	}

	
	public void addAdjustmentListener(AdjustmentListener listener) {
		this.horizontalBar.addAdjustmentListener(listener);
		this.verticalBar.addAdjustmentListener(listener);
	}
	
	public void removeAdjustmentListener(AdjustmentListener listener) {
		this.horizontalBar.removeAdjustmentListener(listener);
		this.verticalBar.removeAdjustmentListener(listener);
	}
	
	public int getHorizontalScrollValue() {
		return this.horizontalBar.getValue();
	}
	
	public int getVerticalScrollValue() {
		return this.verticalBar.getValue();
	}

	public void updateScrollbars() {
		if(null != this.model) {
			this.horizontalBar.setMaximum((int) this.model.getSizeX());
			this.verticalBar.setMaximum((int) this.model.getSizeY());

			this.horizontalBar.setVisibleAmount((int) this.model.getViewportWidth());
			this.verticalBar.setVisibleAmount((int) this.model.getViewportHeight());
		} else {
			this.horizontalBar.setMaximum(1000);
			this.verticalBar.setMaximum(1000);

			this.horizontalBar.setVisibleAmount(1000);
			this.verticalBar.setVisibleAmount(1000);
		}
	}
	
	@Override
	public void cameraUpdated(CameraModel model) {
		this.updateScrollbars();
		
	}
	
	public void updateOperationButtons() {
		for(IOperation operation: this.controller.getOperations()) {
			JComponent component;
			if(operation instanceof IOperationMode) {
				JToggleButton tgb = new JToggleButton();
				if(null != this.iconManager) {
					tgb.setIcon(this.iconManager.getIcon(operation.getIcon()));
				}
				tgb.setToolTipText(operation.getName());
				
				if(operation == this.controller.getCurrentOperationMode()) {
					tgb.setSelected(true);
				}
				
				tgb.addActionListener(this.controller);
				component = tgb;
			} else {
				JButton btn = new JButton();
				if(null != this.iconManager) {
					btn.setIcon(this.iconManager.getIcon(operation.getIcon()));
				}
				btn.setToolTipText(operation.getName());
				btn.addActionListener(this.controller);
				component = btn;
			}
			this.toolBar.add(component);
			this.operationButtons.put(operation, component);
		}
		this.toolBar.revalidate();
	}
	
	public void attachOperationButtons() {
		for(Map.Entry<IOperation, JComponent> button: this.operationButtons.entrySet()) {
			if(button.getKey() instanceof IOperationMode) {
				((JToggleButton)button.getValue()).addActionListener(this.controller);
			} else {
				((JButton)button.getValue()).addActionListener(this.controller);
			}
		}
	}
	
	public void detachOperationButtons() {
		for(Map.Entry<IOperation, JComponent> button: this.operationButtons.entrySet()) {
			if(button.getKey() instanceof IOperationMode) {
				((JToggleButton)button.getValue()).removeActionListener(this.controller);
			} else {
				((JButton)button.getValue()).removeActionListener(this.controller);
			}
		}
	}
	
	public void removeOperationButtons() {
		if(null != this.controller) {
			this.detachOperationButtons();
		}
		this.toolBar.removeAll();
		this.toolBar.revalidate();
		this.operationButtons.clear();
	}
	
	/**
	 * Allows the controller to map the component back to the original operation object.
	 * 
	 * @param component The component sent by the notification event.
	 * @return Operation object associated with this component.
	 */
	public IOperation getOperationFor(JComponent component) {
		return this.operationButtons.inverse().get(component);
	}
	
	public void keepOperationModeState(IOperationMode opm) {
		JToggleButton tgb = (JToggleButton) this.operationButtons.get(opm);
		tgb.setSelected(!tgb.isSelected());
	}
	
	public void setOperationModeState(IOperationMode opm, boolean state) {
		JToggleButton tgb = (JToggleButton) this.operationButtons.get(opm);
		tgb.setSelected(state);
	}
}
