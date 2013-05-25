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
package org.invenzzia.opentrans.lightweight.ui.dialogs.lines;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.annotations.FormField;
import org.invenzzia.opentrans.lightweight.controllers.IFormErrorView;
import org.invenzzia.opentrans.visitons.data.Line.LineRecord;

/**
 * Dialog window for batch managing of transportation lines.
 * @author Tomasz Jędrzejewski
 */
public class LineDialog extends javax.swing.JDialog implements IFormErrorView {
	/**
	 * Currently selected line.
	 */
	private LineRecord selectedRecord;
	/**
	 * Listeners for the selection events.
	 */
	private Set<IItemListener> listeners = new LinkedHashSet<>();
	
	/**
	 * Creates new form LineDialog
	 */
	public LineDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}
	
	/**
	 * Adds a new listener of item events.
	 * 
	 * @param listener 
	 */
	public void addItemListener(IItemListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes an existing listener of item events.
	 * 
	 * @param listener 
	 */
	public void removeItemListener(IItemListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Clears all item event listeners.
	 */
	public void removeItemListeners() {
		this.listeners.clear();
	}

	/**
	 * Sets the management model for the item list.
	 * 
	 * @param model 
	 */
	public void setModel(ListModel<LineRecord> model) {
		this.itemList.setModel(model);
	}
	
	/**
	 * Returns the current management model for the item list.
	 * 
	 * @return Current management model.
	 */
	public ListModel<LineRecord> getModel() {
		return this.itemList.getModel();
	}
	
	/**
	 * Returns the currently selected item or NULL, if no item is selected.
	 * 
	 * @return Selected item.
	 */
	public LineRecord getSelectedRecord() {
		return this.selectedRecord;
	}
	
	/**
	 * Disables the content of the form - no item is selected.
	 */
	public void disableForm() {
		this.numberField.setEnabled(false);
		this.descriptionField.setEnabled(false);
	}
	
	/**
	 * Enables the content of the form for editing.
	 */
	public void enableForm() {
		this.numberField.setEnabled(true);
		this.descriptionField.setEnabled(true);
	}
	
	/**
	 * Sets the new error message to display.
	 * 
	 * @param message New error message.
	 */
	@Override
	public void setErrorMessage(String message) {
		this.errorLabel.setText(message);
	}
	
	/**
	 * Returns the currently displayed error message.
	 * 
	 * @return 
	 */
	@Override
	public String getErrorMessage() {
		return this.errorLabel.getText();
	}
	
	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        numberField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        errorLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manage lines");

        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 139));

        itemList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        itemList.setPreferredSize(new java.awt.Dimension(194, 85));
        itemList.addListSelectionListener(new ItemSelectionListener());
        jScrollPane1.setViewportView(itemList);

        addButton.setText("Add");

        removeButton.setText("Remove");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Properties"));

        jLabel1.setText("Number:");

        jLabel2.setText("Description:");

        descriptionField.setColumns(20);
        descriptionField.setRows(5);
        jScrollPane2.setViewportView(descriptionField);

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(numberField)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(errorLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel)
                .addContainerGap())
        );

        cancelButton.setText("Cancel");

        okButton.setText("OK");

        helpButton.setText("Help");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 128, Short.MAX_VALUE)
                        .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(removeButton)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(helpButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    @Action("addAction")
    private javax.swing.JButton addButton;
    @Action("cancelAction")
    private javax.swing.JButton cancelButton;
    @FormField(name="description")
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JLabel errorLabel;
    @Action("helpAction")
    private javax.swing.JButton helpButton;
    private javax.swing.JList itemList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    @FormField(name="number")
    private javax.swing.JTextField numberField;
    @Action("okAction")
    private javax.swing.JButton okButton;
    @Action("removeAction")
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

	class ItemSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			LineRecord record = (LineRecord) itemList.getSelectedValue();
			selectedRecord = record;
			
			final ItemEvent event = new ItemEvent(record);
			for(IItemListener listener: listeners) {
				listener.onItemSelected(event);
			}
		}
	}
	
	public static class ItemEvent {
		private final LineRecord record;
		
		public ItemEvent(LineRecord record) {
			this.record = record;
		}
		
		public LineRecord getRecord() {
			return this.record;
		}
		
		public boolean hasRecord() {
			return null != this.record;
		}
	}

	public static interface IItemListener {
		public void onItemSelected(ItemEvent event);
	}
}
