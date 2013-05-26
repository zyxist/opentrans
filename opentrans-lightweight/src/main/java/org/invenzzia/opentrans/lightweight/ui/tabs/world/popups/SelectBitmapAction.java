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

package org.invenzzia.opentrans.lightweight.ui.tabs.world.popups;

import com.google.inject.Inject;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.invenzzia.helium.exception.CommandExecutionException;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.annotations.PopupAction;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IEditModeAPI;
import org.invenzzia.opentrans.lightweight.ui.tabs.world.IPopupAction;
import org.invenzzia.opentrans.visitons.editing.ICommand;
import org.invenzzia.opentrans.visitons.editing.operations.SetBitmapCmd;
import org.invenzzia.opentrans.visitons.network.Segment;

/**
 * Allows selecting a background bitmap for a world segment under
 * the cursor.
 * 
 * @author Tomasz Jędrzejewski
 */
@PopupAction(text = "Select bitmap")
public class SelectBitmapAction implements IPopupAction {
	@Inject
	private IDialogBuilder dialogBuilder;
	@Inject
	private History<ICommand> history;
	
	/**
	 * We always remember, where we have loaded the last bitmap.
	 */
	private String lastPath;
	
	@Override
	public void execute(IEditModeAPI api, double x, double y) {
		if(!api.getWorldRecord().isWithinWorld(x, y)) {
			api.setStatusMessage("Bitmaps can be set only for the world segments.");
		} else {
			JFileChooser fc = new JFileChooser();
			
			if(null == this.lastPath) {
				this.lastPath = System.getProperty("home.dir");
			}
			
			fc.setDragEnabled(false);
			fc.setMultiSelectionEnabled(false);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if(f.isDirectory()) {
						return true;
					}
					String name = f.getName();
					int idx = name.lastIndexOf(".");
					if(-1 != idx) {
						String extension = name.substring(idx + 1);
						if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {
							return true;
						}
					}
					return false;
				}

				@Override
				public String getDescription() {
					return "Image files";
				}
			});
			
			if(this.dialogBuilder.showOpenDialog(fc)) {
				File file = fc.getSelectedFile();
				this.lastPath = file.getAbsolutePath();

				try {
					this.history.execute(new SetBitmapCmd(
						(int) Math.floor(x / Segment.SIZE_D),
						(int) Math.floor(y / Segment.SIZE_D),
						file.getAbsolutePath()
					));
					api.setStatusMessage("Bitmap loaded.");
				} catch(CommandExecutionException exception) {
					this.dialogBuilder.showError("Error while setting the bitmap", exception);
				}
			}
		}
	}
}
