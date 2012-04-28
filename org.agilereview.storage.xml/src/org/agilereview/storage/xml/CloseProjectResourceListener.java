package org.agilereview.storage.xml;

import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.agilereview.storage.xml.wizards.noreviewsource.NoReviewSourceProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * This class handles the case of closing the active review source folder.
 */
public class CloseProjectResourceListener implements IResourceChangeListener {
	
	//XXX finish refactoring of this class
	
	/**
	 * This boolean indicates whether there was a PRE_CLOSE event before a POST_BUILD
	 */
	private boolean closedBefore = false;
	/**
	 * This IPath is unequal to null iff there was a PRE_DELETE event before a POST_BUILD
	 */
	private IPath deletedProjectPath = null;
	/**
	 * Variable to save the old SourceProject between a PRE and a POST event
	 */
	private IProject oldSourceProject = null;
	
	/**
	 * Displays the NoAgileReviewSourceProject wizard
	 */
	private void showNoSourceProjectWizard() {
		// Has to be done in new thread, as the resourceChanged method blocks the building of the workspace
		Display.getDefault().syncExec(new Runnable() {								
			@Override
			public void run() {
				NoReviewSourceProjectWizard dialog = new NoReviewSourceProjectWizard();
				WizardDialog wDialog = new WizardDialog(Display.getDefault().getActiveShell(), dialog);
				wDialog.setBlockOnOpen(true);
				wDialog.open();
			}
		});
	}
	
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
				///////////////
				// PRE_CLOSE //
				///////////////
				if(event.getType() == IResourceChangeEvent.PRE_CLOSE) {
					closedBefore = true;
					oldSourceProject = SourceFolderManager.getCurrentSourceFolder();
					// Remove active nature, if needed
					if(oldSourceProject != null) {
						if (oldSourceProject.equals(event.getResource())){
							// now remove active nature
							//XXX unload current source project?
						}
					}
				}
				
				////////////////
				// POST_CLOSE //
				////////////////
				if(event.getType() == IResourceChangeEvent.POST_BUILD && closedBefore) {
					closedBefore = false;
					if(oldSourceProject != null) {
						//check whether the project was one of the closed project
						IResourceDelta[] deltaArr = event.getDelta().getAffectedChildren();
						if(deltaArr.length > 0) {
							for(IResourceDelta delta : deltaArr) {
								if (oldSourceProject.equals(delta.getResource())) {
									Display.getCurrent().syncExec( new Runnable() {
										
										@Override
										public void run() {
											String msg = "You closed the currently used Review Source Project. " +
													"Agile Review will not work without the Review Source Project.\n\n" +
													"Do you want to reopen the project the retain the full functionality of AgileReview?";
											if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Warning: AgileReview Source Project", msg)) {
												try {
													oldSourceProject.open(null); // TODO use progressmonitor?
													Activator.getDefault().getPluginPreferences().setValue(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, oldSourceProject.getName());
												} catch (final CoreException e) {
													String message = "An exception occured while reopening the closed Review Source Project "+oldSourceProject.getName()+"!"+e.getLocalizedMessage();
													Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
													ExceptionHandler.notifyUser(message, e);
												}
											} else {
												// Show NoAgileReviewSourceProject wizard
												showNoSourceProjectWizard();
											}
										}
									});
									break;
								}
							}
						}
					}
				}
				
				////////////////
				// PRE_DELETE //
				////////////////
				if(event.getType() == IResourceChangeEvent.PRE_DELETE) {
					oldSourceProject = SourceFolderManager.getCurrentSourceFolder();
					if(oldSourceProject != null) {
						if(oldSourceProject.equals(event.getResource())) {
							deletedProjectPath = oldSourceProject.getLocation();
							//XXX unload current source project?
						}
					}
				}
				
				/////////////////
				// POST_DELETE //
				/////////////////
				if(event.getType() == IResourceChangeEvent.POST_BUILD && deletedProjectPath != null) {
					if(oldSourceProject != null) {
						//check whether the project was one of the closed project
						IResourceDelta[] deltaArr = event.getDelta().getAffectedChildren();
						if(deltaArr.length > 0) {
							for(IResourceDelta delta : deltaArr) {
								if (oldSourceProject.equals(delta.getResource())) {
									Shell currentShell = Display.getDefault().getActiveShell();
									// Check in file system, if file still exists
									if (!deletedProjectPath.toFile().exists()) {
										String msg = "You deleted the current 'Agile Review Source Project' from disk.\n" +
										"Please choose an other 'AgileReview Source Project' for AgileReview to stay functional";
										MessageDialog.openWarning(currentShell, "'Agile Review Source Project' deleted", msg);
										// Show NoAgileReviewSourceProject wizard
										deletedProjectPath = null; // needed for correct wizard behavior
										showNoSourceProjectWizard();
									} else {
										String msg = "You deleted the current 'Agile Review Source Project' from your internal explorer.\n" +
												"Do you want to re-import it directly to avoid a crash of AgileReview?";
										if (MessageDialog.openQuestion(currentShell, "Warning: AgileReview Source Project", msg)) {
											try {
												IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
														new Path(deletedProjectPath+"/.project"));
												IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
												project.create(description, null); //TODO use progress monitor here and one line below?
												project.open(null);
												ra.loadReviewSourceProject(project.getName());
											} catch (final CoreException e) {
												PluginLogger.logError(this.getClass().toString(), "resourceChanged", "An exception occured while reimporting the closed source project", e);
												Display.getDefault().syncExec(new Runnable() {
													@Override
													public void run() {
														MessageDialog.openError(Display.getDefault().getActiveShell(), "AgileReview: Could not import project", e.getLocalizedMessage());
													}
												});
											}
										} else {
											// Show NoAgileReviewSourceProject wizard
											deletedProjectPath = null; // needed for correct wizard behavior
											showNoSourceProjectWizard();
										}
									}
									break;
								}
							}
						}
					}
					deletedProjectPath = null;
				}
				
				////////////////
				// POST_BUILD //
				////////////////
				if(event.getType() == IResourceChangeEvent.POST_BUILD && deletedProjectPath==null && !closedBefore) {
					for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
						try {
							if (p.hasNature(SourceFolderManager.AGILEREVIEW_ACTIVE_NATURE) && !SourceFolderManager.getCurrentSourceFolder().equals(p)) {
								ReviewAccess.setProjectNatures(p, new String[] {PropertiesManager.getInstance().getInternalProperty(PropertiesManager.INTERNAL_KEYS.AGILEREVIEW_NATURE)});
							}
						} catch (CoreException e) {/* We are not interested in closed or non existent projects*/}
					}
			}
		});
	}
}