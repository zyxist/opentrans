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

import com.google.inject.Inject;
import java.awt.Frame;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the service for building and managing dialogs within application.
 * It guarantees us that every dialog is bound to the main window.
 * 
 * @author Tomasz Jędrzejewski
 */
public class DefaultDialogBuilder implements IDialogBuilder {
	private final Logger logger = LoggerFactory.getLogger(DefaultDialogBuilder.class);
	/**
	 * Necessary to obtain the main frame reference.
	 */
	@Inject
	private MainWindowController controller;

	@Override
	public void showError(String title, String message) {
		JOptionPane.showMessageDialog(this.getMainFrame(), message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showError(String title, Exception messageHolder) {
		this.showError(title, messageHolder.getMessage());
		this.resolveRuntimeException(messageHolder);
	}

	@Override
	public void showWarning(String title, String message) {
		JOptionPane.showMessageDialog(this.getMainFrame(), message, title, JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void showWarning(String title, Exception messageHolder) {
		this.showWarning(title, messageHolder.getMessage());
		this.resolveRuntimeException(messageHolder);
	}

	@Override
	public void showInformation(String title, String message) {
		JOptionPane.showMessageDialog(this.getMainFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void showInformation(String title, Exception messageHolder) {
		this.showInformation(title, messageHolder.getMessage());
		this.resolveRuntimeException(messageHolder);
	}
	
	@Override
	public <T extends JDialog> T createModalDialog(Class<T> modalDialogClass) {
		try {
			Constructor constructor = modalDialogClass.getConstructor(Frame.class, Boolean.TYPE);
			T dialog = (T) constructor.newInstance(this.getMainFrame(), true);
			dialog.setLocationRelativeTo(this.getMainFrame());
			return dialog;
		} catch(NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
			throw new IllegalArgumentException("The class '"+modalDialogClass.getCanonicalName()+"' is not a valid dialog.", exception);
		} catch(InvocationTargetException exception) {
			throw new IllegalStateException("An exception occurred during the dialog instantiation.", exception);
		}
	}
	
	/**
	 * Returns the reference to the main frame.
	 * 
	 * @return Main frame.
	 */
	private JFrame getMainFrame() {
		return this.controller.getMainWindow();
	}

	@Override
	public boolean showConfirmDialog(String title, String message) {
		int result = JOptionPane.showConfirmDialog(this.getMainFrame(), message, title, JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
	
	private void resolveRuntimeException(Exception messageHolder) {
		if(!(messageHolder instanceof RuntimeException)) {
			Throwable cause = messageHolder.getCause();
			if(null != cause && (cause instanceof RuntimeException)) {
				messageHolder = (RuntimeException) cause;
			}
		}
		if(messageHolder instanceof RuntimeException) {
			this.logger.error("Runtime exception captured.", messageHolder);
		}
	}

	@Override
	public boolean showOpenDialog(JFileChooser fileChooser) {
		int retVal = fileChooser.showOpenDialog(this.getMainFrame());
		return retVal == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public boolean showSaveDialog(JFileChooser fileChooser) {
		int retVal = fileChooser.showSaveDialog(this.getMainFrame());
		return retVal == JFileChooser.APPROVE_OPTION;
	}
}
