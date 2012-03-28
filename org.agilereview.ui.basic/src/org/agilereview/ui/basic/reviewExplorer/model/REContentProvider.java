package org.agilereview.ui.basic.reviewExplorer.model;

import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.storage.Review;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The ReviewExplorerContentProvider provides the content for the tree viewer of the {@link ReviewExplorer} 
 * 
 * @author Thilo Rauch (28.03.2012)
 */
public class REContentProvider implements ITreeContentProvider {
		
	/**
	 * ID of the AgileReview resource marker
	 */
	public static String AGILE_REVIEW_MARKER_ID = "org.agilereview.ui.basic.commented";
	
	@Override
	public void dispose() {/* do nothing */	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)  {/* do nothing */}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result = new Object[0];
		// Assume inputElement is a list of reviews
		if (inputElement instanceof List<?>) {
			result = ((List) inputElement).toArray();
		}
		return result;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result = new Object[0];
		// Special case: the root node
		if(parentElement instanceof Review) {
			// Check all projects
			result = filterMarkedResource(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		}  else if (parentElement instanceof IContainer) {
			try {
				result = filterMarkedResource(((IContainer) parentElement).members());
			} catch (CoreException e) {
				// if the element is not existent or not open, we also show no children (but should not happen)
			}
		}
		return result;
	}
	
	/**
	 * Only return the resources from the input which itself or one of its descendants is marked with a AgileReview marker
	 * @param input input collection
	 * @return filtered input
	 * @author Thilo Rauch (28.03.2012)
	 */
	private Object[] filterMarkedResource(IResource[] input) {
		List<IResource> result = new LinkedList<IResource>();
		for (IResource r : input) {
			IMarker[] markersOnResource;
			try {
				markersOnResource = r.findMarkers(AGILE_REVIEW_MARKER_ID, false, IResource.DEPTH_INFINITE);
				if (markersOnResource.length>0) {
					result.add(r);
				}
			} catch (CoreException e) {
				// Resource does not exist or is in an closed project --> do not add
			}
		}
		return result.toArray();
	}
		
	@Override
	public Object getParent(Object element) {
		Object result = null;
		if (element instanceof IResource) {
			result = ((IResource) element).getParent();
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean result = false;
		if (element instanceof Review) {
			result = !((Review) element).getComments().isEmpty();
		} else if (element instanceof IContainer) {
			try {
				result = ((IContainer) element).members().length > 0;
			} catch (CoreException e) {/* Then no children are available */}
		}
		return result;
	}
}