/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * Visitons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Visitons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Visitons. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.projectwizard;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;

public class OpenTransProjectPanelVisual extends JPanel implements DocumentListener
{
	public static final String PROP_PROJECT_NAME = "projectName";
	private OpenTransProjectWizardPanel panel;

	public OpenTransProjectPanelVisual(OpenTransProjectWizardPanel panel)
	{
		initComponents();
		this.panel = panel;
		// Register listener on the textFields to make the automatic updates
		this.projectNameTextField.getDocument().addDocumentListener(this);
		this.projectLocationTextField.getDocument().addDocumentListener(this);
	} // end OpenTransProjectPanelVisual();

	public String getProjectName()
	{
		return this.projectNameTextField.getText();
	} // end getProjectName();

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        authorTextField = new javax.swing.JTextField();
        websiteLabel = new javax.swing.JLabel();
        websiteTextField = new javax.swing.JTextField();
        notesLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notesArea = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.projectNameLabel.text")); // NOI18N

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.projectLocationLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.browseButton.text")); // NOI18N
        browseButton.setActionCommand(org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.browseButton.actionCommand")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.createdFolderLabel.text")); // NOI18N

        createdFolderTextField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(authorLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.authorLabel.text")); // NOI18N

        authorTextField.setText(org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.authorTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(websiteLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.websiteLabel.text")); // NOI18N

        websiteTextField.setText(org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.websiteTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(notesLabel, org.openide.util.NbBundle.getMessage(OpenTransProjectPanelVisual.class, "OpenTransProjectPanelVisual.notesLabel.text")); // NOI18N

        notesArea.setColumns(20);
        notesArea.setRows(5);
        jScrollPane2.setViewportView(notesArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectLocationLabel)
                            .addComponent(createdFolderLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectLocationTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(createdFolderTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectNameLabel)
                            .addComponent(authorLabel)
                            .addComponent(websiteLabel)
                            .addComponent(notesLabel))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(websiteTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(authorTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                    .addComponent(projectNameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                                .addGap(120, 120, 120))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectNameLabel)
                    .addComponent(projectNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(websiteLabel)
                    .addComponent(websiteTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(notesLabel)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLocationLabel)
                    .addComponent(projectLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createdFolderLabel)
                    .addComponent(createdFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
	    String command = evt.getActionCommand();
	    if("BROWSE".equals(command))
	    {
		    JFileChooser chooser = new JFileChooser();
		    FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
		    chooser.setDialogTitle("Select Project Location");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    String path = this.projectLocationTextField.getText();
		    if(path.length() > 0)
		    {
			    File f = new File(path);
			    if(f.exists())
			    {
				    chooser.setSelectedFile(f);
			    }
		    }
		    if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this))
		    {
			    File projectDir = chooser.getSelectedFile();
			    this.projectLocationTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
		    }
		    this.panel.fireChangeEvent();
	    }

    }//GEN-LAST:event_browseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorLabel;
    private javax.swing.JTextField authorTextField;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea notesArea;
    private javax.swing.JLabel notesLabel;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JLabel websiteLabel;
    private javax.swing.JTextField websiteTextField;
    // End of variables declaration//GEN-END:variables

	@Override
	public void addNotify()
	{
		super.addNotify();
		//same problem as in 31086, initial focus on Cancel button
		this.projectNameTextField.requestFocus();
	} // end addNotify();

	boolean valid(WizardDescriptor wizardDescriptor)
	{

		if(this.projectNameTextField.getText().length() == 0)
		{
			// TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_ERROR_MESSAGE:
			wizardDescriptor.putProperty("WizardPanel_errorMessage",
				"Project Name is not a valid folder name.");
			return false; // Display name not specified
		}
		File f = FileUtil.normalizeFile(new File(this.projectLocationTextField.getText()).getAbsoluteFile());
		if(!f.isDirectory())
		{
			String message = "Project Folder is not a valid path.";
			wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
			return false;
		}
		final File destFolder = FileUtil.normalizeFile(new File(this.createdFolderTextField.getText()).getAbsoluteFile());

		File projLoc = destFolder;
		while(projLoc != null && !projLoc.exists())
		{
			projLoc = projLoc.getParentFile();
		}
		if(projLoc == null || !projLoc.canWrite())
		{
			wizardDescriptor.putProperty("WizardPanel_errorMessage",
				"Project Folder cannot be created.");
			return false;
		}

		if(FileUtil.toFileObject(projLoc) == null)
		{
			String message = "Project Folder is not a valid path.";
			wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
			return false;
		}

		File[] kids = destFolder.listFiles();
		if(destFolder.exists() && kids != null && kids.length > 0)
		{
			// Folder exists and is not empty
			wizardDescriptor.putProperty("WizardPanel_errorMessage",
				"Project Folder already exists and is not empty.");
			return false;
		}
		wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
		return true;
	} // end valid();

	void store(WizardDescriptor d)
	{
		String name = this.projectNameTextField.getText().trim();
		String folder = this.createdFolderTextField.getText().trim();

		d.putProperty("projdir", new File(folder));
		d.putProperty("name", name);
	} // end store();

	void read(WizardDescriptor settings)
	{
		File projectLocation = (File) settings.getProperty("projdir");
		if(projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory())
		{
			projectLocation = ProjectChooser.getProjectsFolder();
		}
		else
		{
			projectLocation = projectLocation.getParentFile();
		}
		this.projectLocationTextField.setText(projectLocation.getAbsolutePath());

		String projectName = (String) settings.getProperty("name");
		if(projectName == null)
		{
			projectName = "OpenTransProject";
		}
		this.projectNameTextField.setText(projectName);
		this.projectNameTextField.selectAll();
	} // end read();

	void validate(WizardDescriptor d) throws WizardValidationException
	{
		// nothing to validate
	} // end validate();

	// Implementation of DocumentListener --------------------------------------
	public void changedUpdate(DocumentEvent e)
	{
		this.updateTexts(e);
		if(this.projectNameTextField.getDocument() == e.getDocument())
		{
			this.firePropertyChange(OpenTransProjectPanelVisual.PROP_PROJECT_NAME, null, this.projectNameTextField.getText());
		}
	} // end changedUpdate();

	public void insertUpdate(DocumentEvent e)
	{
		this.updateTexts(e);
		if(this.projectNameTextField.getDocument() == e.getDocument())
		{
			this.firePropertyChange(OpenTransProjectPanelVisual.PROP_PROJECT_NAME, null, this.projectNameTextField.getText());
		}
	} // end insertUpdate();

	public void removeUpdate(DocumentEvent e)
	{
		this.updateTexts(e);
		if(this.projectNameTextField.getDocument() == e.getDocument())
		{
			this.firePropertyChange(OpenTransProjectPanelVisual.PROP_PROJECT_NAME, null, this.projectNameTextField.getText());
		}
	} // end removeUpdate();

	/** Handles changes in the Project name and project directory, */
	private void updateTexts(DocumentEvent e)
	{

		Document doc = e.getDocument();

		if(doc == this.projectNameTextField.getDocument() || doc == this.projectLocationTextField.getDocument())
		{
			// Change in the project name

			String projectName = this.projectNameTextField.getText();
			String projectFolder = this.projectLocationTextField.getText();

			//if (projectFolder.trim().length() == 0 || projectFolder.equals(oldName)) {
			this.createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
			//}

		}
		this.panel.fireChangeEvent(); // Notify that the panel changed
	} // end updateTexts();
} // end OpenTransProjectPanelVisual;
