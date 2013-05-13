/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.util.HashSet;
import java.util.Set;

import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.core.controller.extension.EditorParserController;
import org.agilereview.core.controller.extension.ExtensionControllerFactory;
import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.core.controller.extension.StorageController;
import org.agilereview.core.external.exception.NoOpenEditorException;
import org.agilereview.core.external.exception.NullArgumentException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;

/**
 * The {@link CommentingAPI} provides the functionality to create {@link Review}s and {@link Comment}s and also delete {@link Comment}s in a proper
 * way. If you do not use this API, the {@link Comment}s will not be added to the intended editor selection.
 * @author Malte Brunnlieb (26.11.2012)
 */
public class CommentingAPI {
    
    /**
     * {@link StorageController} instance
     */
    private StorageController sController;
    /**
     * {@link EditorParserController} instance
     */
    private EditorParserController eController;
    
    /**
     * Creates a new instance of the {@link CommentingAPI}
     * @author Malte Brunnlieb (17.12.2012)
     */
    public CommentingAPI() {
        sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        eController = (EditorParserController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.EditorParser);
    }
    
    /**
     * Creates a new {@link Review} and adds it to the currently provided {@link ReviewSet}
     * @return the newly created {@link Review}
     * @author Malte Brunnlieb (17.12.2012)
     */
    public Review createReview() {
        //TODO set name initially in order to be able to display the review in the review explorer nicely
        String reviewId = sController.getNewReviewId();
        Review review = new Review(reviewId);
        sController.getAllReviews().add(review);
        return review;
    }
    
    /**
     * Creates a new {@link Comment} with the given author in the {@link Review} with the given review id.
     * @param author author of the {@link Comment}
     * @param reviewId review id of the {@link Review} the {@link Comment} should be added to
     * @return the newly created {@link Comment}
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @throws NullArgumentException if one of the arguments are passed with value <code>null</code>
     * @author Malte Brunnlieb (17.12.2012)
     */
    public Comment createComment(String author, String reviewId) throws NoOpenEditorException, NullArgumentException {
        if (author == null || reviewId == null) throw new NullArgumentException("The given arguments cannot be set to null when creating a comment");
        IEditorPart editor = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (editor == null) throw new NoOpenEditorException();
        
        IEditorPart part = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (part == null) { throw new NoOpenEditorException(); }
        IFile file = (IFile) part.getEditorInput().getAdapter(IFile.class);
        
        Review review = getReview(reviewId);
        if (review == null) { throw new NullArgumentException(
                "Comment could not be created. Currently no review data are provided by the StorageClient."); }
        String commentId = sController.getNewCommentId(author, review);
        
        Comment newComment = new Comment(commentId, file, review);
        review.addComment(newComment);
        
        eController.addTagsToEditorSelection(editor, newComment);
        return newComment;
    }
    
    /**
     * Deletes the {@link Comment} with the given comment id
     * @param commentId identifying the {@link Comment} which should be deleted
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @author Malte Brunnlieb (17.12.2012)
     */
    public void deleteComment(String commentId) throws NoOpenEditorException {
        IEditorPart part = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (part == null) { throw new NoOpenEditorException(); }
        eController.removeTags(part, commentId);
        Comment commentToDelete = getComment(commentId);
        commentToDelete.getReview().deleteComment(commentToDelete);
    }
    
    /**
     * Searches for the {@link Review} with the given id in the current set of reviews.
     * @param reviewId review id of the review which should be returned
     * @return the review with the given id<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (26.11.2012)
     */
    private Review getReview(String reviewId) {
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            if (r.getId().equals(reviewId)) { return r; }
        }
        return null;
    }
    
    /**
     * Searches for the {@link Comment} with the given id in the current {@link ReviewSet}.
     * @param commentId {@link Comment} id of the {@link Comment} which should be returned
     * @return the {@link Comment} searched for<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (17.12.2012)
     */
    private Comment getComment(String commentId) {
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                if (c.getId().equals(commentId)) { return c; }
            }
        }
        return null;
    }
}
