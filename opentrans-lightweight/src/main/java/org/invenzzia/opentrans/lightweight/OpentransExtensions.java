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

package org.invenzzia.opentrans.lightweight;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import java.util.ArrayList;
import java.util.List;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditMode;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class OpentransExtensions {
	private OpentransExtensions() {
	}
	
	public static void bindEditModes(Binder binder, Class<? extends IEditMode> ... editModeClasses) {
		ListProvider<IEditMode> lp = new ListProvider<>(editModeClasses);
		binder.requestInjection(lp);
		binder.bind(new TypeLiteral<List<IEditMode>>(){}).toProvider(lp);
	}
	
	/**
	 * Popup actions shall be bound in the same manner.
	 * 
	 * @param binder
	 * @param popupActionClasses 
	 */
	public static void bindPopupActions(Binder binder, Class<? extends IPopupAction> ... popupActionClasses) {
		for(Class<? extends IPopupAction> cls: popupActionClasses) {
			binder.bind(cls).in(Singleton.class);
		}
	}
}

class ListProvider<T> implements Provider<List<T>> {
	@Inject
	private Injector injector;
	
	private final Class<? extends T> implementations[];
	
	public ListProvider(Class<? extends T> implementations[]) {
		this.implementations = implementations;
	}
	
	@Override
	public List<T> get() {
		ArrayList<T> list = new ArrayList<>(this.implementations.length);
		for(Class<? extends T> type: this.implementations) {
			list.add(this.injector.getInstance(type));
		}
		return ImmutableList.copyOf(list);
	}
}