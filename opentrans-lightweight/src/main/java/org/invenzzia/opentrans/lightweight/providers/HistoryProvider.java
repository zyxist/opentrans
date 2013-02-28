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

package org.invenzzia.opentrans.lightweight.providers;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.history.History;
import org.invenzzia.helium.history.IHistoryStrategy;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 * Constructs a history manager provided by Helium.
 * 
 * @author Tomasz Jędrzejewski
 */
public class HistoryProvider implements Provider<History<ICommand>> {
	@Inject
	private IHistoryStrategy<ICommand> historyStrategy;
	@Inject
	private EventBus eventBus;

	@Override
	public History<ICommand> get() {
		return new History<>(this.historyStrategy, this.eventBus);
	}
}
