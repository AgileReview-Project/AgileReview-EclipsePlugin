package org.agilereview.storage.xml;

import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Manages the current source folder and exchanges of the source folder. 
 * @author Peter Reuter (25.03.2012)
 */
public class SourceFolderManager implements IPropertyChangeListener {
	
	/**
	 * Name of the property which stores the name of the current source folder 
	 */
	private static String SOURCEFOLDER_PROPERTYNAME = "source_folder";
	
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
	 * Loads the the current source folder by its name specified in the preferences.
	 * @author Peter Reuter (28.04.2012)
	 */
	private static void setCurrentSourceFolderProject() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		@SuppressWarnings("deprecation")
		IProject p = workspaceRoot.getProject(Activator.getDefault().getPluginPreferences().getString(SOURCEFOLDER_PROPERTYNAME));
		if (!p.exists() || !p.isOpen()) {
			String message = "Selected Source Folder does not exist or is not open.";
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
			ExceptionHandler.notifyUser(message);
			//XXX handle closed or non-existing source folder --> call NoReviewSourceFolderWizard
		} else {
			currentSourceFolder = p;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(SOURCEFOLDER_PROPERTYNAME)) {
			setCurrentSourceFolderProject();
		}
	}

}
