/*
 * Helium - a set of useful stuff for java.
 * 
 * Helium is free software: you can redistribute it and/or modify
 * it under the terms of the New BSD license as published by
 * Invenzzia Group.
 *
 * Helium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * New BSD license for more details.
 *
 * You should have received a copy of the new BSD license
 * along with Helium. If not, see <http://invenzzia.org/license/new-bsd>.
 */
package org.invenzzia.opentrans.lightweight.system;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various utilities for the GUI.
 * 
 * @author Tomasz Jędrzejewski
 */
public class GuiUtils {
	private static final Logger logger = LoggerFactory.getLogger(GuiUtils.class);
	
	private GuiUtils() {
	}
	
	/**
	 * Installs the Nimbus look and feel, if it is supported.
	 */
	public static void installLookAndFeel() {
		try {
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception) {
			GuiUtils.logger.warn("Cannot set look&feel.", exception);
		}
	}
	
	/**
	 * Helps selecting the proper screen for the window in the multimonitor environments.
	 * 
	 * @param screen Default screen number.
	 * @param window The window to place on the given screen.
	 */
	public static void selectDefaultScreen(int screen, Window window) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		
		if(screen < 0 || screen >= gs.length) {
			screen = 0;
		}
		if(gs.length > 0) {
			gs[screen].setFullScreenWindow(window);
		}
	}
	
	/**
	 * Performs some painting operations tuning, such as enabling the double bufferning.
	 */
	public static void tunePainting() {
		RepaintManager.currentManager(null).setDoubleBufferingEnabled(true);
	}
	
	/**
	 * Centers the window on the given screen.
	 * 
	 * @param screen Default screen number.
	 */
	public static void centerOnScreen(int screen, Window window) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		
		if(screen < 0 || screen >= gs.length) {
			screen = 0;
		}
		if(gs.length > 0) {
			DisplayMode mode = gs[screen].getDisplayMode();

			int posX = (int) mode.getWidth() / 2 - (window.getWidth() / 2);
			int posY = (int) mode.getHeight() / 2 - (window.getHeight() / 2);		
			window.setLocation(posX, posY);
		}
	}
}
