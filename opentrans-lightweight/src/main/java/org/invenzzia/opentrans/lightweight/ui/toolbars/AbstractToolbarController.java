/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.lightweight.ui.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.invenzzia.opentrans.lightweight.annotations.ToolbarAction;

/**
 * Simplifies writing toolbar controllers by using annotations to tie view
 * buttons and controller actions together. Use {@link ToolbarAction} annotation
 * on both a button field and an appropriate controller action and give them
 * identical names, and they will be tied together.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractToolbarController {
	private Map<String, Method> actions;
	
	public AbstractToolbarController() {
		this.actions = new LinkedHashMap<>();
		
		// Discover all toolbar actions in this controller
		for(Method method: this.getClass().getMethods()) {
			ToolbarAction toolbarAction = method.getAnnotation(ToolbarAction.class);
			if(null != toolbarAction && method.getParameterTypes().length == 0) {
				this.actions.put(toolbarAction.value(), method);
			}
		}
	}
	
	/**
	 * The method shall be used by the controller to discover all the action
	 * buttons within the given view.
	 * 
	 * @param panel The panel to scan.
	 */
	protected void installListeners(JToolBar panel) {
		for(Field field: panel.getClass().getDeclaredFields()) {
			ToolbarAction action = field.getAnnotation(ToolbarAction.class);
			
			if(null != action && field.getType() == JButton.class) {
				if(!this.actions.containsKey(action.value())) {
					throw new IllegalStateException("No binding for toolbar action: '"+action.value()+"'");
				}
				try {
					field.setAccessible(true);
					JButton button = (JButton) field.get(panel);
					button.addActionListener(new ToolbarActionListener(action.value()));
				} catch(Exception exception) {
					throw new IllegalStateException("Cannot scan the toolbar panel.", exception);
				}
			}
		}
	}
	
	/**
	 * Fires the button action in the controller.
	 * 
	 * @param actionName The name of action to fire.
	 */
	protected void fireAction(String actionName) {
		try {
			Method actionMethod = this.actions.get(actionName);
			actionMethod.invoke(this);
		} catch(Exception exception) {
			throw new IllegalStateException("Cannot invoke toolbar action method '"+actionName+"'.", exception);
		}
	}

	/**
	 * This listener delegates the button click to an appropriate controller
	 * action.
	 */
	class ToolbarActionListener implements ActionListener {
		private final String actionName;
		
		public ToolbarActionListener(String name) {
			this.actionName = name;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			fireAction(this.actionName);
		}
	}
}