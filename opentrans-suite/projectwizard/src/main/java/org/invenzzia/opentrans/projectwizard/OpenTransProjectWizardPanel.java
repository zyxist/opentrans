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

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class OpenTransProjectWizardPanel implements WizardDescriptor.Panel,
	WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel
{

	private WizardDescriptor wizardDescriptor;
	private OpenTransProjectPanelVisual component;
	private final Set<ChangeListener> listeners = new HashSet<>(1); // or can use ChangeSupport in NB 6.0

	public OpenTransProjectWizardPanel()
	{
	} // end OpenTransProjectWizardPanel();

	public Component getComponent()
	{
		if(null == this.component)
		{
			this.component = new OpenTransProjectPanelVisual(this);
			this.component.setName(NbBundle.getMessage(OpenTransProjectWizardPanel.class, "LBL_CreateProjectStep"));
		}
		return this.component;
	} // end getComponent();

	public HelpCtx getHelp()
	{
		return new HelpCtx(OpenTransProjectWizardPanel.class);
	} // end getHelp();

	public boolean isValid()
	{
		this.getComponent();
		return this.component.valid(this.wizardDescriptor);
	} // end isValid();
	

	public final void addChangeListener(ChangeListener l)
	{
		synchronized(this.listeners)
		{
			this.listeners.add(l);
		}
	} // end addChangeListener();

	public final void removeChangeListener(ChangeListener l)
	{
		synchronized(this.listeners)
		{
			this.listeners.remove(l);
		}
	} // end removeChangeListener();

	protected final void fireChangeEvent()
	{
		Set<ChangeListener> ls;
		synchronized(this.listeners)
		{
			ls = new HashSet<>(this.listeners);
		}
		ChangeEvent ev = new ChangeEvent(this);
		for(ChangeListener l : ls)
		{
			l.stateChanged(ev);
		}
	} // end fireChangeEvent();

	public void readSettings(Object settings)
	{
		this.wizardDescriptor = (WizardDescriptor) settings;
		this.component.read(this.wizardDescriptor);
	} // end readSettings();

	public void storeSettings(Object settings)
	{
		WizardDescriptor d = (WizardDescriptor) settings;
		this.component.store(d);
	} // end storeSettings();

	public boolean isFinishPanel()
	{
		return true;
	} // end isFinishPanel();

	public void validate() throws WizardValidationException
	{
		this.getComponent();
		this.component.validate(this.wizardDescriptor);
	} // end validate();
} // end OpenTransProjectWizardPanel;
