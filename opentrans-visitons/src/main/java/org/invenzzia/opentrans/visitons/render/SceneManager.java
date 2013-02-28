/*
 * Visitons - public transport simulation engine
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.visitons.render;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scene manager serves as a bridge between the renderer and the other
 * threads. If any data are changed, the interested thread puts here
 * the updated snapshot which replaces the old copy. The renderer will
 * take it during the next run.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class SceneManager {
	private Map<Object, Object> scene;
	
	private boolean batch = false;
	
	private Lock lock;
	
	public SceneManager() {
		this.lock = new ReentrantLock();
		this.scene = new LinkedHashMap<>();
	}
	
	/**
	 * Perform a lock for batch update of several keys at once. Call {@link unguard} when
	 * the updating is finished.
	 */
	public void guard() {
		this.lock.lock();
		this.batch = true;
	}
	
	public void updateResource(Object key, Object value) {
		if(this.batch) {
			throw new IllegalStateException("Call of the updateResource() method in batch mode!");
		}
		try {
			this.lock.lock();
			if(null == value) {
				this.scene.remove(key);
			} else {
				this.scene.put(key, value);
			}
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * The updating method used during the batch update. It implements the fluent
	 * interface.
	 * 
	 * @param key The key to update.
	 * @param value The new value.
	 * @return Fluent interface.
	 */
	public SceneManager batchUpdateResource(Object key, Object value) {
		if(!this.batch) {
			throw new IllegalStateException("Call of the batchUpdateResource() method in normal mode!");
		}
		if(null == value) {
			this.scene.remove(key);
		} else {
			this.scene.put(key, value);
		}
		return this;
	}
	
	/**
	 * Unlocks the model after the batch update.
	 */
	public void unguard() {
		if(true == this.batch) {
			this.batch = false;
			this.lock.unlock();
		}
	}

	/**
	 * Returns the current snapshot of objects for drawing the scene.
	 * 
	 * @return Immutable map of scene objects.
	 */
	public Map<Object, Object> getSnapshot() {
		try {
			this.lock.lock();
			return ImmutableMap.copyOf(this.scene);
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Do not use this method inside the rendering loop. Use snapshot instead.
	 * 
	 * @param key Key to return.
	 * @return Object assigned to this key.
	 */
	public Object getResource(Object key) {
		try {
			this.lock.lock();
			return this.scene.get(key);
		} finally {
			this.lock.unlock();
		}
	}
}
