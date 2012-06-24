package org.agilereview.storage.xml.handler;

import org.agilereview.storage.xml.Activator;
import org.agilereview.storage.xml.SourceFolderManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for activating the currently selected AgileReview Source project.
 */
public class ActivateSourceProjectHandler extends AbstractHandler {

	@SuppressWarnings("deprecation")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection sel = HandlerUtil.getCurrentSelection(event);
			if (sel instanceof StructuredSelection) {
				IStructuredSelection strucSel = (IStructuredSelection)sel;
				Object o = strucSel.getFirstElement();
				if (o instanceof IProject) {
						IProject project = (IProject) o;
						if (project.hasNature(SourceFolderManager.AGILEREVIEW_NATURE)){
							// TODO use preferences scopes here
							Activator.getDefault().getPluginPreferences().setValue(SourceFolderManager.SOURCEFOLDER_PROPERTYNAME, project.getName());
						}
				}
			}
		} catch (CoreException e) { // CoreException presented to the user via the ExecutionException
			throw new ExecutionException("Closed or non-existent project selected.", e);
		}
		return null;
	}

}