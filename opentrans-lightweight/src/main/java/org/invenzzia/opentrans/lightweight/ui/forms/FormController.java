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

package org.invenzzia.opentrans.lightweight.ui.forms;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Form controller handles the form processing logic. Forms consist of fields, and have an
 * associated action button that triggers validation. The operations specific to concrete
 * form are delegated to a separate interface which must be implemented by the user and
 * the instance injected to the form controller constructor.
 * 
 * @author Tomasz Jędrzejewski
 */
public class FormController implements IFormAccessor {
	private static final Color ERROR_INDICATION_COLOR = new Color(255, 204, 204);
	/**
	 * The button that triggers form validation and approval. Searched automatically with annotations.
	 */
	private JButton okButton;
	/**
	 * The button that triggers form validation and approval. Searched automatically with annotations.
	 */
	private JButton reloadButton;
	/**
	 * Actual implementation of the form operation hooks.
	 */
	private IFormHandler formHandler;
	/**
	 * Managed panel.
	 */
	private Component managedPanel;
	/**
	 * Managed information label. Searched automatically with annotations.
	 */
	private JLabel infoLabel;
	/**
	 * Form validator can save here the errors.
	 */
	private List<String> errors;
	/**
	 * Map of found form fields.
	 */
	private Map<String, JComponent> fields;
	/**
	 * Additional form status listeners.
	 */
	private Set<IFormStatusListener> formStatusListeners;
	
	public FormController(IFormHandler handler) {
		this.formHandler = Preconditions.checkNotNull(handler, "The form controller must have a handler.");
		this.errors = new LinkedList<>();
		this.fields = new LinkedHashMap<>();
		this.formStatusListeners = new LinkedHashSet<>();
	}
	
	/**
	 * Adds a new form status listener.
	 * 
	 * @param listener 
	 */
	public void addFormStatusListener(IFormStatusListener listener) {
		this.formStatusListeners.add(Preconditions.checkNotNull(listener, "Cannot add a NULL listener."));
	}
	
	/**
	 * Removes the specified form status listener.
	 * 
	 * @param listener 
	 */
	public void removeFormStatusListener(IFormStatusListener listener) {
		this.formStatusListeners.remove(listener);
	}
	
	/**
	 * Removes all the form status listeners.
	 */
	public void removeFormStatusListeners() {
		this.formStatusListeners.clear();
	}
	
	/**
	 * Binds a managed panel that contains the form. The panel is scanned for
	 * annotations like {@link FormField} or {@link FormButton} and the internal
	 * registry is populated. Additionally, the necessary event listeners are
	 * registered.
	 * 
	 * @param managedPanel 
	 */
	public final void setManagedPanel(final Component managedPanel) {
		this.managedPanel = managedPanel;
		this.fields.clear();
		
		List<JComponent> triggeredComponents = new LinkedList<>();
		
		for(Field field: managedPanel.getClass().getDeclaredFields()) {
			FormField ff = field.getAnnotation(FormField.class);
			FormButton fb = field.getAnnotation(FormButton.class);
			FormNotificationLabel fnl = field.getAnnotation(FormNotificationLabel.class);
			if(null != ff) {
				if(!JComponent.class.isAssignableFrom(field.getType())) {
					throw new IllegalStateException("The annotation @FormField must be put only on Swing component fields.");
				}
				// If flagged as triggered, register in the list to install additional action listener on it.
				JComponent component = this.getComponentFrom(managedPanel, field, JComponent.class);
				if(ff.triggerFormProcessing()) {
					triggeredComponents.add(component);
				}
				this.addField(ff.key(), component);
			} else if(null != fb) {
				if(!JButton.class.isAssignableFrom(field.getType())) {
					throw new IllegalStateException("The annotation @FormButton must be put only on Swing button fields.");
				}
				switch(fb.type()) {
					case "ok":
						this.okButton = this.getComponentFrom(managedPanel, field, JButton.class);
						break;
					case "reload":
						this.reloadButton = this.getComponentFrom(managedPanel, field, JButton.class);
						break;
					default:
						throw new IllegalStateException("Unknown @FormButton type: '"+fb.type()+"'");
				}
			} else if(null != fnl) {
				if(!JLabel.class.isAssignableFrom(field.getType())) {
					throw new IllegalStateException("The annotation @FormNotificationLabel must be put only on Swing label fields.");
				}
				this.infoLabel = this.getComponentFrom(managedPanel, field, JLabel.class);
			}
		}
		if(null == this.okButton) {
			throw new IllegalArgumentException("The specified form panel does not have a button that allows to save the form.");
		}
		
		FormSubmissionActionListener listener = new FormSubmissionActionListener();
		this.okButton.addActionListener(listener);
		this.installTriggeredListeners(triggeredComponents, listener);
		if(null != this.reloadButton) {
			this.reloadButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errors.clear();
					resetFields();
					formHandler.loadModel(FormController.this);
					if(null != infoLabel) {
						infoLabel.setText("");
						managedPanel.revalidate();
					}
				}
			});
		}
		this.formHandler.loadModel(this);
	}
	
	/**
	 * Extracts the component from a private annotated field of the panel.
	 * 
	 * @param panel The panel representing the form.
	 * @param field The reflection field.
	 * @param type Type of the field.
	 * @return Component instance.
	 */
	private <T> T getComponentFrom(Component panel, Field field, Class<T> type) {
		field.setAccessible(true);
		try {
			return (T) field.get(managedPanel);
		} catch(IllegalAccessException exception) {
			throw new IllegalStateException("This should not happen.", exception);
		}
	}

	/**
	 * Adds a new field to the internal registry.
	 * 
	 * @param key Field key.
	 * @param object  Field component.
	 */
	public void addField(String key, JComponent object) {
		if(this.fields.containsKey(key)) {
			throw new IllegalArgumentException("Duplicate form field: '"+key+"'.");
		}
		this.fields.put(key, object);
	}
	
	/**
	 * Removes all the fields from the internal registry.
	 */
	public void resetFields() {
		for(JComponent component: this.fields.values()) {
			component.setBackground(Color.WHITE);
		}
	}
	
	/**
	 * Reloads the model back to the form.
	 */
	public void refresh() {
		this.formHandler.loadModel(this);
	}
	
	@Override
	public <T> T getField(String key, Class<T> type) {
		if(!this.fields.containsKey(key)) {
			throw new IllegalArgumentException("The form field key '"+key+"' does not exist.");
		}
		return (T) this.fields.get(key);
	}

	@Override
	public void setInvalid(String key, String errorMessage) {
		if(!this.fields.containsKey(key)) {
			throw new IllegalArgumentException("The form field key '"+key+"' does not exist.");
		}
		JComponent component = this.fields.get(key);
		component.setBackground(ERROR_INDICATION_COLOR);
		this.errors.add(errorMessage);
	}

	/**
	 * If the component supports 'addActionListener' method, we will register the action listener.
	 * 
	 * @param triggeredComponents
	 * @param listener 
	 */
	private void installTriggeredListeners(List<JComponent> triggeredComponents, FormSubmissionActionListener listener) {
		for(JComponent component: triggeredComponents) {
			try {
				Method addActionListener = component.getClass().getMethod("addActionListener", ActionListener.class);
				addActionListener.invoke(component, listener);				
			} catch(NoSuchMethodException exception) {
				throw new IllegalStateException("'triggersFormSubmission' flag is set on component which does not support action listeners.");
			} catch(IllegalAccessException | InvocationTargetException exception) {
				throw new IllegalStateException("Cannot register a form submission listener on one of triggered components.", exception);
			}
		}
	}
	
	/**
	 * Handles the form submission process.
	 */
	class FormSubmissionActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			errors.clear();
			resetFields();
			boolean validationOk = formHandler.validateModel(FormController.this);
			if(!errors.isEmpty() || !validationOk) {
				JOptionPane.showMessageDialog(managedPanel, Joiner.on("\n").join(errors), "Form validation error", JOptionPane.WARNING_MESSAGE);
				if(null != infoLabel) {
					infoLabel.setText(formHandler.getFailureMessage());
					managedPanel.revalidate();
				}
				for(IFormStatusListener listener: formStatusListeners) {
					listener.onFailedSubmit();
				}
			} else {
				formHandler.saveModel(FormController.this);
				formHandler.loadModel(FormController.this);
				if(null != infoLabel) {
					infoLabel.setText(formHandler.getSuccessMessage());
					managedPanel.revalidate();
				}
				for(IFormStatusListener listener: formStatusListeners) {
					listener.onSuccessfulSubmit();
				}
			}
		}
	}

}
