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

import com.google.common.base.Preconditions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class JReportingSlider extends JComponent {
	private static final String uiClassID = "ReportingSliderUI";
	/**
	 * The data model for this component.
	 */
	private ReportingSliderModel model;
	
	public JReportingSlider() {
		super();
		this.model = new DefaultReportingSliderModel();
		this.model.setMinValue(0);
		this.model.setMaxValue(100);
		this.model.setValue(0);
		this.updateUI();
	}
	
	/**
	 * Sets the new UI delegate.
	 * 
	 * @param ui New UI delegate
	 */
	public void setUI(ReportingSliderUI ui) {
		super.setUI(ui);
	}
	
	/**
	 * Resets the UI property to a value from the current look and feel.
	 */
	@Override
	public void updateUI() {
		if(UIManager.get(this.getUIClassID()) != null) {
			this.setUI((ReportingSliderUI) UIManager.getUI(this));
		} else {
			this.setUI(new BasicReportingSliderUI());
		}
	}
	
	/**
	 * Returns the UI which implements the Look and Feel for this component.
	 * 
	 * @return UI look and feel implementation for this component.
	 */
	public ReportingSliderUI getUI() {
		return (ReportingSliderUI) this.ui;
	}
	
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
	
	public ReportingSliderModel getModel() {
		return this.model;
	}
	
	public void setModel(ReportingSliderModel model) {
		this.model = Preconditions.checkNotNull(model, "The model cannot be null.");
	}


	public static interface ReportingSliderModel {
		public boolean getValueIsAdjusting();
		public void setValueIsAdjusting(boolean value);
		public int getValue();
		public void setValue(int value);
		public int getMinValue();
		public void setMinValue(int minValue);
		public int getMaxValue();
		public void setMaxValue(int maxValue);
		public void addChangeListener(ChangeListener listener);
		public void removeChangeListener(ChangeListener listener);
	}
	
	/**
	 * Default model implementation for the component.
	 */
	public static class DefaultReportingSliderModel implements ReportingSliderModel {
		private int value;
		private int minValue;
		private int maxValue;
		private boolean isAdjusting;
		/**
		 * Listeners waiting for model changes.
		 */
		protected EventListenerList listenerList = new EventListenerList();

		@Override
		public boolean getValueIsAdjusting() {
			return this.isAdjusting;
		}

		@Override
		public void setValueIsAdjusting(boolean value) {
			if(value != this.isAdjusting) {
				this.isAdjusting = value;
				this.fireStateChanged();
			}
		}

		@Override
		public int getValue() {
			return this.value;
		}

		@Override
		public void setValue(int value) {
			if(value != this.value) {
				if(value < this.minValue || value > this.maxValue) {
					throw new IllegalArgumentException("The value '"+value+"' is not within the min-max range.");
				}
				this.value = value;
				this.fireStateChanged();
			}
		}

		@Override
		public int getMinValue() {
			return this.minValue;
		}

		@Override
		public void setMinValue(int minValue) {
			if(this.minValue != minValue) {
				if(minValue > this.maxValue) {
					throw new IllegalArgumentException("The minimum value cannot be greater than the maximum value.");
				}
				this.minValue = minValue;
				if(this.value < minValue) {
					this.value = minValue;
				}
				this.fireStateChanged();
			}
		}

		@Override
		public int getMaxValue() {
			return this.maxValue;
		}

		@Override
		public void setMaxValue(int maxValue) {
			if(this.maxValue != maxValue) {
				if(maxValue < this.minValue) {
					throw new IllegalArgumentException("The maximum value cannot be lower than the minimum value.");
				}
				this.maxValue = maxValue;
				if(this.value > maxValue) {
					this.value = maxValue;
				}
				this.fireStateChanged();
			}
		}

		@Override
		public void addChangeListener(ChangeListener listener) {
			this.listenerList.add(ChangeListener.class, listener);
		}

		@Override
		public void removeChangeListener(ChangeListener listener) {
			this.listenerList.remove(ChangeListener.class, listener);
		}
		
		/**
		 * Notifies all the registered change listeners about a model change.
		 */
		protected void fireStateChanged() {
			ChangeEvent event = new ChangeEvent(this);
			Object listeners[] = this.listenerList.getListenerList();
			for(int i = listeners.length - 2; i >= 0; i -= 2) {
				if(listeners[i] == ChangeListener.class) {
					((ChangeListener) listeners[i+1]).stateChanged(event);
				}
			}
		}
		
		/**
		 * Returns an array of all the currently registered change listeners.
		 * 
		 * @return Array of change listeners.
		 */
		public ChangeListener[] getChangeListeners() {
			return (ChangeListener[]) this.listenerList.getListeners(ChangeListener.class);
		}
		
	}
	
	
	public static abstract class ReportingSliderUI extends ComponentUI {
	}
	
	/**
	 * Default UI implementation for this component.
	 */
	public static class BasicReportingSliderUI extends ReportingSliderUI {
		private static final int TEXT_AREA_SIZE = 70;
		
		/**
		 * The associated slider.
		 */
		protected JReportingSlider reportingSlider;
		/**
		 * Actual slider.
		 */
		protected JSlider slider;
		/**
		 * Text field that displays the value from the slider.
		 */
		protected JTextField textField;
		
		protected CellRendererPane cellRendererPane;

		protected ChangeListener internalChangeListener;
		
		protected ChangeListener changeListener;
		
		public static ComponentUI createUI(JComponent component) {
			return new BasicReportingSliderUI();
		}
		
		@Override
		public void installUI(JComponent c) {
			this.reportingSlider = (JReportingSlider) c;
			this.installDefaults();
			this.installComponents();
			this.installListeners();
			this.reportingSlider.setBorder(new EmptyBorder(1, 1, 1, 1));
			this.reportingSlider.setLayout(new ReportingLayout());
		}
		
		@Override
		public void uninstallUI(JComponent c) {
			this.uninstallListeners();
			this.uninstallComponents();
			this.uninstallDefaults();
			this.reportingSlider.setLayout(null);
			this.reportingSlider = null;
		}
		
		public void installDefaults() {
		}
		
		public void installComponents() {
			this.textField = new JTextField();
			this.textField.setEditable(false);
			this.slider = new JSlider(JSlider.HORIZONTAL);
			this.slider.setFocusable(false);
			this.reportingSlider.add(this.textField);
			this.reportingSlider.add(this.slider);
		}
		
		public void installListeners() {
			this.internalChangeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider slider = (JSlider) e.getSource();
					reportingSlider.getModel().setValue(slider.getValue());
				}
			};
			this.slider.addChangeListener(this.internalChangeListener);
			this.changeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					slider.setMinimum(reportingSlider.getModel().getMinValue());
					slider.setMaximum(reportingSlider.getModel().getMaxValue());
					textField.setText(Integer.toString(reportingSlider.getModel().getValue()));
					reportingSlider.repaint();
				}
			};
			this.reportingSlider.getModel().addChangeListener(this.changeListener);
		}
		
		public void uninstallDefaults() {
			this.reportingSlider.remove(this.cellRendererPane);
			this.cellRendererPane = null;
		}
		
		public void uninstallComponents() {
		}
		
		public void uninstallListeners() {
		//	this.reportingSlider.removeMouseListener(this.mouseListener);
		//	this.reportingSlider.removeMouseMotionListener(this.mouseMotionListener);
			this.reportingSlider.getModel().removeChangeListener(this.changeListener);

			this.internalChangeListener = null;
			this.changeListener = null;
		}
		
		@Override
		public void paint(Graphics g, JComponent c) {
			super.paint(g, c);
		}
		
		protected void paintTextField(Graphics g, Rectangle rectangle) {
			int width = (rectangle.width < TEXT_AREA_SIZE ? TEXT_AREA_SIZE : rectangle.width);
			this.cellRendererPane.paintComponent(g, this.textField, this.reportingSlider,
				rectangle.x, rectangle.y, TEXT_AREA_SIZE, rectangle.height, true);			
		}
		
		protected void paintSlider(Graphics g, Rectangle rectangle) {
			if(rectangle.width > TEXT_AREA_SIZE) {
				this.cellRendererPane.paintComponent(g, this.slider, this.reportingSlider,
					rectangle.x + TEXT_AREA_SIZE, rectangle.y, rectangle.width - TEXT_AREA_SIZE, rectangle.height, true);
			}
		}
		
		protected int modelValueToSliderValue(int modelValue) {
			return modelValue;
		}
		
		protected int sliderValueToModelValue(int sliderValue) {
			sliderValue -= TEXT_AREA_SIZE;
			
			int sliderWidth = this.reportingSlider.getWidth() - TEXT_AREA_SIZE;
			
			int range = this.reportingSlider.getModel().getMaxValue() - this.reportingSlider.getModel().getMinValue();
			
			return (int) ((double)sliderValue / (double)sliderWidth) * range;
		}
		
		class ReportingLayout implements LayoutManager {

			@Override
			public void addLayoutComponent(String name, Component comp) {
			}

			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				int width = 0;
				int height = 0;
				JReportingSlider slider = (JReportingSlider) parent;
				height = Math.max(textField.getHeight(), slider.getHeight());
				width = TEXT_AREA_SIZE + slider.getWidth();
				Insets ins = parent.getInsets();
				
				return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return this.preferredLayoutSize(parent);
			}

			@Override
			public void layoutContainer(Container parent) {
				JReportingSlider reportingSlider = (JReportingSlider) parent;
				
				Insets ins = parent.getInsets();
				int width = parent.getWidth() - ins.left - ins.right;
				int height = parent.getHeight() - ins.top - ins.bottom;
				
				int textFieldWidth = (width < TEXT_AREA_SIZE ? width : TEXT_AREA_SIZE);
				
				textField.setBounds(ins.left, ins.top, textFieldWidth, height);
				if(textFieldWidth < TEXT_AREA_SIZE) {
					slider.setBounds(0, 0, 0, 0);
				} else {
					slider.setBounds(ins.left + TEXT_AREA_SIZE + 2, ins.top, width - textFieldWidth - 2, height);
				}
				slider.setMinimum(reportingSlider.getModel().getMinValue());
				slider.setMaximum(reportingSlider.getModel().getMaxValue());
				slider.setValue(reportingSlider.getModel().getValue());
				textField.setText(Integer.toString(reportingSlider.getModel().getValue()));
			}
		}
	}
}
