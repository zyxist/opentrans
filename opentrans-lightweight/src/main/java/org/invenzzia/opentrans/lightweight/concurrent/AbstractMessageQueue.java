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

package org.invenzzia.opentrans.lightweight.concurrent;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.jcip.annotations.GuardedBy;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public abstract class AbstractMessageQueue extends AbstractExecutionThreadService {
	/**
	 * Lock for controlling the access to the request queue.
	 */
	private final Lock lock = new ReentrantLock();
	/**
	 * For signalling the arrival of the new request.
	 */
	private final Condition notEmpty = this.lock.newCondition();
	/**
	 * Requests awaiting processing.
	 */
	protected final List<Runnable> messages = new LinkedList<>();
	/**
	 * For the implementation of {@link AbstractMessageQueue#enqueueAndWait}
	 */
	protected final Map<Runnable, CountDownLatch> blockers = new LinkedHashMap<>();
	/**
	 * The execution thread.
	 */
	private Thread executionThread;
	
	/**
	 * Enqueues a new task to be executed by this message queue.
	 *
	 * @param request
	 */
	public void enqueue(Runnable msg) {
		if(this.state() == State.TERMINATED) {
			throw new IllegalStateException("The scheduler '%s' is terminated.");
		}
		this.lock.lock();
		try {
			this.messages.add(msg);
			this.notEmpty.signal();
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Returns true, if the execution of the given code section takes place
	 * in the thread of this message queue.
	 * 
	 * @return True, if we are in the thread of this message queue.
	 */
	public boolean isIn() {
		return this.executionThread == Thread.currentThread();
	}
	
	/**
	 * Enqueues a new task to be executed by this message queue.
	 *
	 * @param request
	 */
	public void enqueueAndWait(Runnable msg) throws InterruptedException {
		if(this.state() == State.TERMINATED) {
			throw new IllegalStateException("The scheduler '%s' is terminated.");
		}
		CountDownLatch latch = new CountDownLatch(1);
		
		this.lock.lock();
		try {
			this.blockers.put(msg, latch);
			this.messages.add(msg);
			this.notEmpty.signal();
		} finally {
			this.lock.unlock();
		}
		latch.await();
	}


	@Override
	public void run() {
		if(this.state() == State.TERMINATED) {
			throw new IllegalStateException("The scheduler '%s' is terminated.");
		}
		this.executionThread = Thread.currentThread();
		try {
			main:while(this.isRunning()) {
				this.lock.lock();
				try {
					this.executeStep();
				} catch(InterruptedException exception) {
					if(!this.isRunning()) {
						break main;
					}
				} finally {
					this.lock.unlock();
				}
			}
		} finally {
			this.executionThread = null;
		}
	}
	
	/**
	 * Clears the request buffer by executing all the remaining requests. This is done to avoid deadlocks when we terminate the
	 * scheduler and there are some threads waiting on their future objects.
	 */
	@Override
	protected void shutDown() {
		this.lock.lock();
		try {
			this.processMessages();
		} finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Your code goes here.
	 * 
	 * @throws InterruptedException 
	 */
	abstract protected void executeStep() throws InterruptedException;
	
	/**
	 * Suspends the execution of the thread until new requests are available.
	 */
	protected void waitForMessages() throws InterruptedException {
		while(this.messages.isEmpty()) {
			this.notEmpty.await();
		}
	}
	
	/**
	 * Processes all the messages in the queue.
	 */
	@GuardedBy("guard()")
	protected void processMessages() {
		while(!this.messages.isEmpty()) {
			Runnable runnable = this.messages.remove(0);
			try {
				runnable.run();
			} finally {
				CountDownLatch latch = this.blockers.remove(runnable);
				if(null != latch) {
					latch.countDown();
				}
			}
		}
	}
	
	/**
	 * Locks the messaging queue.
	 */
	protected void guard() {
		this.lock.lock();
	}
	
	/**
	 * Unlocks the messaging queue.
	 */
	protected void unguard() {
		this.lock.unlock();
	}
	
}
