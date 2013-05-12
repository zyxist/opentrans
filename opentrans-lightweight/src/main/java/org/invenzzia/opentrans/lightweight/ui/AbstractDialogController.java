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

package org.invenzzia.opentrans.lightweight.ui;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some common code and logic for OpenTrans dialogs. We often want to use
 * event bus there, but it is quite hard to manage its registartion and
 * deregistration externally, so instead, we deal with it here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AbstractDialogController<T extends JDialog> {
	@Inject
	protected EventBus eventBus;
	/**
	 * Here we have our dialog view.
	 */
	protected T dialog;
	
	public void setView(T dialog) {
		this.dialog = Preconditions.checkNotNull(dialog, "The dialog passed to the dialog controller cannot be NULL.");
		this.dialog.addWindowListener(new DeregisteringWindowListener());
		this.eventBus.register(this);
	}
	
	/**
	 * Deregister from the event bus, when the dialog is being closed.
	 */
	class DeregisteringWindowListener extends WindowAdapter {
		private final Logger logger = LoggerFactory.getLogger(DeregisteringWindowListener.class);

		@Override
		public void windowClosed(WindowEvent e) {
			if(logger.isDebugEnabled()) {
				logger.debug("Deregistering the dialog '"+AbstractDialogController.this.getClass().getSimpleName()+"' from the event bus.");
			}
			eventBus.unregister(AbstractDialogController.this);
		}
	}
}
