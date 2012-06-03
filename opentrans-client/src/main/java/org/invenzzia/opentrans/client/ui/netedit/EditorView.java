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

import java.awt.*;
import java.awt.event.AdjustmentListener;
import javax.swing.*;
import org.invenzzia.helium.gui.annotation.Card;
import org.invenzzia.helium.gui.mvc.IView;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.render.ICameraModelListener;

/**
 * Editor view decorates the camera with a ruler and scroll bars.
 * 
 * @author Tomasz Jędrzejewski
 */
@Card(position = "editor", title = "Network editor")
public class EditorView extends JPanel implements IView<NeteditController>, ICameraModelListener {
	private final NeteditController controller;
	private Ruler horizontalRuler;
	private Ruler verticalRuler;
	
	private JScrollBar horizontalBar;
	private JScrollBar verticalBar;
	
	private JScrollPane scrollPane;
	private CameraModel model;
	
	public EditorView(NeteditController controller, CameraView view) {
		super(new GridBagLayout());

		this.controller = controller;
		this.model = controller.getModel();

		this.initComponents(view);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		this.controller.attachEditorView(this);
		this.model.addCameraModelListener(this);
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		this.controller.detachEditorView(this);
		this.model.removeCameraModelListener(this);
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

	private void initComponents(Component component) {
		this.horizontalRuler = new Ruler(this.model, Ruler.HORIZONTAL);
		this.horizontalRuler.setPreferredSize(600);
		
		this.verticalRuler = new Ruler(this.model, Ruler.VERTICAL);		
		this.verticalRuler.setPreferredSize(600);
		
		this.horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL);
		this.horizontalBar.setMinimum(0);
		this.horizontalBar.setPreferredSize(new Dimension(600, 18));
		
		this.verticalBar = new JScrollBar(JScrollBar.VERTICAL);
		this.verticalBar.setMinimum(0);
		this.verticalBar.setPreferredSize(new Dimension(18, 600));
		
		this.updateScrollbars();
		
		component.setPreferredSize(new Dimension(600, 600));
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		this.add(this.horizontalRuler, c);
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		this.add(this.horizontalBar, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		this.add(this.verticalRuler, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(this.verticalBar, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.add(component, c);
	}

	public void updateScrollbars() {
		this.horizontalBar.setMaximum((int) this.model.getSizeX());
		this.verticalBar.setMaximum((int) this.model.getSizeY());
		
		this.horizontalBar.setVisibleAmount((int) this.model.getViewportWidth());
		this.verticalBar.setVisibleAmount((int) this.model.getViewportHeight());
	}
	
	@Override
	public void cameraUpdated(CameraModel model) {
		this.updateScrollbars();
		
	}

	@Override
	public NeteditController getController() {
		return this.controller;
	}
}
