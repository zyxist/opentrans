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

package org.invenzzia.opentrans.lightweight.ui.dialogs;

import com.google.common.base.Preconditions;
import javax.swing.JDialog;
import org.invenzzia.opentrans.lightweight.ui.forms.IFormStatusListener;

/**
 * Small utility for working with form dialogs that automatically closes the
 * dialog window after successful form submission.
 * 
 * @author Tomasz Jędrzejewski
 */
public class FormDialogCloser implements IFormStatusListener {
	private final JDialog dialog;
	
	public FormDialogCloser(JDialog dialog) {
		this.dialog = Preconditions.checkNotNull(dialog, "Form dialog closer cannot work with NULL dialog.");
	}

	@Override
	public void onSuccessfulSubmit() {
		this.dialog.setVisible(false);
	}

	@Override
	public void onFailedSubmit() {
	}
}
