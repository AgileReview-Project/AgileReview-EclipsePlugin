package org.agilereview.storage.xml.handler;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.agilereview.storage.xml.exception.ExceptionHandler;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
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
				IStructuredSelection strucSel = (IStructuredSelection)sel;
				Object o = strucSel.getFirstElement();
				if (o instanceof IProject) {
						IProject project = (IProject) o;
						if (project.hasNature(SourceFolderManager.AGILEREVIEW_NATURE)){
							IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
							preferences.put(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, project.getName());
							try {
								preferences.flush();
							} catch (BackingStoreException e) {
								String message = "AgileReview could not persistently set Review Source Folder.";
								ExceptionHandler.notifyUser(message);
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
			
			if (error) ExceptionHandler.notifyUser("Current selection is not an AgileReview Source Folder!");
			
		} catch (CoreException e) { // CoreException presented to the user via the ExecutionException
			throw new ExecutionException("Closed or non-existent project selected.", e);
		}
		return null;
	}

}