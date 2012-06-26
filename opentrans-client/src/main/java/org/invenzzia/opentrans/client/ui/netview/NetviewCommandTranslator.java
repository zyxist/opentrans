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
import javax.swing.JPopupMenu;
import org.invenzzia.helium.gui.ActionManager;
import org.invenzzia.helium.gui.ui.menu.MenuController;
import org.invenzzia.helium.gui.ui.menu.MenuModel;
import org.invenzzia.helium.gui.ui.menu.PopupView;

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
	private ActionManager actionManager;
	
	public NetviewCommandTranslator(MenuController controller, EventBus eventBus, ActionManager actionManager) {
		this.controller = controller;
		this.eventBus = eventBus;
		this.actionManager = actionManager;
	}
	
	public void setCurrentOperationMode(IOperationMode operationMode) {
		this.operationMode = operationMode;
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
				PopupView popupView = new PopupView();
				popupView.setModel(model);
				popupView.setController(this.controller);
				popupView.show(event);
			}
		}
	}
}
