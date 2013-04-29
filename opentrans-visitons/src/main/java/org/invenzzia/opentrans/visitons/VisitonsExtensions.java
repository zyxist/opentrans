/*
 * Copyright (C) 2013 Invenzzia Group <http://www.invenzzia.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.invenzzia.opentrans.visitons;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import org.invenzzia.opentrans.visitons.render.ISceneManagerListener;

/**
 * Guice extension points for Visitons library.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VisitonsExtensions {
	private VisitonsExtensions() {
	}
	
	public static void bindSceneManagerListeners(Binder binder, Class<? extends ISceneManagerListener> ... listeners) {
		Multibinder<ISceneManagerListener> lst = Multibinder.newSetBinder(binder, ISceneManagerListener.class);
		for(Class<? extends ISceneManagerListener> listenerClass: listeners) {
			lst.addBinding().to(listenerClass);
		}
	}
}
