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

package org.invenzzia.opentrans.lightweight.ui.navigator;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;
import org.invenzzia.opentrans.visitons.Project;

/**
 * Use this class as the basis for implementing your own navigation models
 * that can actually display something.
 * 
 * @param T Type of displayed objects
 * @author Tomasz Jędrzejewski
 */
public abstract class NavigatorModel<T> extends AbstractListModel {
	/**
	 * Current list of items.
	 */
	private List<T> items;
	
	/**
	 * Returns the name of the displayed object type.
	 * @return name of the displayed object type
	 */
	public abstract String getObjectName();
	
	/**
	 * Put your data importing code here. The method shall be executed in the
	 * model thread and spawn {@link #installItems()} to install the new item
	 * list and notify the listeners. The method is called, when the model
	 * is installed and every time the history is changed to reflect the updated
	 * state.
	 * 
	 * @param project 
	 */
	public abstract void loadItems(Project project);
	
	/**
	 * You can use this method to popuplate the list. The method is
	 * executed asynchronously in Swing thread.
	 * 
	 * @param items 
	 */
	@InSwingThread(asynchronous = true)
	public void installItems(List<T> items) {
		this.items = ImmutableList.copyOf(items);
		this.fireContentsChanged(this, 0, this.items.size());
	}

	@Override
	public final int getSize() {
		if(null == this.items) {
			return 0;
		}
		return this.items.size();
	}

	@Override
	public final T getElementAt(int index) {
		if(null == this.items) {
			return null;
		}
		return this.items.get(index);
	}
}
