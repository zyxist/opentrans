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
import javax.swing.JMenuItem;
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
	 * Sets the new status message.
	 * 
	 * @param message 
	 */
	public void setStatusMessage(String message) {
		this.statusBox.setText(message);
	}
	
	/**
	 * Clears both of the location boxes.
	 */
	public void clearLocationInfo() {
		this.firstLocationBox.setText(" ");
		this.secondLocationBox.setText(" ");
	}
	
	/**
	 * Shows the location info on the status bar. This is primarily used for displaying the
	 * mouse cursor position on the world map.
	 * 
	 * @param x
	 * @param y 
	 */
	public void setLocationInfo(int x, int y) {
		this.firstLocationBox.setText("X: "+x);
		this.secondLocationBox.setText("Y: "+y);
	}
	
	/**
	 * Adds a new tab selection item to the main menu.
	 * 
	 * @param item Tab selection item to add.
	 */
	public void addTabSelectionItem(JMenuItem item) {
		this.tabSelectionSubmenu.add(item);
	}
	
	/**
	 * Adds a new toolbar selection item to the main menu.
	 * 
	 * @param item Toolbar selection item.
	 */
	public void addToolbarSelectionItem(JMenuItem item) {
		this.toolbarSubmenu.add(item);
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
        statusBox = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        firstLocationBox = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        secondLocationBox = new javax.swing.JLabel();
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
        routesMenuItem = new javax.swing.JMenuItem();
        simulationMenu = new javax.swing.JMenu();
        toolsMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        tabSelectionSubmenu = new javax.swing.JMenu();
        toolbarSubmenu = new javax.swing.JMenu();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        closeAllTabsItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("OpenTrans");

        toolbarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolbarPanel.setPreferredSize(new java.awt.Dimension(110, 50));
        toolbarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        projectToolbar1.setRollover(true);
        toolbarPanel.add(projectToolbar1);

        workspacePanel.setLayout(new javax.swing.BoxLayout(workspacePanel, javax.swing.BoxLayout.LINE_AXIS));

        statusPanel.setFloatable(false);
        statusPanel.setRollover(true);

        statusBox.setText(" ");
        statusPanel.add(statusBox);
        statusPanel.add(filler1);

        firstLocationBox.setText(" ");
        firstLocationBox.setMaximumSize(new java.awt.Dimension(70, 15));
        firstLocationBox.setMinimumSize(new java.awt.Dimension(70, 15));
        firstLocationBox.setPreferredSize(new java.awt.Dimension(70, 15));
        statusPanel.add(firstLocationBox);
        statusPanel.add(filler2);

        secondLocationBox.setText(" ");
        secondLocationBox.setMaximumSize(new java.awt.Dimension(70, 15));
        secondLocationBox.setMinimumSize(new java.awt.Dimension(70, 15));
        secondLocationBox.setPreferredSize(new java.awt.Dimension(70, 15));
        statusPanel.add(secondLocationBox);

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

        resizeWorldMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        resizeWorldMenuItem.setText("Resize world...");
        projectMenu.add(resizeWorldMenuItem);
        projectMenu.add(jSeparator2);

        meanOfTransportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        meanOfTransportMenuItem.setText("Means of transport");
        projectMenu.add(meanOfTransportMenuItem);

        vehicleTypeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        vehicleTypeMenuItem.setText("Vehicle types");
        projectMenu.add(vehicleTypeMenuItem);

        routesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        routesMenuItem.setText("Routes");
        routesMenuItem.setActionCommand("");
        projectMenu.add(routesMenuItem);

        jMenuBar1.add(projectMenu);

        simulationMenu.setText("Simulation");
        jMenuBar1.add(simulationMenu);

        toolsMenu.setText("Tools");
        jMenuBar1.add(toolsMenu);

        windowMenu.setText("Window");

        tabSelectionSubmenu.setText("Project tabs");
        windowMenu.add(tabSelectionSubmenu);

        toolbarSubmenu.setText("Toolbars");
        windowMenu.add(toolbarSubmenu);
        windowMenu.add(jSeparator3);

        closeAllTabsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        closeAllTabsItem.setText("Close all tabs");
        windowMenu.add(closeAllTabsItem);

        jMenuBar1.add(windowMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        aboutMenuItem.setText("About...");
        helpMenu.add(aboutMenuItem);

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
    @Action("about")
    private javax.swing.JMenuItem aboutMenuItem;
    @Action("closeAllTabs")
    private javax.swing.JMenuItem closeAllTabsItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel firstLocationBox;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
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
    @Action("routes")
    private javax.swing.JMenuItem routesMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveProjectMenuItem;
    private javax.swing.JLabel secondLocationBox;
    private javax.swing.JMenu simulationMenu;
    private javax.swing.JLabel statusBox;
    private javax.swing.JToolBar statusPanel;
    private javax.swing.JMenu tabSelectionSubmenu;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JMenu toolbarSubmenu;
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
