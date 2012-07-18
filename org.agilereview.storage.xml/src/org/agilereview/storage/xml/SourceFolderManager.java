package org.agilereview.storage.xml;

import java.io.ByteArrayInputStream;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Manages the current source folder and exchanges of the source folder.
 * @author Peter Reuter (25.03.2012)
 */
public class SourceFolderManager {

	//TODO compatibility to old nature IDs?

	/**
	 * Name of the property which stores the name of the current source folder
	 */
	public static final String SOURCEFOLDER_PROPERTYNAME = "source_folder";
	/**
	 * Nature of the Review Source Folder
	 */
	public static final String AGILEREVIEW_NATURE = "org.agilereview.sourcefolder";
	/**
	 * Nature of the active Review Source Folder
	 */
	public static final String AGILEREVIEW_ACTIVE_NATURE = "org.agilereview.sourcefolder.active";
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
	static void setCurrentSourceFolderProject() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		String projectName = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, "", null);
		if ("".equals(projectName)) {
			currentSourceFolder = null;
		} else {
			IProject p = workspaceRoot.getProject(projectName);
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
				setProjectNatures(currentSourceFolder, new String[] { AGILEREVIEW_NATURE });
				currentSourceFolder = p;
				setProjectNatures(currentSourceFolder, new String[] { AGILEREVIEW_NATURE, AGILEREVIEW_ACTIVE_NATURE });
			}
		}
	}

	/**
	 * Creates and opens the given AgileReview Source Folder (if not existent or closed)
	 * @param projectName project name
	 * @return <i>true</i> if everything worked,<i>false</i> if something went wrong
	 */
	public static boolean createAndOpenSourceFolder(String projectName) {
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
			String message = "Could not create or open AgileReview Source Project " + projectName + "!";
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
			String message = "Could not set project natures for project " + p.getName() + "!";
			ExceptionHandler.notifyUser(message, e);
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
			return false;
		}
	}

	/**
	 * Prepares the current source folder if the user wants to close it. Preparation includes removing the "active" nature if it was set.
	 * @author Peter Reuter (24.05.2012)
	 */
	public static void prepareCurrentSourceFolderForClosing() {
		setProjectNatures(currentSourceFolder, new String[] { AGILEREVIEW_NATURE });
	}

	///////////////////////////////////////////////////
	// static methods for managing files and folders //
	///////////////////////////////////////////////////

	/**
	 * Creates an {@link IFile} object which represents the file for storing {@link Comment} objects based on the given reviewId/author pair. The file
	 * is create if it does not exist.
	 * @param reviewId
	 * @param author
	 * @return {@link IFile} for the given parameter pair
	 */
	static IFile getCommentFile(String reviewId, String author) {
		IFile file = getReviewFolder(reviewId).getFile("author_" + author + ".xml");
		if (!file.exists()) {
			try {
				// TODO maybe use ProgressMonitor here?
				file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
				while (!file.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return file;
	}

	/**
	 * Returns an {@link IFolder} object which represents the folder of the {@link Review} given by its ID. The folder is created if it does not
	 * exists.
	 * @param reviewId
	 * @return {@link IFolder} for this review
	 */
	static IFolder getReviewFolder(String reviewId) {
		IFolder folder = getCurrentSourceFolder().getFolder("review." + reviewId);
		if (!folder.exists()) {
			try {
				// TODO maybe use progressmonitor here?
				folder.create(IResource.NONE, true, null);
				while (!folder.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return folder;
	}

	/**
	 * Returns an {@link IFile} object which represents the the review-file of the {@link Review} given by its ID. The file and its review folder are
	 * created if they do not exist.
	 * @param reviewId
	 * @return {@link IFile} for this review
	 */
	static IFile getReviewFile(String reviewId) {
		IFile file = getReviewFolder(reviewId).getFile("review.xml");
		if (!file.exists()) {
			try {
				// TODO maybe use ProgressMonitor here?
				file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
				while (!file.exists()) {
				}
			} catch (final CoreException e) {
				ExceptionHandler.notifyUser(e);
			}
		}
		return file;
	}

	/**
	 * Deletes a {@link Review} folder (and its content).
	 * @param reviewId the ID of the review that is to be deleted within the file system.
	 * @author Peter Reuter (24.06.2012)
	 */
	static void deleteReviewContents(String reviewId) {
		IFolder reviewFolder = getReviewFolder(reviewId);
		try {
			reviewFolder.delete(true, null);
		} catch (CoreException e) {
			ExceptionHandler.notifyUser(e);
		}
	}
	
	/**
	 * Deletes a {@link Comment} file.
	 * @param reviewId The review ID to which the comment file belongs. 
	 * @param author The author of the comments contained in this file.
	 * @author Peter Reuter (18.07.2012)
	 */
	static void deleteCommentFile(String reviewId, String author) {
		IFile commentFile = getCommentFile(reviewId, author);
		try {
			commentFile.delete(true, null);
		} catch (CoreException e) {
			ExceptionHandler.notifyUser(e);
		}
	}

}
