package org.agilereview.storage.xml.handler;

import org.agilereview.common.exception.ExceptionHandler;
import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.persistence.SourceFolderManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Handler for activating the currently selected AgileReview Source project.
 */
public class ActivateReviewSourceProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			
			ISelection sel = HandlerUtil.getCurrentSelection(event);
			
			boolean error = false;
			
			if (sel instanceof StructuredSelection) {
			
				IStructuredSelection strucSel = (IStructuredSelection) sel;
				
				Object o = strucSel.getFirstElement();
				
				if (o instanceof IProject) {
				
					IProject project = (IProject) o;
					
					if (project.hasNature(SourceFolderManager.AGILEREVIEW_NATURE)) {
						
						// remove active project natures of old Review Source Project
						String oldProjectName = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, "");
						if (!oldProjectName.equals("")) {
							IProject oldProject = ResourcesPlugin.getWorkspace().getRoot().getProject(oldProjectName);
							if (oldProject.exists()) {
								boolean wasOpen = oldProject.isOpen(); 
								if (!oldProject.isOpen()) {
									oldProject.open(null);
									while (!oldProject.isOpen()) {}
								}
								IProjectDescription projectDesc = oldProject.getDescription();
								projectDesc.setNatureIds(new String[] { SourceFolderManager.AGILEREVIEW_NATURE });
								oldProject.setDescription(projectDesc, null);
								if (!wasOpen) {
									oldProject.close(null);
								}
							}
						}
						
						// set active nature for new Review Source Project
						IProjectDescription projectDesc = project.getDescription();
						projectDesc.setNatureIds(new String[] { SourceFolderManager.AGILEREVIEW_NATURE, SourceFolderManager.AGILEREVIEW_ACTIVE_NATURE });
						project.setDescription(projectDesc, null);

						// set sourcefolder preference to selected project
						IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
						preferences.put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, project.getName());
						try {
							preferences.flush();
						} catch (BackingStoreException e) {
							String message = "AgileReview could not persistently set Review Source Project.";
							ExceptionHandler.logAndNotifyUser(new Exception(message), Activator.PLUGIN_ID);
						}
						
					} else {
						error = true;
					}
					
				} else {
					error = true;
				}
				
			} else {
				error = true;
			}

			if (error)
				ExceptionHandler.logAndNotifyUser(new Exception("Current selection is not an Review Source Project!"), Activator.PLUGIN_ID);

		} catch (CoreException e) { // CoreException presented to the user via the ExecutionException
			throw new ExecutionException("Closed or non-existent project selected.", e);
		}
		
		return null;
	}

}