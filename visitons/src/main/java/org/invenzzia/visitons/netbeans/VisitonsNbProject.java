/*
 * Visitons - transportation network simulation and visualization library.
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
package org.invenzzia.visitons.netbeans;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.invenzzia.visitons.project.VisitonsProject;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * A wrapper for the original Simulation project that makes a NetBeans
 * Project from it.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
public class VisitonsNbProject extends VisitonsProject implements Project
{
	private final FileObject projectDir;
	private final ProjectState state;
	private Lookup lookup = null;
	

	VisitonsNbProject(FileObject projectDirectory, ProjectState projectState)
	{
		this.projectDir = projectDirectory;
		this.state = projectState;
	} // end VisitonsNbProject();

	@Override
	public FileObject getProjectDirectory()
	{
		return this.projectDir;
	} // end getProjectDirectory();

	@Override
	public Lookup getLookup()
	{
		if(null != this.lookup)
		{
			return this.lookup;
		}
		return this.lookup = Lookups.fixed(new Object[]{
			this.state,
			new ActionProviderImpl(),
			new Info(),
			new VisitonsNbProjectLogicalView(this),
		});
	} // end getLookup();
	
	private final class ActionProviderImpl implements ActionProvider
	{
		private String[] supported = new String[]
		{
			ActionProvider.COMMAND_DELETE,
			ActionProvider.COMMAND_COPY,
		};

		@Override
		public String[] getSupportedActions()
		{
			return this.supported;
		} // end getSupportedActions();

		@Override
		public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException
		{
			if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE))
			{
				DefaultProjectOperations.performDefaultDeleteOperation(VisitonsNbProject.this);
			}
			if (string.equalsIgnoreCase(ActionProvider.COMMAND_COPY))
			{
				DefaultProjectOperations.performDefaultCopyOperation(VisitonsNbProject.this);
			}
		} // end invokeAction();

		@Override
		public boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException
		{
			if ((command.equals(ActionProvider.COMMAND_DELETE)))
			{
				return true;
			}
			else if ((command.equals(ActionProvider.COMMAND_COPY)))
			{
				return true;
			}
			else
			{
				throw new IllegalArgumentException(command);
			}
		} // end isActionEnabled();
	} // end ActionProviderImpl;
	
	private final class Info implements ProjectInformation
	{
		@Override
		public Icon getIcon()
		{
			return new ImageIcon(ImageUtilities.loadImage("org/invenzzia/visitons/netbeans/project.png"));
		} // end getIcon();

		@Override
		public String getName()
		{
			return VisitonsNbProject.this.getProjectDirectory().getName();
		} // end getName();

		@Override
		public String getDisplayName()
		{
			return VisitonsNbProject.this.getName();
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener pcl)
		{
			//do nothing, won't change
		} // end addPropertyChangeListener();

		@Override
		public void removePropertyChangeListener(PropertyChangeListener pcl)
		{
			//do nothing, won't change
		} // removePropertyChangeListener();

		@Override
		public Project getProject()
		{
			return VisitonsNbProject.this;
		} // end getProject();
	} // end Info;
} // end VisitonsNbProject;

