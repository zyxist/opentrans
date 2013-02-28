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

package org.invenzzia.opentrans.lightweight.ui.workspace;

import com.google.common.eventbus.Subscribe;
import java.util.List;
import javax.swing.AbstractListModel;
import org.invenzzia.helium.events.HistoryChangedEvent;
import org.invenzzia.helium.history.CommandInfo;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryModel extends AbstractListModel {
	private List<CommandInfo<ICommand>> commands;

	public HistoryModel(List<CommandInfo<ICommand>> initialCommands) {
		this.commands = initialCommands;
	}
	
	@Subscribe
	public void notifyHistoryChanged(HistoryChangedEvent<ICommand> event) {
		this.commands = event.getHistory().getHistory();
		this.fireContentsChanged(this, 0, this.commands.size());
	}

	@Override
	public int getSize() {
		return this.commands.size();
	}

	@Override
	public Object getElementAt(int index) {
		return this.commands.get(index);
	}
}
