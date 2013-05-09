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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scene manager serves as a bridge between the renderer and the other
 * threads. If any data are changed, the interested thread puts here
 * the updated snapshot which replaces the old copy. The renderer will
 * take it during the next run.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
@ThreadSafe
public class SceneManager implements ISceneManagerOperations {
	/**
	 * List of objects on the scene.
	 */
	private Map<Object, Object> scene;
	/**
	 * Is batch mode for updating data enabled? Batch mode allows updating
	 * several objects within a single lock.
	 */
	private boolean batch = false;
	/**
	 * Data structure lock for synchronization between the rendering thread
	 * and the other threads.
	 */
	private Lock lock;
	/**
	 * Listeners that are activated, when a certain key is updated.
	 */
	private Multimap<Object, ISceneManagerListener> listeners;

	public SceneManager() {
		this.lock = new ReentrantLock();
		this.scene = new LinkedHashMap<>();
		this.listeners = LinkedListMultimap.create();
	}
	
	/**
	 * Registers a new scene manager listener.
	 * 
	 * @param key The key that will be updated in order to activate the listener.
	 * @param listener The registered listener.
	 */
	public void addSceneManagerListener(Object key, ISceneManagerListener listener) {
		try {
			this.lock.lock();
			this.listeners.put(key, Preconditions.checkNotNull(listener, "The listener cannot be empty."));
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Perform a lock for batch update of several keys at once. Call {@link unguard} when
	 * the updating is finished.
	 */
	public void guard() {
		this.lock.lock();
		this.batch = true;
	}
	
	/**
	 * Puts a new value into a given key. This is the primary method of passing data
	 * into the rendering thread. It is thread-safe. If you want to pass several
	 * objects within a single lock, use {@link #guard()}, {@link #batchUpdateResource} and
	 * {@link #unguard()} methods.
	 * 
	 * @param key
	 * @param value 
	 */
	public void updateResource(Object key, Object value) {
		try {
			this.lock.lock();
			if(this.batch) {
				throw new IllegalStateException("Call of the updateResource() method in batch mode!");
			}
			if(null == value) {
				this.scene.remove(key);
			} else {
				this.scene.put(key, value);
			}
			this.notifyListenersForKey(key);
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * The updating method used during the batch update. It implements the fluent
	 * interface. Note that you should keep your {@link #unguard()} method within
	 * a <tt>try ... finally ... </tt> block.
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
		this.notifyListenersForKey(key);
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
	
	/**
	 * Do not use this method inside the rendering loop. Use snapshot instead.
	 * 
	 * @param key Key to return.
	 * @param cast Trick for nicer casting.
	 * @return Object assigned to this key.
	 */
	public <T> T getResource(Object key, Class<T> cast) {
		try {
			this.lock.lock();
			return (T) this.scene.get(key);
		} finally {
			this.lock.unlock();
		}
	}
	
	@Override
	public Object getSceneResource(Object key) {
		return this.scene.get(key);
	}
	
	@Override
	public <T> T getSceneResource(Object key, Class<T> cast) {
		return (T) this.scene.get(key);
	}
	
	/**
	 * Sends object update notifications for the given key.
	 * 
	 * @param key 
	 */
	private void notifyListenersForKey(Object key) {
		for(ISceneManagerListener listener: this.listeners.get(key)) {
			listener.notifyObjectChanged(this, key);
		}
	}
}
