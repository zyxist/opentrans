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

import com.google.common.eventbus.EventBus;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.invenzzia.helium.gui.ContextManagerService;
import org.invenzzia.helium.gui.actions.SimpleActionManager;
import org.invenzzia.helium.gui.mvc.ModelService;
import org.invenzzia.helium.gui.ui.menu.MenuController;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.PopupView;
import org.invenzzia.opentrans.client.context.ProjectContext;
import org.invenzzia.opentrans.visitons.render.CameraModel;
import org.invenzzia.opentrans.visitons.world.Segment;
import org.invenzzia.opentrans.visitons.world.World;

/**
 * A helper class of the netedit view controller which translates the user
 * input to the camera to the appropriate high-level actions and passes them
 * to the current operation mode. The operation mode is responsible for
 * taking the action or ignoring it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetviewCommandTranslator extends MouseAdapter {
	private IOperationMode operationMode;
	private MenuController controller;
	private EventBus eventBus;
	private SimpleActionManager actionManager;
	private NetviewActionInterceptor actionInterceptor;
	private ProjectContext projectContext;
	private CameraModel cameraModel;
	
	public NetviewCommandTranslator(
		MenuController controller,
		EventBus eventBus,
		SimpleActionManager simpleActionManager,
		NetviewActionInterceptor actionInterceptor,
		ContextManagerService ctxService,
		ModelService modelService)
	{
		this.controller = controller;
		this.eventBus = eventBus;
		this.controller.setActionManager(simpleActionManager);
		this.actionInterceptor = actionInterceptor;
		this.actionManager = simpleActionManager;
		this.actionManager.setInterceptor(actionInterceptor);
		this.projectContext = (ProjectContext) ctxService.peekContext();
		this.cameraModel = modelService.get(CameraModel.class);
	}
	
	public void setCurrentOperationMode(IOperationMode operationMode) {
		this.actionManager.unregisterAll();
		this.operationMode = operationMode;
		if(null != operationMode) {
			Object menuActions = this.projectContext.get(this.operationMode.getMenuActions());
			this.actionManager.registerActions(menuActions);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		if(event.getButton() == MouseEvent.BUTTON3) {
			// Right click activates the menu
			this.togglePopupMenu(event);
		} else {
			
		}
	}
	
	/**
	 * Shows the pop-up menu with the extra actions downloaded from the current operation mode.
	 * 
	 * @param event 
	 */
	private void togglePopupMenu(MouseEvent event) {
		if(null != this.operationMode) {
			MenuModel model = this.operationMode.getContextMenuModel();
			if(null != model) {
				actionInterceptor.setClickedElement(this.createClickedElementFromMouseEvent(event));
				PopupView popupView = new PopupView();
				popupView.setModel(model);
				popupView.setController(this.controller);
				popupView.show(event);
			}
		}
	}
	
	/**
	 * Translates a mouse event into a clicked element object.
	 * 
	 * @param event The event to translate.
	 * @return Clicked element 
	 */
	private ClickedElement createClickedElementFromMouseEvent(MouseEvent event) {
		World world = this.projectContext.getProject().getWorld();
		
		double x = this.cameraModel.world2pixX(event.getX());
		double y = this.cameraModel.world2pixY(event.getY());
		
		if(x >= 0.0 && x <= world.getX() * CameraModel.SEGMENT_SIZE && y >= 0 && y <= world.getY() * CameraModel.SEGMENT_SIZE) {
			Segment segment = world.findSegment((int)(x / CameraModel.SEGMENT_SIZE), (int)(y / CameraModel.SEGMENT_SIZE));	
			return new ClickedElement(segment, x % CameraModel.SEGMENT_SIZE, y % CameraModel.SEGMENT_SIZE);
		}
		
		return null;
	}
}
