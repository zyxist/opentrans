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

package org.invenzzia.opentrans.lightweight.ui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * The label of the tab used by {@link AdvancedTabPane}. It adds icon
 * and "X" button to the tab label.
 * 
 * @author Tomasz Jędrzejewski
 */
public class TabHeader extends JPanel implements ActionListener {
	/**
	 * The label with the title and icon.
	 */
	private JLabel titleLabel;
	/**
	 * Close button.
	 */
	private JButton closeButton;
	/**
	 * Icon used by the label.
	 */
	private Icon icon;
	/**
	 * Header title.
	 */
	private String title;
	/**
	 * Managing tab pane.
	 */
	private AdvancedTabPane managingPane;
	/**
	 * Managed component, so that we know, which tab gets closed.
	 */
	private Component managedComponent;
	
	/**
	 * This mouse listener paints the border around the button, when we move a mouse
	 * over it.
	 */
	private final static MouseListener buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if(component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if(component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};
	
	public TabHeader() {
		this("Default tab", null);
	}
	
	public TabHeader(String title) {
		this(title, null);
	}
	
	public TabHeader(String title, Icon icon) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.title = title;
		this.icon = icon;
		
		this.setOpaque(false);
		
		this.titleLabel = new JLabel() {
			@Override
			public String getText() {
				return TabHeader.this.title;
			}
			
			@Override
			public Icon getIcon() {
				return TabHeader.this.icon;
			}
		};
		
		this.titleLabel.setOpaque(false);
		this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		this.add(this.titleLabel);
		this.closeButton = new JButton();
		this.closeButton.setPreferredSize(new Dimension(16, 16));
		this.closeButton.setToolTipText("Close this dockable.");
		this.closeButton.setUI(new BasicButtonUI());
		this.closeButton.setContentAreaFilled(false);
		this.closeButton.setFocusable(false);
		this.closeButton.setBorder(BorderFactory.createEtchedBorder());
		this.closeButton.setBorderPainted(false);
		this.closeButton.setRolloverEnabled(true);
		this.closeButton.addMouseListener(buttonMouseListener);
		this.closeButton.addActionListener(this);
		this.add(this.closeButton);
	}

	public AdvancedTabPane getManagingPane() {
		return this.managingPane;
	}

	public void setManagingPane(AdvancedTabPane managingPane) {
		this.managingPane = managingPane;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public Icon getCloseIcon() {
		return this.closeButton.getIcon();
	}
	
	public void setCloseIcon(Icon closeIcon) {
		this.closeButton.setIcon(closeIcon);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(null != this.managingPane) {
			this.managingPane.tabCloseRequest(this.managedComponent);
		}
	}

	public void setManagedComponent(Component content) {
		this.managedComponent = content;
	}
}
