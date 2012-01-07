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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import org.invenzzia.utils.XmlUtil;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * The object of the wizard iterator creates and scans the wizard panels, imports the
 * data from it and finally, creates the project from a predefined ZIP
 * archive.
 * 
 * @author Tomasz Jędrzejewski
 */
public class OpenTransProjectWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator
{
	/**
	 * The index of the panel.
	 */
	private int index;
	/**
	 * The wizard panels.
	 */
	private WizardDescriptor.Panel[] panels;
	/**
	 * The wizard descriptor.
	 */
	private WizardDescriptor wiz;

	public OpenTransProjectWizardIterator()
	{
	} // end OpenTransProjectWizardIterator();

	/**
	 * A factory method that creates a new project wizard
	 * iterator.
	 * 
	 * @return The new project wizard iterator.
	 */
	public static OpenTransProjectWizardIterator createIterator()
	{
		return new OpenTransProjectWizardIterator();
	} // end createIterator();

	/**
	 * Produces the panels that form the wizard.
	 * 
	 * @return The array of wizard panels.
	 */
	private WizardDescriptor.Panel[] createPanels()
	{
		return new WizardDescriptor.Panel[]
			{
				new OpenTransProjectWizardPanel(),
			};
	} // end createPanels();

	/**
	 * @return The list of wizard step names.
	 */
	private String[] createSteps()
	{
		return new String[]
			{
				NbBundle.getMessage(OpenTransProjectWizardIterator.class, "LBL_CreateProjectStep")
			};
	} // end createSteps();

	/**
	 * Instantiates the project once the wizard is finished.
	 * 
	 * @return The list of projects to open.
	 * @throws IOException 
	 */
	public Set/*<FileObject>*/ instantiate(/*ProgressHandle handle*/) throws IOException
	{
		Set<FileObject> resultSet = new LinkedHashSet<>();
		File dirF = FileUtil.normalizeFile((File) this.wiz.getProperty("projdir"));
		dirF.mkdirs();

		FileObject template = Templates.getTemplate(this.wiz);
		FileObject dir = FileUtil.toFileObject(dirF);
		unZipFile(template.getInputStream(), dir, this.wiz);

		// Always open top dir as a project:
		resultSet.add(dir);
		// Look for nested projects to open as well:
		Enumeration<? extends FileObject> e = dir.getFolders(true);
		while(e.hasMoreElements())
		{
			FileObject subfolder = e.nextElement();
			if(ProjectManager.getDefault().isProject(subfolder))
			{
				resultSet.add(subfolder);
			}
		}

		File parent = dirF.getParentFile();
		if(parent != null && parent.exists())
		{
			ProjectChooser.setProjectsFolder(parent);
		}

		return resultSet;
	} // end instantiate();

	public void initialize(WizardDescriptor wiz)
	{
		this.wiz = wiz;
		this.index = 0;
		this.panels = createPanels();
		// Make sure list of steps is accurate.
		String[] steps = createSteps();
		for(int i = 0; i < this.panels.length; i++)
		{
			Component c = this.panels[i].getComponent();
			if(steps[i] == null)
			{
				// Default step name to component name of panel.
				// Mainly useful for getting the name of the target
				// chooser to appear in the list of steps.
				steps[i] = c.getName();
			}
			if(c instanceof JComponent)
			{ // assume Swing components
				JComponent jc = (JComponent) c;
				// Step #.
				// TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
				jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
				// Step name (actually the whole list for reference).
				jc.putClientProperty("WizardPanel_contentData", steps);
			}
		}
	} // end initialize();

	/**
	 * Uninitializes the given wizard descriptor.
	 * 
	 * @param wiz The wizard descriptor to uninitialize.
	 */
	public void uninitialize(WizardDescriptor wiz)
	{
		this.wiz.putProperty("projdir", null);
		this.wiz.putProperty("name", null);
		this.wiz.putProperty("author", null);
		this.wiz.putProperty("website", null);
		this.wiz.putProperty("notes", null);
		this.wiz = null;
		panels = null;
	} // end uninitialize();

	public String name()
	{
		return MessageFormat.format("{0} of {1}",
			new Object[]
			{
				new Integer(this.index + 1), new Integer(this.panels.length)
			});
	} // end name();

	public boolean hasNext()
	{
		return this.index < panels.length - 1;
	} // end hasNext();

	public boolean hasPrevious()
	{
		return this.index > 0;
	} // end hasPrevious();

	public void nextPanel()
	{
		if(!hasNext())
		{
			throw new NoSuchElementException();
		}
		this.index++;
	} // end nextPanel();

	public void previousPanel()
	{
		if(!hasPrevious())
		{
			throw new NoSuchElementException();
		}
		this.index--;
	} // end previousPanel();

	public WizardDescriptor.Panel current()
	{
		return this.panels[this.index];
	} // end current();

	// If nothing unusual changes in the middle of the wizard, simply:
	public final void addChangeListener(ChangeListener l)
	{
	} // end addChangeListener();

	public final void removeChangeListener(ChangeListener l)
	{
	} // end removeChangeListener();

	/**
	 * Unzips the project template and copies the files to the new location.
	 * In addition, it configures the main project file using the data from
	 * the wizard.
	 * 
	 * @param source
	 * @param projectRoot
	 * @throws IOException 
	 */
	private static void unZipFile(InputStream source, FileObject projectRoot, WizardDescriptor descriptor) throws IOException
	{
		try
		{
			ZipInputStream str = new ZipInputStream(source);
			ZipEntry entry;
			while((entry = str.getNextEntry()) != null)
			{
				if(entry.isDirectory())
				{
					FileUtil.createFolder(projectRoot, entry.getName());
				}
				else
				{
					FileObject fo = FileUtil.createData(projectRoot, entry.getName());
					if("project.xml".equals(entry.getName()))
					{
						// Special handling for setting name of Ant-based projects; customize as needed:
						filterProjectXML(fo, str, descriptor);
					}
					else
					{
						writeFile(str, fo);
					}
				}
			}
		}
		finally
		{
			source.close();
		}
	} // end unZipFile();

	/**
	 * Attempts to save the file.
	 * 
	 * @param str
	 * @param fo
	 * @throws IOException 
	 */
	private static void writeFile(ZipInputStream str, FileObject fo) throws IOException
	{
		OutputStream out = fo.getOutputStream();
		try
		{
			FileUtil.copy(str, out);
		}
		finally
		{
			out.close();
		}
	} // end writeFile();

	/**
	 * Filters the main project XML file and puts the wizard data into it.
	 * 
	 * @param fo The file object
	 * @param str
	 * @param name
	 * @throws IOException 
	 */
	private static void filterProjectXML(FileObject fo, ZipInputStream str, WizardDescriptor descriptor) throws IOException
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileUtil.copy(str, baos);
			Document doc = XMLUtil.parse(new InputSource(new ByteArrayInputStream(baos.toByteArray())), false, false, null, null);
			Element rootElement = doc.getDocumentElement();
			XmlUtil.quickPropertyWrite(rootElement, "name", (String) descriptor.getProperty("name"));
			XmlUtil.quickPropertyWrite(rootElement, "author", (String) descriptor.getProperty("author"));
			XmlUtil.quickPropertyWrite(rootElement, "website", (String) descriptor.getProperty("website"));
			XmlUtil.quickPropertyWrite(rootElement, "notes", (String) descriptor.getProperty("notes"));
			
			OutputStream out = fo.getOutputStream();
			try
			{
				XMLUtil.write(doc, out, "UTF-8");
			}
			finally
			{
				out.close();
			}
		}
		catch(Exception ex)
		{
			Exceptions.printStackTrace(ex);
			writeFile(str, fo);
		}

	}
}
