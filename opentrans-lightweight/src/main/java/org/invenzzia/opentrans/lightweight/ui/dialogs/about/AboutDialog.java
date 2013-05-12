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

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;

/**
 * Code of the dialog that displays the information about the application.
 * 
 * @author Tomasz Jędrzejewski
 */
public class AboutDialog extends javax.swing.JDialog {
	/**
	 * Where do we take the information from?
	 */
	private BrandingModel model;
	
	private Set<IAboutDialogListener> listeners;

	/**
	 * Creates new form AboutDialog
	 */
	public AboutDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		this.listeners = new LinkedHashSet<>();
		this.initComponents();
		this.appWebsiteLabel.addMouseListener(new UrlMouseListener());
	}
	
	/**
	 * Adds new dialog listener.
	 * 
	 * @param listener 
	 */
	public void addAboutDialogListener(IAboutDialogListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes the dialog listener.
	 * 
	 * @param listener 
	 */
	public void removeAboutDialogListener(IAboutDialogListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Removes all about dialog listeners.
	 */
	public void removeAboutDialogListeners() {
		this.listeners.clear();
	}
	
	/**
	 * Initializes the dialog with the information from the model.
	 * 
	 * @param brandingModel 
	 */
	public void setModel(BrandingModel brandingModel) {
		this.model = brandingModel;
		if(null != this.model) {
			this.appNameLabel.setText(this.model.getApplicationName());
			this.appVersionLabel.setText(this.model.getApplicationVersion());
			this.appLicenseLabel.setText(this.model.getApplicationLicense());
			this.copyrightLabel.setText(this.model.getApplicationCopyright());
			this.appWebsiteLabel.setText(this.model.getApplicationWebsite());
			this.appAuthorLabel.setText(this.model.getApplicationAuthor());
			URL url = this.getClass().getClassLoader().getResource(this.model.getPromoImagePath());
			if(null != url) {
				this.splashImageLabel.setIcon(new ImageIcon(url, "splash"));
			}
		}
	}
	
	/**
	 * Returns the current branding model.
	 * 
	 * @return Branding model.
	 */
	public BrandingModel getModel() {
		return this.model;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
      // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
      private void initComponents() {

            splashImageLabel = new javax.swing.JLabel();
            appNameLabel = new javax.swing.JLabel();
            appVersionLabel = new javax.swing.JLabel();
            staticLabel1 = new javax.swing.JLabel();
            appAuthorLabel = new javax.swing.JLabel();
            copyrightLabel = new javax.swing.JLabel();
            staticLabel2 = new javax.swing.JLabel();
            appLicenseLabel = new javax.swing.JLabel();
            staticLabel3 = new javax.swing.JLabel();
            appWebsiteLabel = new javax.swing.JLabel();
            okButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("About...");

            splashImageLabel.setText("[Here be splash image]");
            splashImageLabel.setMaximumSize(new java.awt.Dimension(474, 300));
            splashImageLabel.setMinimumSize(new java.awt.Dimension(474, 300));
            splashImageLabel.setPreferredSize(new java.awt.Dimension(474, 300));

            appNameLabel.setText("[App name]");

            appVersionLabel.setText("[App version]");

            staticLabel1.setText("Written by:");

            appAuthorLabel.setText("[App author]");

            copyrightLabel.setText("[App copyright]");

            staticLabel2.setText("License:");

            appLicenseLabel.setText("[App license]");

            staticLabel3.setText("Website:");

            appWebsiteLabel.setForeground(new java.awt.Color(0, 0, 255));
            appWebsiteLabel.setText("[App website]");
            appWebsiteLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            okButton.setText("OK");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(splashImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(layout.createSequentialGroup()
                                    .addComponent(appNameLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(appVersionLabel))
                              .addComponent(copyrightLabel)
                              .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                          .addComponent(staticLabel1)
                                          .addComponent(staticLabel2)
                                          .addComponent(staticLabel3))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                          .addComponent(appWebsiteLabel)
                                          .addComponent(appLicenseLabel)
                                          .addComponent(appAuthorLabel)))))
                  .addGroup(layout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            layout.setVerticalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                        .addComponent(splashImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                              .addComponent(appNameLabel)
                              .addComponent(appVersionLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(copyrightLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(appAuthorLabel)
                              .addComponent(staticLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addComponent(staticLabel2)
                              .addComponent(appLicenseLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                              .addComponent(staticLabel3)
                              .addComponent(appWebsiteLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
      }// </editor-fold>//GEN-END:initComponents

      // Variables declaration - do not modify//GEN-BEGIN:variables
      private javax.swing.JLabel appAuthorLabel;
      private javax.swing.JLabel appLicenseLabel;
      private javax.swing.JLabel appNameLabel;
      private javax.swing.JLabel appVersionLabel;
      private javax.swing.JLabel appWebsiteLabel;
      private javax.swing.JLabel copyrightLabel;
      @Action("okButton")
      private javax.swing.JButton okButton;
      private javax.swing.JLabel splashImageLabel;
      private javax.swing.JLabel staticLabel1;
      private javax.swing.JLabel staticLabel2;
      private javax.swing.JLabel staticLabel3;
      // End of variables declaration//GEN-END:variables

	public static interface IAboutDialogListener {
		public void websiteUrlClicked(AboutDialogEvent event);
	}
	
	public static class AboutDialogEvent {
		private final String url;
		
		public AboutDialogEvent(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return this.url;
		}
	}
	
	class UrlMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			final AboutDialogEvent event = new AboutDialogEvent(model.getApplicationWebsite());
			for(IAboutDialogListener listener: listeners) {
				listener.websiteUrlClicked(event);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			Map attr = appWebsiteLabel.getFont().getAttributes();
			attr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			appWebsiteLabel.setFont(appWebsiteLabel.getFont().deriveFont(attr));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Map attr = appWebsiteLabel.getFont().getAttributes();
			attr.put(TextAttribute.UNDERLINE, -1);
			appWebsiteLabel.setFont(appWebsiteLabel.getFont().deriveFont(attr));
		}
	}
}
