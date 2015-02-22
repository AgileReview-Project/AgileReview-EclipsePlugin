package org.agilereview.storage.xml.persistence;

import java.io.ByteArrayInputStream;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.exception.DataLoadingException;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * Manages the current source folder and exchanges of the source folder.
 * @author Peter Reuter (25.03.2012)
 */
public class SourceFolderManager {
    
    /**
     * Name of the property which stores the name of the current source folder
     */
    public static final String SOURCEFOLDER_PROPERTYNAME = "source_folder";
    /**
     * Nature of the Review Source Project
     */
    public static final String AGILEREVIEW_NATURE = "org.agilereview.storage.xml.nature.reviewsourceproject";
    /**
     * Nature of the active Review Source Project
     */
    public static final String AGILEREVIEW_ACTIVE_NATURE = "org.agilereview.storage.xml.nature.reviewsourceproject.active";
    /**
     * The {@link IProject} that represents the current source folder
     */
    private static IProject currentSourceFolder = null;
    
    /**
     * @return The {@link IProject} that represents the current source folder
     * @author Peter Reuter (25.03.2012)
     */
    public static IProject getCurrentReviewSourceProject() {
        if (currentSourceFolder == null) {
            setCurrentSourceFolderProject();
        }
        return currentSourceFolder;
    }
    
    /**
     * Returns the name of the current Review Source Project.
     * @return The name of the current Review Source Project.
     * @author Peter Reuter (26.11.2012)
     */
    public static String getCurrentReviewSourceProjectName() {
        if (currentSourceFolder == null) {
            return "null";
        } else {
            return currentSourceFolder.getName();
        }
    }
    
    /**
     * Loads the the current source folder specified by the name. The name is loaded from the preferences.
     * @author Peter Reuter (28.04.2012)
     */
    public static void setCurrentSourceFolderProject() {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        String projectName = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, "");
        if ("".equals(projectName)) {
            currentSourceFolder = null;
        } else {
            IProject p = workspaceRoot.getProject(projectName);
            if (!p.exists() || !p.isOpen()) {
                String message = "Selected Review Source Project does not exist or is not open. Please open the selected Review Source Project or create a new one.";
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
                ExceptionHandler.logAndNotifyUser(new Exception(message), Activator.PLUGIN_ID);
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
                if (currentSourceFolder != null) {
                    setProjectNatures(currentSourceFolder, new String[] { AGILEREVIEW_NATURE });
                }
                setProjectNatures(p, new String[] { AGILEREVIEW_NATURE, AGILEREVIEW_ACTIVE_NATURE });
                currentSourceFolder = p;
                if (currentSourceFolder.isSynchronized(IResource.DEPTH_INFINITE)) {
                    try {
                        currentSourceFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
                        while (!currentSourceFolder.isSynchronized(IResource.DEPTH_INFINITE)) {
                            
                        }
                    } catch (CoreException e) {
                        String message = "Error while refreshing Review Source Project '" + getCurrentReviewSourceProjectName() + "'!";
                        ExceptionHandler.logAndNotifyUser(new DataLoadingException(message), Activator.PLUGIN_ID);
                    }
                }
            }
        }
    }
    
    /**
     * Creates and opens the given Review Source Project (if not existent or closed)
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
            ExceptionHandler.logAndNotifyUser(message, e, Activator.PLUGIN_ID);
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
            ExceptionHandler.logAndNotifyUser(message, e, Activator.PLUGIN_ID);
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
     * @return {@link IFile} for the given parameter pair if a Review Source Project is available, <code>null</code> else.
     */
    public static IFile getCommentFile(String reviewId, String author) {
        if (currentSourceFolder == null) {
            return null;
        } else {
            IFile file = getReviewFolder(reviewId).getFile("author_" + author + ".xml");
            if (!file.exists()) {
                try {
                    // TODO maybe use ProgressMonitor here?
                    file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
                    while (!file.exists()) {
                    }
                } catch (final CoreException e) {
                    ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
                }
            }
            return file;
        }
    }
    
    /**
     * Returns an {@link IFolder} object which represents the folder of the {@link Review} given by its ID. The folder is created if it does not
     * exists.
     * @param reviewId
     * @return {@link IFolder} for this review if a Review Source Project is available, <code>null</code> else.
     */
    public static IFolder getReviewFolder(String reviewId) {
        if (currentSourceFolder == null) {
            return null;
        } else {
            IFolder folder = currentSourceFolder.getFolder("review." + reviewId);
            if (!folder.exists()) {
                try {
                    // TODO maybe use progressmonitor here?
                    folder.create(IResource.NONE, true, null);
                    while (!folder.exists()) {
                    }
                } catch (final CoreException e) {
                    ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
                }
            }
            return folder;
        }
    }
    
    /**
     * Returns an {@link IFile} object which represents the the review-file of the {@link Review} given by its ID. The file and its review folder are
     * created if they do not exist.
     * @param reviewId
     * @return {@link IFile} for this review if a Review Source Project is available, <code>null</code> else.
     */
    public static IFile getReviewFile(String reviewId) {
        if (currentSourceFolder == null) {
            return null;
        } else {
            IFile file = getReviewFolder(reviewId).getFile("review.xml");
            if (!file.exists()) {
                try {
                    // TODO maybe use ProgressMonitor here?
                    file.create(new ByteArrayInputStream("".getBytes()), IResource.NONE, null);
                    while (!file.exists()) {
                    }
                } catch (final CoreException e) {
                    ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
                }
            }
            return file;
        }
    }
    
    /**
     * Deletes a {@link Review} folder (and its content).
     * @param reviewId the ID of the review that is to be deleted within the file system.
     * @author Peter Reuter (24.06.2012)
     */
    public static void deleteReviewContents(String reviewId) {
        if (currentSourceFolder != null) {
            IFolder reviewFolder = getReviewFolder(reviewId);
            try {
                reviewFolder.delete(true, null);
            } catch (CoreException e) {
                ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
            }
        }
    }
    
    /**
     * Deletes a {@link Comment} file.
     * @param reviewId The review ID to which the comment file belongs.
     * @param author The author of the comments contained in this file.
     * @author Peter Reuter (18.07.2012)
     */
    public static void deleteCommentFile(String reviewId, String author) {
        if (currentSourceFolder != null) {
            IFile commentFile = getCommentFile(reviewId, author);
            try {
                commentFile.delete(true, null);
            } catch (CoreException e) {
                ExceptionHandler.logAndNotifyUser(e, Activator.PLUGIN_ID);
            }
        }
    }
    
    /**
     * @param currFolder
     * @return
     * @author Peter Reuter (03.11.2013)
     */
    static String getReviewId(IResource currFolder) {
        return ((IFolder) currFolder).getName().replace("review.", "");
    }
    
    /**
     * @param commentFile
     * @return
     * @author Peter Reuter (03.11.2013)
     */
    static boolean isAuthorFile(IResource commentFile) {
        return commentFile instanceof IFile && !commentFile.getName().equals("review.xml");
    }
    
}
