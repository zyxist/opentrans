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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.invenzzia.helium.gui.actions.IActionInterceptor;

/**
 * For context pop-ups: we must inject the information about clicked element,
 * if the action method needs it.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NetviewActionInterceptor implements IActionInterceptor {
	private ClickedElement clickedElement;
	
	public void setClickedElement(ClickedElement clickedElement) {
		this.clickedElement = clickedElement;
	}
	
	public ClickedElement getClickedElement() {
		return this.clickedElement;
	}

	@Override
	public void invoke(Object target, Method method) throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
		Class<?> types[] = method.getParameterTypes();
		if(types.length == 1 && types[0] == ClickedElement.class) {
			method.invoke(target, this.clickedElement);
		} else {
			method.invoke(target);
		}
	}
}
