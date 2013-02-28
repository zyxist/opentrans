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

package org.invenzzia.opentrans.lightweight.ui.dialogs.resize;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.InModelThread;
import org.invenzzia.opentrans.lightweight.events.WorldSizeChangedEvent;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialog.IResizeListener;
import org.invenzzia.opentrans.lightweight.ui.dialogs.resize.ResizeDialog.WorldResizeEvent;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.ExtendWorldCmd;
import org.invenzzia.opentrans.visitons.editing.operations.ShrinkWorldCmd;
import org.invenzzia.opentrans.visitons.exception.WorldException;
import org.invenzzia.opentrans.visitons.network.World;

/**
 * Controller that works with the world resize dialog, handling the notifications about
 * world resizing requests. The new controller instance must be created for each dialog.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ResizeDialogController implements IResizeListener {
	@Inject
	private Provider<World> worldProvider;
	@Inject
	private EventBus eventBus;
	@Inject
	private History<ICommand> history;
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * The managed dialog.
	 */
	private ResizeDialog dialog;
	/**
	 * Notifications from the model.
	 */
	private WorldException worldException;
	/**
	 * Container to receive the map data.
	 */
	private boolean data[][];
	
	private int sizeX;
	
	private int sizeY;
	
	/**
	 * Registers the managed dialog window.
	 * 
	 * @param dialog 
	 */	
	public void setView(ResizeDialog dialog) {
		this.dialog = dialog;
		this.dialog.addResizeListener(this);
	}

	@Override
	public void worldResized(final WorldResizeEvent event) {
		try {
			if(event.isExtend()) {
				this.history.execute(new ExtendWorldCmd(event.getDirection()));
			} else {
				this.history.execute(new ShrinkWorldCmd(event.getDirection()));
			}
			this.getDataFromModel(this.worldProvider.get());
			this.eventBus.post(new WorldSizeChangedEvent(this.sizeX, this.sizeY));
			this.dialog.setWorldSize(this.sizeX, this.sizeY);
			this.dialog.setMinimapData(this.data);
		} catch(CommandExecutionException exception) {
			this.dialogBuilder.showError("Cannot change world size.", (Exception) exception.getCause());
		}
	}
	
	@InModelThread(asynchronous = false)
	protected void getDataFromModel(World world) {
		this.sizeX = world.getX();
		this.sizeY = world.getY();
		this.data = world.exportSegmentUsage();
	}
}
