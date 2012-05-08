/*
 * OpenTrans - public transport simulator
 * Copyright (c) 2011-2012 Invenzzia Group
 * 
 * OpenTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenTrans. If not, see <http://www.gnu.org/licenses/>.
 */
package org.invenzzia.opentrans.client.presenter.project;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.invenzzia.helium.application.Application;
import org.invenzzia.helium.forms.DefinitionManager;
import org.invenzzia.helium.forms.Form;
import org.invenzzia.helium.forms.IFormFactory;
import org.invenzzia.helium.forms.exception.FormException;
import org.invenzzia.helium.gui.BaseLeafPresenter;
import org.invenzzia.helium.gui.LifecycleManager;
import org.invenzzia.helium.gui.annotations.Dialog;
import org.invenzzia.helium.gui.exception.PresenterConfigurationException;
import org.invenzzia.helium.gui.presenter.dialog.DialogPresenter;
import org.invenzzia.helium.gui.presenter.dialog.IDialogPresenter;
import org.invenzzia.opentrans.visitons.VisitonsProject;
import org.picocontainer.Characteristics;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
@Dialog(
	title="New project",
	buttons = { DialogPresenter.DialogButtons.BUTTON_OK, DialogPresenter.DialogButtons.BUTTON_CANCEL }
)
public class ProjectPresenter extends BaseLeafPresenter implements IDialogPresenter {
	private JPanel view;
	private Form form;

	public ProjectPresenter(Application application, LifecycleManager manager, String name) throws PresenterConfigurationException {
		super(application, manager, name);
	} // end ProjectPresenter();
	
	@Override
	protected boolean doStartup() {
		try {
			this.form = this.application.getContainer().getComponent(IFormFactory.class).createForm("forms/NewProjectForm.xml");

			VisitonsProject project = new VisitonsProject();
			this.form.setBean(project);
			this.view = this.form.render();
			return true;
		} catch(FormException exception) {
			JOptionPane.showMessageDialog(null, exception.getMessage());
			return false;
		}
	}

	@Override
	protected boolean doShutdown() {
		return true;
	}

	@Override
	public boolean confirm() {
		if(this.form.validate()) {
			this.form.populate();
			return true;
		} else {
			this.form.refresh(this.view);
			return false;
		}
	}

	@Override
	public Component getView() {
		return this.view;
	}

} // end ProjectPresenter;
