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

package org.invenzzia.opentrans.lightweight.interceptor;

import com.google.common.base.Preconditions;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Carries the method invocation to another thread and remembers the
 * execution results for the purpose of synchronous invocations.
 * 
 * @author Tomasz Jędrzejewski
 */
class InterceptionRunnable implements Runnable {
	private final MethodInvocation invocation;
	private Throwable throwable;
	private Object result;
	
	public InterceptionRunnable(MethodInvocation invocation) {
		this.invocation = Preconditions.checkNotNull(invocation);
	}
	
	public Throwable getThrowable() {
		return this.throwable;
	}
	
	public Object getResult() {
		return this.result;
	}
	
	@Override
	public void run() {
		try {
			this.result = this.invocation.proceed();
		} catch(Throwable thr) {
			this.throwable = thr;
		}
	}
}
