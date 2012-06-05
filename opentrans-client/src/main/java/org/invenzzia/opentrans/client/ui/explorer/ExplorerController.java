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
package org.invenzzia.opentrans.client.ui.explorer;

import org.invenzzia.helium.gui.ContextManager;
import org.invenzzia.helium.gui.mvc.IController;
import org.invenzzia.helium.gui.ui.trees.HeliumTreeController;
import org.invenzzia.helium.gui.ui.trees.HeliumTreeModel;
import org.invenzzia.opentrans.client.projectmodel.ProjectNodeProvider;
import org.invenzzia.opentrans.client.projectmodel.WorldDescriptor;
import org.invenzzia.opentrans.visitons.VisitonsProject;

/**
 * Description here.
 * 
 * @author Tomasz Jędrzejewski
 */
public class ExplorerController implements IController<ExplorerView> {
	
	private ExplorerView view;
	private VisitonsProject model;
	private HeliumTreeModel treeModel;
	private HeliumTreeController treeController;

	public ExplorerController(VisitonsProject project, HeliumTreeController treeController, ContextManager ctxMgr) {
		this.model = project;
		this.treeController = treeController;
		this.treeModel = new HeliumTreeModel(ctxMgr.getCurrentContainer());
		this.initializeProjectExplorer();
		
		this.treeController.setModel(this.treeModel);
	}

	@Override
	public void attachView(ExplorerView object) {
		this.view = object;
		this.view.setTreeController(this.treeController);
		this.view.setProjectName(this.model.getName());
	}

	@Override
	public void detachView(ExplorerView object) {
		this.view = null;
	}
	
	private void initializeProjectExplorer() {
		this.treeModel.setMainNodeProvider(new ProjectNodeProvider(this.model));
		this.treeModel.addNodeDescriptor(WorldDescriptor.class);
	}
}
