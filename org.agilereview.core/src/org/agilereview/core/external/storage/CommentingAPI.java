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
import org.agilereview.core.external.storage.constants.ReviewSetMetaDataKeys;
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
    private static final StorageController sController;
    /**
     * {@link EditorParserController} instance
     */
    private static final EditorParserController eController;
    
    /**
     * Creates a new instance of the {@link CommentingAPI}
     * @author Malte Brunnlieb (17.12.2012)
     */
    static {
        sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        eController = (EditorParserController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.EditorParser);
    }
    
    /**
     * Creates a new {@link Review} and adds it to the currently provided {@link ReviewSet}
     * @return the newly created {@link Review}
     * @author Malte Brunnlieb (17.12.2012)
     */
    public static Review createReview() {
        //TODO set name initially in order to be able to display the review in the review explorer nicely
        String reviewId = sController.getNewReviewId();
        Review review = new Review(reviewId);
        sController.getAllReviews().add(review);
        sController.getAllReviews().storeValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, review);
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
    public static Comment createComment(String author, String reviewId) throws NoOpenEditorException, NullArgumentException {
        if (author == null || reviewId == null) throw new NullArgumentException("The given arguments cannot be set to null when creating a comment");
        IEditorPart editor = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (editor == null) throw new NoOpenEditorException();
        
        IEditorPart part = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (part == null) {
            throw new NoOpenEditorException();
        }
        IFile file = (IFile) part.getEditorInput().getAdapter(IFile.class);
        
        Review review = getReview(reviewId);
        if (review == null) {
            throw new NullArgumentException("Comment could not be created. Currently no review data are provided by the StorageClient.");
        }
        
        String commentId = sController.getNewCommentId(author, review);
        Comment newComment = new Comment(commentId, file, review);
        review.addComment(newComment);
        
        eController.addTagsToEditorSelection(editor, newComment);
        sController.getAllReviews().storeValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, newComment);
        return newComment;
    }
    
    /**
     * Creates a new {@link Reply} with the given id and the given parent
     * @param id The ID of the reply
     * @param parent The parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
     */
    public static Reply createReply(Object parent) {
        return new Reply(sController.getNewReplyId(parent), parent);
    }
    
    /**
     * Deletes the {@link Review} with the given review id. Use this, if you do not have a reference to the real review.
     * @param reviewId identifying the {@link Review} which should be deleted
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReview(String reviewId) throws NoOpenEditorException {
        deleteReview(getReview(reviewId));
    }
    
    /**
     * Deletes the given {@link Review}
     * @param review {@link Review} which should be deleted
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReview(Review review) throws NoOpenEditorException {
        for (Comment c : review.getComments()) {
            deleteComment(c.getId());
        }
        sController.getAllReviews().remove(review);
    }
    
    /**
     * Deletes the {@link Comment} with the given comment id. Use this, if you do not have a reference to the real comment.
     * @param commentId identifying the {@link Comment} which should be deleted
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @author Malte Brunnlieb (17.12.2012)
     */
    public static void deleteComment(String commentId) throws NoOpenEditorException {
        deleteComment(getComment(commentId));
    }
    
    /**
     * Deletes the given {@link Comment}
     * @param commentId {@link Comment} which should be deleted
     * @throws NoOpenEditorException //TODO should be removed when tag parser for IFile has been implemented
     * @author Thilo Rauch (02.11.2014)
     */
    public static void deleteComment(Comment comment) throws NoOpenEditorException {
        IEditorPart part = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (part == null) {
            throw new NoOpenEditorException();
        }
        eController.removeTags(part, comment.getId());
        comment.getReview().deleteComment(comment);
    }
    
    /**
     * Deletes the {@link Reply} with the given reviewId. Use this, if you do not have a reference to the real reply.
     * @param replyId identifying the {@link Reply} which should be deleted
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReply(String replyId) {
        deleteReply(getReply(replyId));
    }
    
    /**
     * Deletes the given {@link Reply}
     * @param replyId {@link Reply} which should be deleted
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReply(Reply reply) {
        Object parent = reply.getParent();
        if (parent instanceof Reply) {
            ((Reply) parent).deleteReply(reply);
        } else if (parent instanceof Comment) {
            ((Comment) parent).deleteReply(reply);
        }
    }
    
    /**
     * Searches for the {@link Review} with the given id in the current set of reviews.
     * @param reviewId review id of the review which should be returned
     * @return the review with the given id<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (26.11.2012)
     */
    private static Review getReview(String reviewId) {
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            if (r.getId().equals(reviewId)) {
                return r;
            }
        }
        return null;
    }
    
    /**
     * Searches for the {@link Comment} with the given id in the current {@link ReviewSet}.
     * @param commentId {@link Comment} id of the {@link Comment} which should be returned
     * @return the {@link Comment} searched for<br><code>null</code>, otherwise
     * @author Malte Brunnlieb (17.12.2012)
     */
    private static Comment getComment(String commentId) {
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                if (c.getId().equals(commentId)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    /**
     * Searches for the {@link Reply} with the given id in the current {@link ReviewSet}.
     * @param replyId {@link Reply} id of the {@link Reply} which should be returned
     * @return the {@link Reply} searched for<br><code>null</code>, otherwise
     * @author Thilo Rauch (02.11.2013)
     */
    private static Reply getReply(String replyId) {
        // TODO: Test me hard
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                for (Reply reply : c.getReplies()) {
                    return getReply(replyId, reply);
                }
            }
        }
        return null;
    }
    
    private static Reply getReply(String replyId, Reply reply) {
        // TODO: Test me hard
        Reply result = null;
        if (reply.getId().equals(replyId)) {
            result = reply;
        } else {
            for (Reply r : reply.getReplies()) {
                result = r.getId().equals(replyId) ? r : getReply(replyId, r);
            }
        }
        return result;
    }
}
