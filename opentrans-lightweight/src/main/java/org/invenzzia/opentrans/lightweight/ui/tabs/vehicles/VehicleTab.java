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
package org.invenzzia.opentrans.lightweight.ui.tabs.vehicles;

import com.google.common.base.Preconditions;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.invenzzia.opentrans.lightweight.annotations.Action;
import org.invenzzia.opentrans.lightweight.model.IBatchModelListener;
import org.invenzzia.opentrans.lightweight.model.VisitonsSelectionModel;
import org.invenzzia.opentrans.visitons.data.MeanOfTransport.MeanOfTransportRecord;
import org.invenzzia.opentrans.visitons.data.Vehicle.VehicleRecord;

/**
 * The tab for managing the vehicles.
 * 
 * @author Tomasz Jędrzejewski
 */
public class VehicleTab extends javax.swing.JPanel implements IBatchModelListener<VehicleTabModel> {
	/**
	 * Infrastructure listener set.
	 */
	private Set<IVehicleTabListener> listeners;
	/**
	 * Currently selected record.
	 */
	private VehicleRecord selectedRecord;
	/**
	 * The model of vehicle records.
	 */
	private VehicleTableModel model;

	/**
	 * Creates new form VehicleTab
	 */
	public VehicleTab() {
		this.listeners = new LinkedHashSet<>();
		this.initComponents();
	}
	
	/**
	 * Adds a new vehicle tab event listener.
	 *
	 * @param listener
	 */
	public void addVehicleTabListener(IVehicleTabListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Removes the existing vehicle tab event listener.
	 *
	 * @param listener
	 */
	public void removeVehicleTabListener(IVehicleTabListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Clears all the vehicle tab event listeners.
	 */
	public void removeVehicleTabListeners() {
		this.listeners.clear();
	}
	
	/**
	 * Installs the new table model for the vehicle table.
	 * 
	 * @param model The new model.
	 */
	public void setVehicleTableModel(VehicleTableModel model) {
		this.vehicleTable.setModel(this.model = Preconditions.checkNotNull(model));
	}
	
	/**
	 * Sets the model for the means of transport.
	 * 
	 * @param model Mean of transport model.
	 */
	public void setMeanOfTransportModel(VisitonsSelectionModel model) {
		this.meanOfTransportSelector.setModel(model);
	}

	/**
	 * Automatic notification about new model data.
	 * 
	 * @param model 
	 */
	@Override
	public void modelDataAvailable(VehicleTabModel model) {
		this.model.updateModel(model.getVehicles(), model.getVehicleTypes());
	}

	/**
	 * Sets the new selected record.
	 * 
	 * @param record The new selected record.
	 */
	public void setSelectedRecord(VehicleRecord record) {
		this.selectedRecord = record;
		if(null != record) {
			this.editVehicleButton.setEnabled(true);
			this.removeVehicleButton.setEnabled(true);
			this.locateVehicleButton.setEnabled(true);
		} else {
			this.editVehicleButton.setEnabled(false);
			this.removeVehicleButton.setEnabled(false);
			this.locateVehicleButton.setEnabled(false);
		}
	}

	/**
	 * Returns the currently selected record or <strong>null</strong>.
	 * 
	 * @return Currently selected record.
	 */
	public VehicleRecord getSelectedRecord() {
		return this.selectedRecord;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        meanOfTransportSelector = new javax.swing.JComboBox();
        meanOfTransportManageButton = new javax.swing.JButton();
        vehicleTypeManageButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        addVehicleButton = new javax.swing.JButton();
        editVehicleButton = new javax.swing.JButton();
        removeVehicleButton = new javax.swing.JButton();
        locateVehicleButton = new javax.swing.JButton();
        vehicleTableScroller = new javax.swing.JScrollPane();
        vehicleTable = new javax.swing.JTable();
        VehicleSelectionListener lst = new VehicleSelectionListener();
        vehicleTable.getSelectionModel().addListSelectionListener(lst);
        vehicleTable.addMouseListener(lst);

        jLabel1.setText("Mean of transport:");

        meanOfTransportSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        meanOfTransportManageButton.setText("Manage");

        vehicleTypeManageButton.setText("Vehicle types");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vehicle directory"));

        addVehicleButton.setText("Add");
        addVehicleButton.setPreferredSize(new java.awt.Dimension(100, 25));

        editVehicleButton.setText("Edit");
        editVehicleButton.setPreferredSize(new java.awt.Dimension(100, 25));

        removeVehicleButton.setText("Remove");
        removeVehicleButton.setPreferredSize(new java.awt.Dimension(100, 25));

        locateVehicleButton.setText("Locate");
        locateVehicleButton.setPreferredSize(new java.awt.Dimension(100, 25));

        vehicleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        vehicleTableScroller.setViewportView(vehicleTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vehicleTableScroller)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(addVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(locateVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(vehicleTableScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locateVehicleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meanOfTransportSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(meanOfTransportManageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                        .addComponent(vehicleTypeManageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(meanOfTransportSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(meanOfTransportManageButton)
                    .addComponent(vehicleTypeManageButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    @Action("addAction")
    private javax.swing.JButton addVehicleButton;
    @Action("editAction")
    private javax.swing.JButton editVehicleButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    @Action("locateAction")
    private javax.swing.JButton locateVehicleButton;
    @Action("manageMeans")
    private javax.swing.JButton meanOfTransportManageButton;
    private javax.swing.JComboBox meanOfTransportSelector;
    @Action("removeAction")
    private javax.swing.JButton removeVehicleButton;
    private javax.swing.JTable vehicleTable;
    private javax.swing.JScrollPane vehicleTableScroller;
    @Action("manageVehicleTypes")
    private javax.swing.JButton vehicleTypeManageButton;
    // End of variables declaration//GEN-END:variables

	/**
	 * Handles selecting the row in a table and a double-click on the row.
	 */
	class VehicleSelectionListener extends MouseAdapter implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent event) {
			int row = vehicleTable.getSelectedRow();
			if(-1 == row) {
				setSelectedRecord(null);
			} else {
				setSelectedRecord(model.getRecord(row));
			}
			final VehicleTabEvent itEvent = new VehicleTabEvent(selectedRecord);
			for(IVehicleTabListener listener : listeners) {
				listener.vehicleSelected(itEvent);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				JTable target = (JTable) e.getSource();
				
				int row = target.getSelectedRow();
				if(0 <= row) {
					setSelectedRecord(model.getRecord(row));
					final VehicleTabEvent itEvent = new VehicleTabEvent(selectedRecord);
					for(IVehicleTabListener listener : listeners) {
						listener.vehicleSelected(itEvent);
						listener.editRequested(itEvent);
					}
				}
			}
		}
	}

	/**
	 * The event used in the notifications about the state change.
	 */
	public static class VehicleTabEvent {
		private final VehicleRecord record;

		public VehicleTabEvent() {
			this.record = null;
		}

		public VehicleTabEvent(VehicleRecord record) {
			this.record = record;
		}

		/**
		 * Returns the record affected by the event.
		 * 
		 * @return Record affected by the event.
		 */
		public VehicleRecord getRecord() {
			return this.record;
		}
	}
	
	/**
	 * Allows writing notifications about GUI state changes.
	 */
	public static interface IVehicleTabListener {
		/**
		 * Notifies that a vehicle has been selected.
		 * @param event 
		 */
		public void vehicleSelected(VehicleTabEvent event);
		/**
		 * Vehicle edit has been requested by double clicking on the record.
		 * 
		 * @param event 
		 */
		public void editRequested(VehicleTabEvent event);
	}
}
