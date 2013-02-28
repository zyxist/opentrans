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

package org.invenzzia.opentrans.lightweight.tasks;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.invenzzia.opentrans.lightweight.system.GuiUtils;
import org.invenzzia.opentrans.lightweight.system.UnixFilePreferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs the setup of the Look&Feel and configures the Java Preferences API.
 * 
 * @author Tomasz Jędrzejewski
 */
public class LoadConfigurationTask implements ITask {
	private final Logger logger = LoggerFactory.getLogger(LoadConfigurationTask.class);

	@Override
	public void startup() {
		GuiUtils.installLookAndFeel();
		if(System.getProperty("os.name").equals("Linux")) {
			System.setProperty("java.util.prefs.PreferencesFactory", UnixFilePreferenceFactory.class.getCanonicalName());
		}
	}

	@Override
	public void shutdown() {
		try {
			Preferences.systemRoot().flush();
			Preferences.userRoot().flush();
		} catch(BackingStoreException ex) {
			this.logger.error("Cannot save the preferences.", ex);
		}
	}

}
