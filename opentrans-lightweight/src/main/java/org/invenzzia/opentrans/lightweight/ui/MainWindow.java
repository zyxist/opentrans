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

import com.google.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.invenzzia.opentrans.lightweight.annotations.Action;

/**
 * Represents the main application window, with a menu, toolbar, workspace
 * and the status bar.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class MainWindow extends JFrame {
	/**
	 * Creates new form MainWindow
	 */
	public MainWindow() {
		this.initComponents();
	}

	/**
	 * Returns the panel used for displaying the toolbars.
	 * 
	 * @return Toolbar panel. 
	 */
	public JPanel getToolbarPanel() {
		return this.toolbarPanel;
	}
	
	/**
	 * Returns the workspace panel.
	 * 
	 * @return Workspace panel.
	 */
	public JPanel getWorkspacePanel() {
		return this.workspacePanel;
	}
	
	/**
	 * Allows enabling or disabling the 'undo' button.
	 * 
	 * @param enabled 
	 */
	public void setUndoEnabled(boolean enabled) {
		this.undoMenuItem.setEnabled(enabled);
	}
	
	/**
	 * Allows enabling or disabling the 'redo' button.
	 * 
	 * @param enabled 
	 */
	public void setRedoEnabled(boolean enabled) {
		this.redoMenuItem.setEnabled(enabled);
	}
	
	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
	 * method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbarPanel = new javax.swing.JPanel();
        projectToolbar1 = new org.invenzzia.opentrans.lightweight.ui.toolbars.ProjectToolbar();
        workspacePanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        openProjectMenuItem = new javax.swing.JMenuItem();
        saveProjectMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        projectMenu = new javax.swing.JMenu();
        resizeWorldMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        meanOfTransportMenuItem = new javax.swing.JMenuItem();
        vehicleTypeMenuItem = new javax.swing.JMenuItem();
        simulationMenu = new javax.swing.JMenu();
        toolsMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OpenTrans");

        toolbarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolbarPanel.setPreferredSize(new java.awt.Dimension(110, 50));
        toolbarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        projectToolbar1.setRollover(true);
        toolbarPanel.add(projectToolbar1);

        workspacePanel.setLayout(new javax.swing.BoxLayout(workspacePanel, javax.swing.BoxLayout.LINE_AXIS));

        statusPanel.setFloatable(false);
        statusPanel.setRollover(true);

        fileMenu.setText("File");

        newProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newProjectMenuItem.setText("New project");
        fileMenu.add(newProjectMenuItem);

        openProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openProjectMenuItem.setText("Open project");
        fileMenu.add(openProjectMenuItem);

        saveProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveProjectMenuItem.setText("Save project");
        fileMenu.add(saveProjectMenuItem);

        saveAsMenuItem.setText("Save as...");
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator1);

        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        quitMenuItem.setText("Quit");
        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setText("Undo");
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setText("Redo");
        editMenu.add(redoMenuItem);

        jMenuBar1.add(editMenu);

        viewMenu.setText("View");
        jMenuBar1.add(viewMenu);

        projectMenu.setText("Project");

        resizeWorldMenuItem.setText("Resize world...");
        projectMenu.add(resizeWorldMenuItem);
        projectMenu.add(jSeparator2);

        meanOfTransportMenuItem.setText("Means of transport");
        projectMenu.add(meanOfTransportMenuItem);

        vehicleTypeMenuItem.setText("Vehicle types");
        projectMenu.add(vehicleTypeMenuItem);

        jMenuBar1.add(projectMenu);

        simulationMenu.setText("Simulation");
        jMenuBar1.add(simulationMenu);

        toolsMenu.setText("Tools");
        jMenuBar1.add(toolsMenu);

        windowMenu.setText("Window");
        jMenuBar1.add(windowMenu);

        helpMenu.setText("Help");
        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
            .addComponent(workspacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(workspacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    @Action("meansOfTransport")
    private javax.swing.JMenuItem meanOfTransportMenuItem;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JMenuItem openProjectMenuItem;
    private javax.swing.JMenu projectMenu;
    private org.invenzzia.opentrans.lightweight.ui.toolbars.ProjectToolbar projectToolbar1;
    @Action("quit")
    private javax.swing.JMenuItem quitMenuItem;
    @Action("redo")
    private javax.swing.JMenuItem redoMenuItem;
    @Action("resizeWorld")
    private javax.swing.JMenuItem resizeWorldMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveProjectMenuItem;
    private javax.swing.JMenu simulationMenu;
    private javax.swing.JToolBar statusPanel;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JMenu toolsMenu;
    @Action("undo")
    private javax.swing.JMenuItem undoMenuItem;
    @Action("vehicleTypes")
    private javax.swing.JMenuItem vehicleTypeMenuItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JPanel workspacePanel;
    // End of variables declaration//GEN-END:variables
}
