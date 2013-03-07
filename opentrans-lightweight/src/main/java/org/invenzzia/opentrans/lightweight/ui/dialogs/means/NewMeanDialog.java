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
package org.invenzzia.opentrans.lightweight.ui.dialogs.means;

/**
 * The dialog is used for creating new means of transport.
 * 
 * @author Tomasz Jędrzejewski
 */
public class NewMeanDialog extends javax.swing.JDialog {
	/**
	 * Whether the choice has been confirmed?
	 */
	private boolean confirmed = false;

	/**
	 * Creates new form NewMeanDialog
	 */
	public NewMeanDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}
	
	/**
	 * Returns the entered mean of transport name.
	 * 
	 * @return Mean of transport name.
	 */
	public String getEnteredName() {
		return this.nameField.getText();
	}
	
	/**
	 * Returns 'true', if the user confirmed the action by clicking 'OK'.
	 * 
	 * @return True, if the user clicked 'OK'
	 */
	public boolean isConfirmed() {
		return this.confirmed;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
      // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
      private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            nameField = new javax.swing.JTextField();
            cancelButton = new javax.swing.JButton();
            okButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jLabel1.setText("Enter the name of the mean of transport:");

            nameField.setNextFocusableComponent(okButton);

            cancelButton.setText("Cancel");
            cancelButton.setNextFocusableComponent(nameField);
            cancelButton.setPreferredSize(new java.awt.Dimension(100, 25));
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                  public void actionPerformed(java.awt.event.ActionEvent evt) {
                        cancelButtonActionPerformed(evt);
                  }
            });

            okButton.setText("OK");
            okButton.setNextFocusableComponent(cancelButton);
            okButton.setPreferredSize(new java.awt.Dimension(100, 25));
            okButton.addActionListener(new java.awt.event.ActionListener() {
                  public void actionPerformed(java.awt.event.ActionEvent evt) {
                        okButtonActionPerformed(evt);
                  }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                              .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(0, 56, Short.MAX_VALUE))
                              .addComponent(nameField)
                              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
            );
            layout.setVerticalGroup(
                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                              .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                              .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
            );

            pack();
      }// </editor-fold>//GEN-END:initComponents

      private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		this.setVisible(false);
		this.confirmed = true;
      }//GEN-LAST:event_okButtonActionPerformed

      private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		this.setVisible(false);
		this.confirmed = false;
      }//GEN-LAST:event_cancelButtonActionPerformed

      // Variables declaration - do not modify//GEN-BEGIN:variables
      private javax.swing.JButton cancelButton;
      private javax.swing.JLabel jLabel1;
      private javax.swing.JTextField nameField;
      private javax.swing.JButton okButton;
      // End of variables declaration//GEN-END:variables
}