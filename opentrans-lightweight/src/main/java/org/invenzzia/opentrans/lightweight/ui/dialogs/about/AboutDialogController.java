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

package org.invenzzia.opentrans.lightweight.ui.dialogs.about;

import com.google.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import org.invenzzia.opentrans.lightweight.AppUtils;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.ui.AbstractDialogController;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.lightweight.ui.dialogs.about.AboutDialog.IAboutDialogListener;

/**
 * Controller for the dialog window - we simply handle closing and clicking
 * on the project URL here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AboutDialogController extends AbstractDialogController<AboutDialog> implements IAboutDialogListener {
	/**
	 * Binds the view buttons to the controller actions.
	 */
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private BrandingModel brandingModel;
	@Inject
	private IDialogBuilder dialogBuilder;

	@Override
	public void setView(AboutDialog dialog) {
		super.setView(dialog);
		dialog.setModel(this.brandingModel);
		dialog.addAboutDialogListener(this);
		this.actionScanner.discoverActions(AboutDialogController.class, this);
		this.actionScanner.bindComponents(AboutDialog.class, dialog);
	}
	
	@Action("okButton")
	public void okClicked() {
		this.dialog.dispose();
	}

	@Override
	public void websiteUrlClicked(AboutDialog.AboutDialogEvent event) {
		try {
			AppUtils.openBrowser(new URI(event.getUrl()));
		} catch(URISyntaxException exception) {
			this.dialogBuilder.showError("Invalid address", "Unfortunately the author is a n00b and can't write the correct URL...");
		}
	}
}
