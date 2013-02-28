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
package org.invenzzia.opentrans.lightweight.system;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Custom implementation of the preference factory for Unix systems which
 * stores all the preferences in a single file rather than in a fancy directory
 * structure. In addition, it allows configuring the storage place.
 * 
 * @author Tomasz Jędrzejewski
 */
public class UnixFilePreferenceFactory implements PreferencesFactory {
	private Preferences rootPreferences;
	private static File preferenceFile;
	private final static String PREFERENCE_FILE_PROPERTY = "org.invenzzia.opentrans.preferenceFile";

	@Override
	public Preferences systemRoot() {
		return this.userRoot();
	}

	@Override
	public Preferences userRoot() {
		if(null == this.rootPreferences) {
			this.rootPreferences = new UnixFilePreferences(null, "");
		}
		return this.rootPreferences;
	}

	/**
	 * Returns the file with the preferences. The name of the file to open is obtained from the property, and if it is
	 * not set, it is constructed from a template: ~/.helium-app/preferences.conf
	 * 
	 * @return The preference file.
	 */
	public static File getPreferencesFile() {
		if(null == preferenceFile) {
			String fileName = System.getProperty(PREFERENCE_FILE_PROPERTY);
			if(null == fileName) {
				fileName = System.getProperty("user.home") + File.separator + ".opentrans/preferences.conf";
			}
			preferenceFile = new File(fileName).getAbsoluteFile();
		}
		return preferenceFile;
	}
}
