package org.agilereview.ui.basic.reviewExplorer;

import org.agilereview.common.preferences.PreferencesAccessor;
import org.agilereview.core.external.preferences.AgileReviewPreferences;
import org.agilereview.core.external.storage.Review;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Comparator which determines the ordering of elements in the Review Explorer. Reviews are firstly ordered by category (category=1 if review is
 * closed, category=0 otherwise) and then based on their names (except for the active review, which is always on top), all other elements are directly
 * ordered by their names
 */
class REViewerComparator extends ViewerComparator {

    @Override
    public int category(Object element) {
        int result = 0;
        // Set category only if element is a ReviewWrapper
        if (element instanceof Review) {
            Review review = (Review) element;
            // Closed reviews are category 1 -> sorted below open reviews
            if (!review.getIsOpen()) {
                result = 1;
            }
        } else if (element instanceof IFolder) {
            result = 2;
        } else if (element instanceof IFile) {
            result = 3;
        }
        // Sorting order by category is then: Reviews, Folder, Files
        return result;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = super.compare(viewer, e1, e2);
        if (e1 instanceof Review && e2 instanceof Review) {
            String activeReview = new PreferencesAccessor().get(AgileReviewPreferences.ACTIVE_REVIEW_ID);
            if (activeReview.equals(((Review) e1).getId())) {
                result = -1;
            } else if (activeReview.equals(((Review) e2).getId())) {
                result = 1;
            }
        }

        return result;
    }
}