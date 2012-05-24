package org.agilereview.storage.xml;

import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Manages the current source folder and exchanges of the source folder.
 * @author Peter Reuter (25.03.2012)
 */
public class SourceFolderManager implements IPropertyChangeListener {

	//TODO compatibility to old nature IDs?
	
	/**
	 * Name of the property which stores the name of the current source folder
	 */
	public static String SOURCEFOLDER_PROPERTYNAME = "source_folder";
	/**
	 * Nature of the Review Source Folder
	 */
	public static String AGILEREVIEW_NATURE = "org.agilereview.sourcefolder";
	/**
	 * Nature of the active Review Source Folder
	 */
	public static String AGILEREVIEW_ACTIVE_NATURE = "org.agilereview.sourcefolder.active";
	/**
	 * The {@link IProject} that represents the current source folder
	 */
	private static IProject currentSourceFolder = null;

	/**
	 * @return The {@link IProject} that represents the current source folder
	 * @author Peter Reuter (25.03.2012)
	 */
	static IProject getCurrentSourceFolder() {
		if (currentSourceFolder == null) {
			setCurrentSourceFolderProject();
		}
		return currentSourceFolder;
	}

	/**
	 * Loads the the current source folder specified by the name. The name is loaded from the preferences.
	 * @author Peter Reuter (28.04.2012)
	 */
	@SuppressWarnings("deprecation")
	private static void setCurrentSourceFolderProject() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject p = workspaceRoot.getProject(Activator.getDefault().getPluginPreferences().getString(SOURCEFOLDER_PROPERTYNAME));
		if (!p.exists() || !p.isOpen()) {
			String message = "Selected Source Folder does not exist or is not open.";
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
			ExceptionHandler.notifyUser(message);
			Display.getCurrent().syncExec(new Runnable() {
				@Override
				public void run() {
					NoReviewSourceProjectWizard dialog = new NoReviewSourceProjectWizard();
					WizardDialog wDialog = new WizardDialog(Display.getCurrent().getActiveShell(), dialog);
					wDialog.setBlockOnOpen(true);
					wDialog.open();
				}
			});
		} else {
			setProjectNatures(currentSourceFolder, new String[]{ AGILEREVIEW_NATURE });
			currentSourceFolder = p;
			setProjectNatures(currentSourceFolder, new String[]{ AGILEREVIEW_NATURE, AGILEREVIEW_ACTIVE_NATURE });
		}
	}
	
	/**
	 * Creates and opens the given AgileReview Source Project (if not existent or closed)
	 * @param projectName project name
	 * @return <i>true</i> if everything worked,<i>false</i> if something went wrong
	 */
	public static boolean createAndOpenReviewProject(String projectName) {
		boolean result = true;
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject p = workspaceRoot.getProject(projectName);

		try {
			// Create a new Project, if necessary
			if (!p.exists()) {
				p.create(null);// TODO: Use ProgressMonitor?
				while (!p.exists()) {
				}
			}

			// Open the Project, if necessary
			if (!p.isOpen()) {
				p.open(null);// TODO: Use ProgressMonitor?
			}
			while (!p.isOpen()) {
			}

			// Set project description
			setProjectNatures(p, new String[] { AGILEREVIEW_NATURE });
		} catch (final CoreException e) {
			String message = "Could not create or open AgileReview Source Project "+projectName+"!";
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
			ExceptionHandler.notifyUser(message);
			result = false;
		}
		return result;
	}
	
	/**
	 * Sets the given project natures to those specified by those in "natures".
	 * @param p The project.
	 * @param natures The natures.
	 * @return <code>true</code> if everything worked, <code>false</code> otherwise
	 */
	private static boolean setProjectNatures(IProject p, String[] natures) {
		try {
			IProjectDescription projectDesc = p.getDescription();
			projectDesc.setNatureIds(natures);
			p.setDescription(projectDesc, null);// TODO: Use ProgressMonitor?
			return true;
		} catch (final CoreException e) {
			String message = "Could not set project natures for project "+p.getName()+"!";
			ExceptionHandler.notifyUser(message, e);
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
			return false;
		}
	}
	
	/**
	 * 
	 * @author Peter Reuter (24.05.2012)
	 */
	public static void prepareCurrentSourceProjectForClosing() {
		setProjectNatures(currentSourceFolder, new String[]{ AGILEREVIEW_NATURE });
	}
	///////////////////////////////////////
	// methods of PropertyChangeListener //
	///////////////////////////////////////

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(SOURCEFOLDER_PROPERTYNAME)) {
			setCurrentSourceFolderProject();
		}
	}

}
