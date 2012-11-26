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
    
    public static Comment createComment(String author, Review review) throws FileNotSupportedException, EditorCurrentlyNotOpenException,
            NullArgumentException, UnknownException {
        if (review == null) throw new NullArgumentException("The Review argument cannot be set to null when creating a comment");
        
        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        IFile file = (IFile) part.getAdapter(IFile.class);
        
        StorageController sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        String commentId = sController.getNewCommentId(author, review);
        Comment newComment = new Comment(commentId, file, review);
        
        EditorParserController eController = (EditorParserController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.EditorParser);
        IEditorParser parser = eController.createParser(part.getClass());
        
        parser.addTagsToCurrentEditorSelection(commentId);
        review.addComment(newComment);
        return newComment;
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
