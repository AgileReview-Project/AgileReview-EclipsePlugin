package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * The LabelProvider specifies how the nodes of the tree viewer should be displayed
 */
class RELabelProvider extends ColumnLabelProvider {
    
    /**
     * Standard grey color for displaying not existing resources
     */
    private final Color grey = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
    
    /**
     * Empty Constructor
     */
    public RELabelProvider() {
        super();
    }   
   
    /**
     * Checks whether the file represented by the given AbstractMultipleWrapper exists
     * @param o AbstractMultipleWrapper to check
     * @return <i>true</i> if the resource exits, false otherwise
     */
    private boolean exists(Object o) {
        boolean result = true;
        if (o instanceof IResource) {
            result = ((IResource) o).exists();
        }
        return result;
    }
    
    @Override
    public Image getImage(Object element) {
        Image result = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getText(element)).createImage();
        if (element instanceof Review) {
            Review currentReview = (Review) element;
            // A closed review cannot be active
            if (!currentReview.getIsOpen()) {
                result = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
           // } else if (currentReview.getId().equals(this.props.get(AgileReviewPreferences.ACTIVE_REVIEW_ID))) {
            } else if (currentReview.getIsActive()) {
            	result = Activator.getDefault().getImageRegistry().get(Activator.ISharedImages.ACTIVE_REVIEW_ICON);
            }
        } else if (element instanceof IProject) {
            result = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
        } else if (element instanceof IFolder) {
            result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        }
        
        return result;
    }
    
    @Override
    public String getText(Object element) {
        String result = "unknown object";
        if (element instanceof Review) {
            Review review = (Review) element;
            result = review.getName();
//            // Check whether this review is active
//            if (review.getIsActive()) {
//                result += " (active)";
//            }
        } else if (element instanceof IResource) {
            result = ((IResource) element).getName();
        }
        return result;
    }
    
    @Override
    public Color getForeground(Object element) {
        if (!this.exists(element)) {
            return this.grey;
        }
        return null;
    }
}