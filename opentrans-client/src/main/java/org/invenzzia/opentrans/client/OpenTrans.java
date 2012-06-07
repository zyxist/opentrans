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
package org.invenzzia.opentrans.client;

import org.invenzzia.helium.application.Application;
import org.invenzzia.opentrans.client.context.ClientContext;

/**
 * The main class for the application.
 * 
 * @author Tomasz Jędrzejewski
 */
public class OpenTrans {
	public static final String VERSION = "0.1.0";
	
	/**
	 * Starts the application. Recognized args:
	 * 
	 * <ul>
	 *  <li><code>--config /path/to/config/dir</code> - stores the user configuration in the given directory</li>
	 *  <li><code>--project /path/to/opentrans/project</code> - opens OpenTrans with the given project</li>	 * 
	 * </ul>
	 * 
	 * @param args 
	 */
	public static void main(String args[]) {
		Application theApp = new Application("storage.xml");
		theApp.run(ClientContext.class);
	} // end main();
} // end OpenTrans;
