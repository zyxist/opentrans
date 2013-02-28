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

import javax.swing.JDialog;

/**
 * Contains several utilities for quick dialog displaying.
 * 
 * @author Tomasz Jędrzejewski
 */
public interface IDialogBuilder {
	/**
	 * Shows an error message.
	 * 
	 * @param title
	 * @param message 
	 */
	public void showError(String title, String message);
	/**
	 * Shows an error message from an exception.
	 * 
	 * @param title
	 * @param messageHolder 
	 */
	public void showError(String title, Exception messageHolder);
	/**
	 * Shows a warning message.
	 * 
	 * @param title
	 * @param message 
	 */
	public void showWarning(String title, String message);
	/**
	 * Shows a warning message from an exception.
	 * @param title
	 * @param messageHolder 
	 */
	public void showWarning(String title, Exception messageHolder);
	/**
	 * Shows an information message.
	 * 
	 * @param title
	 * @param message 
	 */
	public void showInformation(String title, String message);
	/**
	 * Shows an information message from exception.
	 * 
	 * @param title
	 * @param messageHolder 
	 */
	public void showInformation(String title, Exception messageHolder);
	
	/**
	 * Creates a new modal dialog. The constructor of the dialog class must accept
	 * {@link JFrame} and a modality flag.
	 * 
	 * @param modalDialogClass The class identifying the modal dialog.
	 * @return Modal dialog instance.
	 */
	public <T extends JDialog> T createModalDialog(Class<T> modalDialogClass);

	/**
	 * Shows a confirmation dialog with the specified message. The method shall stop
	 * the GUI until the user answers the question.
	 * 
	 * @param title The dialog title.
	 * @param message Dialog message.
	 * @return True, if the user answered 'Yes'.
	 */
	public boolean showConfirmDialog(String title, String message);
}
