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

package org.invenzzia.opentrans.lightweight.ui.splash;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.awt.Container;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.invenzzia.opentrans.lightweight.events.SplashEvent;
import org.invenzzia.opentrans.lightweight.exception.SplashScreenException;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.system.GuiUtils;

/**
 * A component that displays a splash screen and manages updating its state.
 * The key information is imported from the branding model, and the updates
 * are done by receiving {@link SplashEvent} objects which bring information
 * about the progress. These events are delivered by an event bus.
 * 
 * @author Tomasz Jędrzejewski
 */
public class SplashScreen extends JWindow {
	private int weight;
	private int currentWeight;
	private String message;
	/**
	 * Actual drawing is delegated to that label.
	 */
	private SplashLabel label;
	/**
	 * Image to paint on the backgroup.
	 */
	private ImageIcon backgroundImage;
	/**
	 * Location of the splash image.
	 */
	private String imagePath;
	/**
	 * Where to import the branding information from?
	 */
	private BrandingModel brandingModel;

	public SplashScreen() {
		this.setAlwaysOnTop(true);
	}
	
	public void setBrandingModel(BrandingModel model) {
		this.brandingModel = Preconditions.checkNotNull(model);
	}
	
	public BrandingModel getBrandingModel() {
		return this.brandingModel;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public int getCurrentWeight() {
		return this.currentWeight;
	}
	
	@Subscribe
	public void notifySplashUpdate(final SplashEvent event) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SplashScreen.this.currentWeight += event.getWeight();
				if(event.getMessage() != null) {
					SplashScreen.this.message = event.getMessage();
				}
				SplashScreen.this.repaint();
			}
		});
	}

	/**
	 * Performs closing of the splash screen in the event dispatch thread.
	 * 
	 * @param scr 
	 */
	public static void close(final SplashScreen scr) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scr.setVisible(false);
				scr.dispose();
			}
		});
	}

	/**
	 * Displays the splash screen. The method must be started in the event dispatch thread.
	 * 
	 * @throws SplashScreenException 
	 */
	public void display() throws SplashScreenException {
		this.importBranding();
		this.importResources();

		this.setSize(this.backgroundImage.getIconWidth(), this.backgroundImage.getIconHeight());

		this.label = new SplashLabel(this);
		this.label.setIcon(this.backgroundImage);
		this.label.setSize(this.backgroundImage.getIconWidth(), this.backgroundImage.getIconHeight());
		this.label.setTextPosition(30, this.backgroundImage.getIconHeight() - 30);
		this.label.setProgressPosition(this.backgroundImage.getIconHeight() - 60);
		this.label.setProgressColor(Color.BLUE);
		this.label.setForeground(Color.WHITE);

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(this.label);
		
		GuiUtils.centerOnScreen(0, this);
		
		this.setVisible(true);
	}

	/**
	 * Imports branding information from the model.
	 * 
	 * @throws SplashScreenException 
	 */
	private void importBranding() throws SplashScreenException {
		this.imagePath = this.brandingModel.getPromoImagePath();
		if(null == this.imagePath) {
			throw new SplashScreenException("The branding model does not contain promo image path for the splash screen.");
		}
	}

	/**
	 * Imports branding resources needed to paint the splash screen.
	 * 
	 * @throws SplashScreenException 
	 */
	private void importResources() throws SplashScreenException {
		URL url = SplashScreen.class.getClassLoader().getResource(this.imagePath);
		if(null == url) {
			throw new SplashScreenException(String.format("Cannot start a splash screen. Image \"%s\" not found in the application resources.", this.imagePath));
		}
		this.backgroundImage = new ImageIcon(url, "splash");
	}
}
