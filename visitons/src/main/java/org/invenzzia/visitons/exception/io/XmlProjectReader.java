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
package org.invenzzia.visitons.exception.io;

import org.invenzzia.utils.Args;
import org.invenzzia.utils.XmlUtil;
import org.invenzzia.utils.exception.ApplicationException;
import org.invenzzia.utils.exception.ParseException;
import org.invenzzia.visitons.project.VisitonsProject;
import org.invenzzia.visitons.visualization.World;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This reader reads the project definition from an XML file.
 * 
 * @author Tomasz Jędrzejewski
 */
@ServiceProvider(service=IProjectReader.class)
public class XmlProjectReader implements IProjectReader
{
	/**
	 * The analyzed project.
	 */
	private VisitonsProject theProject;

	@Override
	public void readProject(VisitonsProject project, FileObject projectDirectory) throws ApplicationException
	{
		this.theProject = project;
		
		this.readProjectFile(projectDirectory.getFileObject(VisitonsProject.PROJECT_FILE));
	} // end readProject();
	
	/**
	 * Reads the data from the main project file.
	 * 
	 * @param fileObject The reference to the project file object.
	 * @throws ApplicationException 
	 */
	protected void readProjectFile(FileObject fileObject) throws ApplicationException
	{
		Document document = XmlUtil.readXml(fileObject.getPath());
		Element rootElement = document.getDocumentElement();
		
		this.theProject.setName(Args.checkNotEmpty(XmlUtil.quickPropertyRead(rootElement, "name")));
		this.theProject.setAuthor(XmlUtil.quickPropertyRead(rootElement, "author"));
		this.theProject.setWebsite(XmlUtil.quickPropertyRead(rootElement, "website", ""));
		this.theProject.setNotes(XmlUtil.quickPropertyRead(rootElement, "notes", ""));
	} // end readProjectFile();
	
	/**
	 * Reads the geographical world structure.
	 * 
	 * @param fileObject The reference to the world file object.
	 * @throws ApplicationException 
	 */
	protected void readWorldFile(FileObject fileObject) throws ApplicationException
	{
		World world = this.theProject.getWorld();
		
		Document document = XmlUtil.readXml(fileObject.getPath());
		
		Element rootElement = document.getDocumentElement();
		try
		{
			world.construct(
				Integer.parseInt(rootElement.getAttribute("x")),
				Integer.parseInt(rootElement.getAttribute("y"))
			);
			world.setIterators(
				Long.parseLong(rootElement.getAttribute("it-vertex")),
				Long.parseLong(rootElement.getAttribute("it-edge"))
			);
		}
		catch(NumberFormatException wrongNumber)
		{
			throw new ParseException("Cannot parse the world settings: invalid number format.", fileObject.getName());
		}
	} // end readWorldFile();
	
	protected void readSituations(FileObject fileObject) throws ApplicationException
	{
		
	} // end readSituations();
	
	protected void readSimulations(FileObject fileObject) throws ApplicationException
	{
		
	} // end readSimulations();
} // end XmlProjectReader;

