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
package org.invenzzia.opentrans.client.context;

import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.gui.context.AbstractContext;

/**
 * Context for the OpenTrans client. Represents a case, where no project
 * is open.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ClientContext extends AbstractContext {
	public ClientContext(Application application) {
		super(application);
	}

	@Override
	protected boolean startup() {
		this.container.start();
		return true;
	}

	@Override
	protected boolean shutdown() {
		this.container.stop();
		return true;
	}
}
