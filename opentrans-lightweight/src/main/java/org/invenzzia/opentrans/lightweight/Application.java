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

package org.invenzzia.opentrans.lightweight;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import org.invenzzia.opentrans.lightweight.exception.TaskException;
import org.invenzzia.opentrans.lightweight.model.branding.BrandingModel;
import org.invenzzia.opentrans.lightweight.tasks.*;
import org.invenzzia.opentrans.lightweight.ui.splash.SplashScreen;
import org.invenzzia.opentrans.visitons.Project;
import org.invenzzia.opentrans.visitons.Project.ProjectRecord;
import org.invenzzia.opentrans.visitons.VisitonsModule;
import org.invenzzia.opentrans.visitons.events.NewProjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of the application with the basic routines for the setup.
 * 
 * @author Tomasz Jędrzejewski
 */
@Singleton
public class Application implements IProjectHolder {
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static int STARTUP_EMPTY_PROJECT = 0;
	public static int STARTUP_OPEN_PROJECT = 1;
	public static int STARTUP_HELP = 2;
	
	/**
	 * Application arguments.
	 */
	private String args[];
	/**
	 * Where the project is stored on disk?
	 */
	private String projectPath;
	/**
	 * Currently loaded project.
	 */
	private Project currentProject;
	/**
	 * Application home directory.
	 */
	private String homeDirectory;
	/**
	 * Do we use a splash screen?
	 */
	private boolean useSplash = true;
	/**
	 * Used for waiting on the Swing thread.
	 */
	private CountDownLatch terminationLatch = new CountDownLatch(1);
	/**
	 * How to start the program? Information from the command line arguments.
	 */
	private int startupMode = STARTUP_EMPTY_PROJECT;
	@Inject
	private EventBus eventBus;
	@Inject
	private BrandingModel brandingModel;
	
	public static void main(String args[]) {
		try {
			System.getProperties().load(Application.class.getResourceAsStream("/org/invenzzia/opentrans/opentrans.properties"));
		} catch(IOException exception) {
			System.err.println("Cannot load the internal properties.");
			System.exit(-1);
		}
		
		Injector injector = Guice.createInjector(new VisitonsModule(), new OpentransModule());
		Application application = injector.getInstance(Application.class);
		application.setArgs(args);
		
		if(application.getStartupMode() == STARTUP_HELP) {
			application.printHelp();
		} else {
			List<ITask> tasks = new LinkedList<>();
			tasks.add(injector.getInstance(LoadConfigurationTask.class));
			tasks.add(injector.getInstance(ListenerTask.class));
			tasks.add(injector.getInstance(IconServiceTask.class));
			tasks.add(injector.getInstance(ProjectTask.class));
			tasks.add(injector.getInstance(RendererTask.class));
			tasks.add(injector.getInstance(ThreadTask.class));
			tasks.add(injector.getInstance(CreateGuiTask.class));

			application.start(tasks);
		}
	}
	
	/**
	 * Sets the arguments for the application.
	 * 
	 * @param args The arguments
	 */
	public void setArgs(String args[]) {
		this.args = args;
		this.parseCommands(args);
	}
	
	/**
	 * Returns the startup mode of the application.
	 * 
	 * @return Application startup mode.
	 */
	public int getStartupMode() {
		return this.startupMode;
	}
	
	/**
	 * Starts the application.
	 */
	public void start(List<ITask> tasks) {
		try {
			this.run(tasks);
		} catch(Throwable thr) {
			this.logger.error("An error occurred while executing the application.", thr);
		}
	}
	
	/**
	 * Closes down the application.
	 */
	public void close() {
		this.terminationLatch.countDown();
	}
	
	/**
	 * Returns the currently opened project.
	 * 
	 * @return Current project. 
	 */
	@Override
	public Project getCurrentProject() {
		return this.currentProject;
	}
	
	/**
	 * Returns the path to the currently opened project.
	 * 
	 * @return Project path. 
	 */
	public String getProjectPath() {
		return this.projectPath;
	}
	
	@Override
	public void setCurrentProject(Project project) {
		this.currentProject = Preconditions.checkNotNull(project);
		
		ProjectRecord record = new ProjectRecord();
		record.importData(project, project);
		this.eventBus.post(new NewProjectEvent(record));
	}
	
	public void setProjectPath(String path) {
		this.projectPath = path;
	}

	/**
	 * Returns the home directory of the application. The settings are stored there.
	 * 
	 * @return Application home directory.
	 */
	public String getHomeDirectory() {
		return this.homeDirectory;
	}
	
	/**
	 * Processes the command line arguments.
	 * 
	 * @param args List of command line arguments.
	 */
	private void parseCommands(String args[]) {
		int size = args.length;
		for(int i = 0; i < size; i++) {
			switch(args[i]) {
				case "--no-splash":
					this.useSplash = false;
					break;
				case "--project":
					this.projectPath = args[++i];
					this.startupMode = STARTUP_OPEN_PROJECT;
					break;
				case "--home":
					this.homeDirectory = args[++i];
					break;
				case "--version":
					this.startupMode = STARTUP_HELP;
					break;
			}
		}
	}

	/**
	 * Prints the help information.
	 */
	protected void printHelp() {
		System.out.println("OpenTrans 0.1.0");
		System.out.println("(c) Invenzzia Group 2011-2013");
		System.out.println("This program is distributed under the terms of GNU General Public License 3.0");
		System.out.println("How to start:");
		System.out.println("");
		System.out.println(" ./opentrans [<options>]");
		System.out.println("");
		System.out.println("Options:");
		System.out.println(" --no-splash                 - disables the splash screen.");
		System.out.println(" --project <project_path>    - opens with the given project.");
		System.out.println(" --home <home_path>          - reads the configuration from the given directory.");
		System.out.println(" --version                   - prints this message");
	}
	
	/**
	 * Performs the startup sequence, waits for the end signal, and executes
	 * the terminating tasks.
	 * 
	 * @param tasks List of tasks to execute on the startup and shutdown.
	 */
	protected void run(List<ITask> tasks) throws TaskException {
		this.logger.info("Starting OpenTrans.");
		this.logger.debug("Creating basic services.");
		
		final SplashScreen splash;
		if(this.useSplash) {
			this.logger.debug("Constructing splash screen.");
			splash = new SplashScreen();
			splash.setBrandingModel(this.brandingModel);
			this.eventBus.register(splash);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					splash.setWeight(100);
					splash.display();
				}
			});
		} else {
			splash = null;
		}
		
		try {
			this.logger.info("Executing startup tasks.");
			for(ITask task: tasks) {
				this.logger.debug("Executing task '"+task.getClass().getName()+"'");
				task.startup();
			}
		} finally {
			if(this.useSplash) {
				SplashScreen.close(splash);
				this.eventBus.unregister(splash);
			}
		}
		try {
			this.logger.info("Application started.");
			this.terminationLatch.await();
		} catch(InterruptedException exception) {
		}
		this.logger.info("Terminating the application.");
		Collections.reverse(tasks);
		for(ITask task: tasks) {
			this.logger.debug("Executing task '"+task.getClass().getName()+"'");
			task.shutdown();
		}
	}
}
