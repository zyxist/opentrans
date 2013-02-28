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

import javax.swing.SwingUtilities;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.invenzzia.opentrans.lightweight.annotations.InSwingThread;

/**
 * Forces the method to be always executed in Swing thread through the
 * interception technique.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SwingThreadInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		if(SwingUtilities.isEventDispatchThread()) {
			return mi.proceed();
		} else {
			InterceptionRunnable runnable = new InterceptionRunnable(mi);
			InSwingThread annot = mi.getMethod().getAnnotation(InSwingThread.class);
			if(annot.asynchronous()) {
				SwingUtilities.invokeLater(runnable);
				return null;
			} else {
				SwingUtilities.invokeAndWait(runnable);
				if(null != runnable.getThrowable()) {
					throw runnable.getThrowable();
				} else {
					return runnable.getResult();
				}
			}
		}
	}
}
