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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java API preferences implementation which stores the preferences in a single file rather than a directory structure. It is dedicated for
 * Unix systems.
 *
 * @author Tomasz Jędrzejewski
 */
public class UnixFilePreferences extends AbstractPreferences {
	private static final Logger logger = LoggerFactory.getLogger(UnixFilePreferences.class);

	private Map<String, String> root;
	private Map<String, UnixFilePreferences> children;
	private boolean isRemoved = false;
	
	public UnixFilePreferences(AbstractPreferences parent, String name) {
		super(parent, name);
		
		this.root = new TreeMap<>();
		this.children = new TreeMap<>();
		
		try {
			this.sync();
		} catch(BackingStoreException e) {
			logger.error("Unable to sync during the creation of node '{}'", name);
		}
	}

	@Override
	protected void putSpi(String key, String value) {
		this.root.put(key, value);
		try {
			this.flush();
		} catch(BackingStoreException e) {
			logger.error("Unable to flush after putting the key '{}'", key, e);
		}
	}

	@Override
	protected String getSpi(String key) {
		return this.root.get(key);
	}

	@Override
	protected void removeSpi(String key) {
		this.root.remove(key);
		try {
			this.flush();
		} catch(BackingStoreException e) {
			logger.error("Unable to flush after removing the key '{}'", key);
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		this.isRemoved = true;
		this.root.clear();
		this.flush();
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return this.root.keySet().toArray(new String[root.keySet().size()]);
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return this.children.keySet().toArray(new String[children.keySet().size()]);
	}

	@Override
	protected AbstractPreferences childSpi(String key) {
		UnixFilePreferences prefs = this.children.get(key);
		if(null == prefs) {
			prefs = new UnixFilePreferences(this, key);
		} else if(prefs.isRemoved()) {
			prefs.children.clear();
			prefs.isRemoved = false;
		}
		return prefs;
	}
	
	/**
	 * Override the default implementation to optimize disk I/O operations. We do not have to
	 * read the file for each node.
	 * 
	 * @throws BackingStoreException 
	 */
	@Override
	public void sync() throws BackingStoreException {
		final File file = UnixFilePreferenceFactory.getPreferencesFile();
		final Properties props = new Properties();
		synchronized(file) {
			try {
				props.load(new FileInputStream(file));
			} catch(IOException exception) {
				throw new BackingStoreException(exception);
			}
		}
		StringBuilder path = new StringBuilder();
		this.getPath(path);
		this.syncInternal(path.toString(), props);
	}
	
	private void syncInternal(String path, Properties props) {
		AbstractPreferences cachedKids[];

		synchronized(lock) {
			this.syncSpi(path, props);
			cachedKids = this.cachedChildren();
		}
		for(AbstractPreferences prefs: cachedKids) {
			((UnixFilePreferences)prefs).syncInternal(path+"."+prefs.name(), props);
		}
	}
	
	protected void syncSpi(String topPath, Properties props) {
		if(this.isRemoved) {
			return;
		}
		
		final Enumeration<?> names = props.propertyNames();
		while(names.hasMoreElements()) {
			String key = (String) names.nextElement();
			if(key.startsWith(topPath)) {
				String subKey = key.substring(topPath.length() + 1);
				if(subKey.indexOf(".") == -1) {
					this.root.put(subKey, props.getProperty(key));
				}
			}
		}
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		// unused.
	}
	
	/**
	 * Override the default implementation to optimize disk I/O operations. We do not have to
	 * read the file for each node.
	 * 
	 * @throws BackingStoreException 
	 */
	@Override
	public void flush() throws BackingStoreException {
		final File file = UnixFilePreferenceFactory.getPreferencesFile();
		final Properties props = new Properties();
		synchronized(file) {
			try {
				StringBuilder path = new StringBuilder();
				this.getPath(path);
				
				props.load(new FileInputStream(file));
				this.flushInternal(path.toString(), props);				
				props.store(new FileOutputStream(file), "UnixFilePreferences");
			} catch(IOException exception) {
				throw new BackingStoreException(exception);
			}
		}
	}
	
	private void flushInternal(String path, Properties props) {
		AbstractPreferences cachedKids[];
		synchronized(lock) {
			this.flushSpi(path, props);
			cachedKids = this.cachedChildren();
		}
		for(AbstractPreferences prefs: cachedKids) {
			((UnixFilePreferences)prefs).flushInternal(path+"."+prefs.name(), props);
		}
	}
	
	protected void flushSpi(String topPath, Properties props) {
		List<String> toRemove = new ArrayList<>(this.root.size());
		final Enumeration<?> names = props.propertyNames();
		while(names.hasMoreElements()) {
			String key = (String) names.nextElement();
			if(key.startsWith(topPath)) {
				String subKey = key.substring(topPath.length());
				if(subKey.indexOf(".") == -1) {
					toRemove.add(key);
				}
			}
		}
		for(String key: toRemove) {
			props.remove(key);
		}
		if(!this.isRemoved) {
			for(Map.Entry<String, String> entry: this.root.entrySet()) {
				props.setProperty(topPath + "." + entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		// Unused.
	}
	
	private void getPath(StringBuilder sb) {
		final UnixFilePreferences parent = (UnixFilePreferences) parent();
		if(null == parent) {
			return;
		}
		parent.getPath(sb);
		if(sb.length() != 0) {
			sb.append(".");
		}
		sb.append(this.name());
	}
}
