package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.tools.Preferences;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The LabelProvider specifies how the nodes of the tree viewer should be displayed
 */
class RELabelProvider extends ColumnLabelProvider {
    
    /**
     * Standard grey color for displaying not exisitng resources
     */
    private final Color grey = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
    /**
     * Preference query interface of the core plugin
     */
    private final Preferences props = new Preferences();
    
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
            } else if (currentReview.getId().equals(this.props.get(AgileReviewPreferences.ACTIVE_REVIEW_ID))) {
                String activeReviewIconPath = Activator.getDefault().getInternalProperty(Activator.Keys.ACTIVE_REVIEW_ICON);
                result = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, activeReviewIconPath).createImage();
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
            result = review.getId();
            // Check whether this review is active
            if (review.getId().equals(this.props.get(AgileReviewPreferences.ACTIVE_REVIEW_ID))) {
                result += " (active)";
            }
        } else if (element instanceof IResource) {
            result = ((IResource) element).getName();
        }
        return result;
    }
    
    @Override
    public Color getForeground(Object element) {
        if (!this.exists(element)) { return this.grey; }
        return null;
    }
}