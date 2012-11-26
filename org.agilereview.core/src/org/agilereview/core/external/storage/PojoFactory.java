/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import org.agilereview.core.controller.extension.EditorParserController;
import org.agilereview.core.controller.extension.ExtensionControllerFactory;
import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.core.controller.extension.StorageController;
import org.agilereview.core.external.definition.IEditorParser;
import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.exception.EditorCurrentlyNotOpenException;
import org.agilereview.core.external.exception.FileNotSupportedException;
import org.agilereview.core.external.exception.NullArgumentException;
import org.agilereview.core.external.exception.UnknownException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Malte Brunnlieb (26.11.2012)
 */
public class PojoFactory implements IReviewDataReceiver {
    
    private static ReviewSet reviewSet = null;
    
    public static Review createReview() throws NullArgumentException {
        if (reviewSet == null)
            throw new NullArgumentException("Review could not be created. Currently no review data are provided by the StorageClient.");
        StorageController sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        String reviewId = sController.getNewReviewId();
        Review review = new Review(reviewId);
        reviewSet.add(review);
        return review;
    }
    
    public static Comment createComment(String author, String reviewId) throws FileNotSupportedException, EditorCurrentlyNotOpenException,
            NullArgumentException, UnknownException {
        if (author == null || reviewId == null) throw new NullArgumentException("The given arguments cannot be set to null when creating a comment");
        
        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (part == null) {
            throw new EditorCurrentlyNotOpenException();
        }
        IFile file = (IFile) part.getEditorInput().getAdapter(IFile.class);
        
        StorageController sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        Review review = getReview(reviewId);
        if (review == null) {
            throw new NullArgumentException("Comment could not be created. Currently no review data are provided by the StorageClient.");
        }
        String commentId = sController.getNewCommentId(author, review);
        Comment newComment = new Comment(commentId, file, review);
        
        EditorParserController eController = (EditorParserController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.EditorParser);
        IEditorParser parser = eController.createParser(part.getClass());
        
        parser.addTagsToCurrentEditorSelection(commentId);
        review.addComment(newComment);
        return newComment;
    }
    
    /**
     * Searches the Review with the given id in the current set of reviews.
     * @param reviewId review id of the review which should be returned
     * @return the review with the given id<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (26.11.2012)
     */
    private static Review getReview(String reviewId) {
        if (reviewSet == null) return null;
        for (Review r : reviewSet) {
            if (r.getId().equals(reviewId)) {
                return r;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(org.agilereview.core.external.storage.ReviewSet)
     * @author Malte Brunnlieb (26.11.2012)
     */
    @Override
    public void setReviewData(ReviewSet reviews) {
        reviewSet = reviews;
    }
    
}
