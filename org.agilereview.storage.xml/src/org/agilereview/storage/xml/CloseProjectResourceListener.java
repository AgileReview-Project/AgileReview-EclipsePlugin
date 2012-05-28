package org.agilereview.storage.xml;

import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class handles the case of closing the active review source folder.
 */
public class CloseProjectResourceListener implements IResourceChangeListener {

	//XXX check remaining XXX in this class

	/**
	 * This boolean indicates whether there was a PRE_CLOSE event before a POST_BUILD
	 */
	private boolean preCloseOccured = false;
	/**
	 * This IPath is unequal to null iff there was a PRE_DELETE event before a POST_BUILD
	 */
	private IPath deletedProjectPath = null;
	/**
	 * Variable to save the old SourceProject between a PRE and a POST event
	 */
	private IProject oldSourceProject = null;

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			handlePreClose(event.getResource());
		}
		if (event.getType() == IResourceChangeEvent.POST_BUILD && preCloseOccured) {
			handlePostClose(event.getDelta().getAffectedChildren());
		}
		if (event.getType() == IResourceChangeEvent.PRE_DELETE) {
			handlePreDelete(event.getResource());
		}
		if (event.getType() == IResourceChangeEvent.POST_BUILD && deletedProjectPath != null) {
			handlePostDelete(event.getDelta().getAffectedChildren());
		}
		if (event.getType() == IResourceChangeEvent.POST_BUILD && deletedProjectPath == null && !preCloseOccured) {
			handlePostBuild();
		}
	}

	/**
	 * Handles post build events.
	 * @author Peter Reuter (24.05.2012)
	 */
	private void handlePostBuild() {
		//XXX probably no longer needed --> check
//		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
//			try {
//				if (p.hasNature(SourceFolderManager.AGILEREVIEW_ACTIVE_NATURE) && !SourceFolderManager.getCurrentSourceFolder().equals(p)) {
//					
//				}
//			} catch (CoreException e) {/* We are not interested in closed or non existent projects*/
//			}
//		}
	}

	/**
	 * Handles post delete events.
	 * @param affectedChildren resource that are affected by the post delete event.
	 * @author Peter Reuter (24.05.2012)
	 */
	@SuppressWarnings("deprecation")
	private void handlePostDelete(final IResourceDelta[] affectedChildren) {
		if (oldSourceProject != null) {
			//check whether the project was one of the closed project
			if (affectedChildren.length > 0) {
				for (IResourceDelta delta : affectedChildren) {
					if (oldSourceProject.equals(delta.getResource())) {
						Shell currentShell = Display.getDefault().getActiveShell();
						String msg = "You deleted the current 'Agile Review Source Project' from disk.\n"
									+ "Please choose an other 'AgileReview Source Project' to retain the functionality of AgileReview.";
						MessageDialog.openInformation(currentShell, "'Agile Review Source Project' deleted", msg);
//						deletedProjectPath = null; // needed for correct wizard behavior
						//XXX check whether this fails --> in case, use a private function to open NoReviewSourceWizard
						Activator.getDefault().getPluginPreferences().setValue(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, null);
						break;
					}
				}
			}
		}
		deletedProjectPath = null;
	}

	/**
	 * Handles pre delete events.
	 * @param resource the {@link IResource} that is to be deleted. 
	 * @author Peter Reuter (24.05.2012)
	 */
	private void handlePreDelete(final IResource resource) {
		oldSourceProject = SourceFolderManager.getCurrentSourceFolder();
		if (oldSourceProject != null) {
			if (oldSourceProject.equals(resource)) {
				deletedProjectPath = oldSourceProject.getLocation();
				//XXX unload current source project?
			}
		}
	}

	/**
	 * Handles post close events.
	 * @param affectedChildren the children affected by the post close event
	 * @author Peter Reuter (24.05.2012)
	 */
	@SuppressWarnings("deprecation")
	private void handlePostClose(final IResourceDelta[] affectedChildren) {
		preCloseOccured = false;
		if (oldSourceProject != null) {
			//check whether the project was one of the closed project
			if (affectedChildren.length > 0) {
				for (IResourceDelta delta : affectedChildren) {
					if (oldSourceProject.equals(delta.getResource())) {
						Display.getCurrent().syncExec(new Runnable() {
							@Override
							public void run() {
								String msg = "You closed the currently used Review Source Project. "
										+ "Agile Review will not work without the Review Source Project.\n\n"
										+ "Do you want to reopen the project the retain the full functionality of AgileReview?";
								if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Warning: AgileReview Source Project", msg)) {
									try {
										oldSourceProject.open(null); // TODO use progressmonitor?
										Activator.getDefault().getPluginPreferences().setValue(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, oldSourceProject.getName());
									} catch (final CoreException e) {
										String message = "An exception occured while reopening the closed Review Source Project "
												+ oldSourceProject.getName() + "!" + e.getLocalizedMessage();
										Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
										ExceptionHandler.notifyUser(message, e);
									}
								} else {
									//XXX check whether this fails --> in case, use a private function to open NoReviewSourceWizard
									Activator.getDefault().getPluginPreferences().setValue(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, null);
								}
							}
						});
						break;
					}
				}
			}
		}
	}

	/**
	 * Handles pre close events.
	 * @param resource the {@link IResource} that is to be closed. 
	 * @author Peter Reuter (24.05.2012)
	 */
	private void handlePreClose(final IResource resource) {
		preCloseOccured = true;
		oldSourceProject = SourceFolderManager.getCurrentSourceFolder();
		// Remove active nature, if needed
		if (oldSourceProject != null) {
			if (oldSourceProject.equals(resource)) {
				SourceFolderManager.prepareCurrentSourceFolderForClosing();
			}
		}
	}
}