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

import java.io.IOException;

import org.invenzzia.visitons.project.VisitonsProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * A factory for creating, opening and saving Simulation NetBeans
 * projects.
 * 
 * @copyright Invenzzia Group <http://www.invenzzia.org/>
 * @author Tomasz Jędrzejewski
 */
@org.openide.util.lookup.ServiceProvider(service=ProjectFactory.class)
public class VisitonsNbProjectFactory implements ProjectFactory
{

	@Override
	public boolean isProject(FileObject projectDirectory)
	{
		boolean isProject = true;
		isProject = isProject && projectDirectory.getFileObject(VisitonsProject.PROJECT_FILE) != null;
		isProject = isProject && projectDirectory.getFileObject(VisitonsProject.MAP_FILE) != null;
		isProject = isProject && projectDirectory.getFileObject(VisitonsProject.SIMULATION_DIR) != null;
		isProject = isProject && projectDirectory.getFileObject(VisitonsProject.SITUATION_DIR) != null;
		return isProject;
	} // end isProject();

	@Override
	public Project loadProject(FileObject projectDirectory, ProjectState projectState) throws IOException
	{
		return this.isProject(projectDirectory) ? new VisitonsNbProject(projectDirectory, projectState) : null;
	} // end loadProject();

	@Override
	public void saveProject(Project project) throws IOException, ClassCastException
	{
		FileObject projectRoot = project.getProjectDirectory();
		if(null == projectRoot.getFileObject(VisitonsProject.PROJECT_FILE))
		{
			throw new IOException("The project directory " + projectRoot.getPath() + " has been deleted.");
		}
		
	} // end saveProject();
	
} // end VisitonsNbProjectFactory;

