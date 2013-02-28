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

package org.invenzzia.opentrans.lightweight.ui.dialogs.resize;

import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.invenzzia.opentrans.lightweight.ui.component.Minimap;

/**
 * This dialog window shows a minimap and buttons around it that
 * allows extending and shrinking the world in the specified
 * direction.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ResizeDialog extends JDialog {
	private JRadioButton extendButton;
	private JRadioButton shrinkButton;
	private ButtonGroup buttonGroup;
	private JLabel resizeLabel;
	private JButton northButton;
	private JButton southButton;
	private JButton westButton;
	private JButton eastButton;
	private JButton okButton;
	private Minimap minimap;
	/**
	 * Do we currently extend the screen?
	 */
	private boolean extend = true;
	/**
	 * World size X
	 */
	private int worldSizeX = 1;
	/**
	 * World size Y
	 */
	private int worldSizeY = 1;
	/**
	 * Listeners that respond to the events emitted by the dialog.
	 */
	private Set<IResizeListener> listeners;
	
	public ResizeDialog(Frame parent, boolean modal) {
		super(parent, modal);
		
		this.listeners = new LinkedHashSet<>();
		this.setTitle("Resize world");
		
		this.extendButton = new JRadioButton("Extend");
		this.shrinkButton = new JRadioButton("Shrink");
		this.resizeLabel = new JLabel(this.createDimension());
		this.northButton = new JButton("↑");
		this.northButton.setActionCommand("north");
		this.southButton = new JButton("↓");
		this.southButton.setActionCommand("south");
		this.westButton = new JButton("←");
		this.westButton.setActionCommand("west");
		this.eastButton = new JButton("→");
		this.eastButton.setActionCommand("east");
		this.minimap = new Minimap();
		this.okButton = new JButton("OK");
		this.okButton.setSize(90, 30);
		this.okButton.setMaximumSize(new Dimension(90, 30));
		this.buttonGroup = new ButtonGroup();
		this.buttonGroup.add(this.extendButton);
		this.buttonGroup.add(this.shrinkButton);
		
		this.setSize(new Dimension(600, 500));
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
		upperPanel.add(this.extendButton);
		upperPanel.add(this.shrinkButton);
		upperPanel.add(Box.createHorizontalGlue());
		upperPanel.add(this.resizeLabel);
		upperPanel.setSize(600, 40);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		middlePanel.add(this.northButton, c);
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		middlePanel.add(this.southButton, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		middlePanel.add(this.westButton, c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		middlePanel.add(this.eastButton, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		middlePanel.add(this.minimap, c);
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
		lowerPanel.add(this.okButton);
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().add(upperPanel);
		this.getContentPane().add(middlePanel);
		this.getContentPane().add(lowerPanel);
		
		this.initLogic();
	}
	
	/**
	 * Initializes the internal logic of the dialog.
	 */
	private void initLogic() {
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		this.extendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				extend = true;
				northButton.setText("↑");
				southButton.setText("↓");
				westButton.setText("←");
				eastButton.setText("→");
				updateButtonStatus();
			}
		});
		this.shrinkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				extend = true;
				northButton.setText("↓");
				southButton.setText("↑");
				westButton.setText("→");
				eastButton.setText("←");
				updateButtonStatus();
			}
		});
		ActionListener resizing = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WorldResizeEvent evt = null;
				switch(e.getActionCommand()) {
					case "north":
						evt = new WorldResizeEvent(WorldResizeEvent.RESIZE_NORTH, extend);
						break;
					case "south":
						evt = new WorldResizeEvent(WorldResizeEvent.RESIZE_SOUTH, extend);
						break;
					case "west":
						evt = new WorldResizeEvent(WorldResizeEvent.RESIZE_WEST, extend);
						break;
					case "east":
						evt = new WorldResizeEvent(WorldResizeEvent.RESIZE_EAST, extend);
						break;
				}
				Preconditions.checkNotNull(evt, "The event cannot be null now.");
				for(IResizeListener listener: listeners) {
					listener.worldResized(evt);
				}
			}
		};
		this.northButton.addActionListener(resizing);
		this.southButton.addActionListener(resizing);
		this.westButton.addActionListener(resizing);
		this.eastButton.addActionListener(resizing);
	}
	
	private String createDimension() {
		return this.worldSizeX + " x "+this.worldSizeY;
	}
	
	/**
	 * Registers a new listener for world size change events.
	 * 
	 * @param listener 
	 */
	public void addResizeListener(IResizeListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes the existing world size change listener.
	 * 
	 * @param listener 
	 */
	public void removeResizeListener(IResizeListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Removes all world size change listeners.
	 */
	public void removeResizeListeners() {
		this.listeners.clear();
	}
	
	
	public void setWorldSize(int x, int y) {
		this.worldSizeX = x;
		this.worldSizeY = y;
		this.resizeLabel.setText(this.createDimension());
		this.resizeLabel.repaint();
	}
	
	public int getWorldSizeX() {
		return this.worldSizeX;
	}
	
	public int getWorldSizeY() {
		return this.worldSizeY;
	}
	
	/**
	 * Injects the new data for the minimap.
	 * 
	 * @param data 
	 */
	public void setMinimapData(boolean data[][]) {
		this.minimap.setData(data);
		this.updateButtonStatus();
		this.minimap.repaint();
	}
	
	/**
	 * Updates the 'enabled' status of all the resizing buttons.
	 */
	public void updateButtonStatus() {
		boolean northButtonStatus = true;
		boolean southButtonStatus = true;
		boolean westButtonStatus = true;
		boolean eastButtonStatus = true;
		
		boolean data[][] = this.minimap.getData();
		int sizeX = data.length - 1;
		int sizeY = data[0].length - 1;
		
		if(!this.extend) {
			for(int i = 0; i < data.length; i++) {
				if(data[i][0] == true) {
					northButtonStatus = false;
				}
				if(data[i][sizeY] == true) {
					southButtonStatus = false;
				}
			}
			for(int j = 0; j < sizeY; j++) {
				if(data[0][j] == true) {
					westButtonStatus = false;
				}
				if(data[sizeX][j] == true) {
					eastButtonStatus = false;
				}
			}
		}
		this.northButton.setEnabled(northButtonStatus);
		this.southButton.setEnabled(southButtonStatus);
		this.westButton.setEnabled(westButtonStatus);
		this.eastButton.setEnabled(eastButtonStatus);
	}
	
	/**
	 * Allows receiving notifications about clicking "north"/"west"/"east"/"south"
	 * buttons that shall spawn world resizing.
	 */
	public static interface IResizeListener {
		/**
		 * Notifies that the user requested changing the size of the world.
		 * 
		 * @param event 
		 */
		public void worldResized(WorldResizeEvent event);
	}
	
	/**
	 * Notification about the request to change the size of the world.
	 */
	public static class WorldResizeEvent {
		public static final int RESIZE_NORTH = 0;
		public static final int RESIZE_EAST = 1;
		public static final int RESIZE_SOUTH = 2;
		public static final int RESIZE_WEST = 3;
		
		private final int direction;
		private final boolean extend;
		
		public WorldResizeEvent(int direction, boolean extend) {
			this.direction = direction;
			this.extend = extend;
		}
		
		/**
		 * Direction, where the world should be resized.
		 * 
		 * @return Resizing direction.
		 */
		public int getDirection() {
			return this.direction;
		}
		
		/**
		 * Should we extend (<strong>true</strong>) or shrink the world (<strong>false</strong>)?
		 * 
		 * @return 
		 */
		public boolean isExtend() {
			return this.extend;
		}
	}
}
