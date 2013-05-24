/*
 * Copyright (C) 2013 zyxist
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.lightweight.ui.dialogs.lines;

import com.google.inject.Inject;
import org.invenzzia.helium.history.History;
import org.invenzzia.opentrans.lightweight.controllers.IActionScanner;
import org.invenzzia.opentrans.lightweight.controllers.IFormScanner;
import org.invenzzia.opentrans.lightweight.ui.AbstractDialogController;
import org.invenzzia.opentrans.lightweight.ui.IDialogBuilder;
import org.invenzzia.opentrans.visitons.editing.ICommand;

/**
 *
 * @author zyxist
 */
public class LineDialogController extends AbstractDialogController<LineDialog> {
	/**
	 * Required to save the state at the end of work.
	 */
	@Inject
	private History<ICommand> history;
	/**
	 * Provides dialog creation.
	 */
	@Inject
	private IDialogBuilder dialogBuilder;
	/**
	 * Binds the view buttons to the controller actions.
	 */
	@Inject
	private IActionScanner actionScanner;
	@Inject
	private IFormScanner formScanner;
}
