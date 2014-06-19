/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.agilereview.common.ui.PlatformUITools;
import org.agilereview.core.controller.extension.EditorParserController;
import org.agilereview.core.controller.extension.ExtensionControllerFactory;
import org.agilereview.core.controller.extension.ExtensionControllerFactory.ExtensionPoint;
import org.agilereview.core.controller.extension.StorageController;
import org.agilereview.core.external.exception.NoOpenEditorException;
import org.agilereview.core.external.exception.NullArgumentException;
import org.agilereview.core.external.storage.constants.ReviewSetMetaDataKeys;
import org.agilereview.core.preferences.dataprocessing.FileSupportPreferencesFactory;
import org.agilereview.fileparser.FileParser;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CommentingAPI} provides the functionality to create {@link Review}s and {@link Comment}s and also delete {@link Comment}s in a proper
 * way. If you do not use this API, the {@link Comment}s will not be added to the intended editor selection.
 * @author Malte Brunnlieb (26.11.2012)
 */
public class CommentingAPI {
    
    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(CommentingAPI.class);
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
     * Stores a meta data value into the current {@link ReviewSet}
     * @param key String identifier for the given value
     * @param value {@link Object} value to be stored
     * @author Malte Brunnlieb (02.02.2014)
     */
    public static void storeMetaValue(String key, Object value) {
        sController.getAllReviews().storeValue(key, value);
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
     * Creates a new {@link Comment} with the given author in the {@link Review} with the given review id in the current editor selection
     * @param author author of the {@link Comment}
     * @param reviewId review id of the {@link Review} the {@link Comment} should be added to
     * @return the newly created {@link Comment}
     * @throws NoOpenEditorException if no editor is currently open
     * @throws NullArgumentException if one of the arguments are passed with value <code>null</code>
     * @author Malte Brunnlieb (17.12.2012)
     */
    public static Comment createCommentInEditorSelection(String author, String reviewId) throws NoOpenEditorException, NullArgumentException {
        if (author == null || reviewId == null) { throw new NullArgumentException("The given arguments cannot be set to null when creating a comment"); }
        IEditorPart editor = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (editor == null) { throw new NoOpenEditorException(); }
        IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
        
        Review review = getReview(reviewId);
        if (review == null) { throw new NullArgumentException(
                "Comment could not be created. Currently no review data are provided by the StorageClient."); }
        
        String commentId = sController.getNewCommentId(author, review);
        Comment newComment = new Comment(commentId, file, review);
        review.addComment(newComment);
        
        eController.addTagsToEditorSelection(editor, newComment);
        sController.getAllReviews().storeValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, newComment);
        return newComment;
    }
    
    /**
     * Creates a new {@link Comment} with the given author in the {@link Review} with the given review id in for the given file.
     * @param iFile The file to which the comment will be added
     * @param startLine line number of the for comment start tag
     * @param endLine line number of the for comment end tag
     * @param author the author's name
     * @param reviewId the ID of the review to which the comment belongs
     * @return the new {@link Comment}
     * @throws IOException if the start/end tag cannot be added or the file does not exist.
     * @throws NullArgumentException if the review with the given ID cannot be found.
     * @author Malte Brunnlieb (09.06.2014)
     * @author Peter Reuter (09.06.2014)
     */
    public static Comment createComment(IFile iFile, int startLine, int endLine, String author, String reviewId) throws IOException,
            NullArgumentException {
        if (startLine < 0 || endLine < 0)
            throw new IllegalArgumentException("Start line or end line must not be < 0. Start line was " + startLine + " and end line was " + endLine
                    + ".");
        if (!iFile.exists()) throw new FileNotFoundException("The file " + iFile.getFullPath() + " could not be found in the current workspace");
        
        //get review
        Review review = getReview(reviewId);
        if (review == null) { throw new NullArgumentException(
                "Comment could not be created. Currently no review data is provided by the StorageClient."); }
        
        //create comment
        String commentId = sController.getNewCommentId(author, review);
        Comment newComment = new Comment(commentId, iFile, review);
        review.addComment(newComment);
        
        Map<String, String[]> fileSupportMap = FileSupportPreferencesFactory.createFileSupportMap();
        String fileExtension = FilenameUtils.getExtension(iFile.getName());
        String[] multiLineCommentTags = fileSupportMap.get(fileExtension);
        if (multiLineCommentTags != null) {
            FileParser fileParser = new FileParser(iFile.getLocation().toFile(), multiLineCommentTags);
            fileParser.addTags(commentId, startLine, endLine);
            refreshIFile(iFile);
        } else {
            LOG.info("File extension '{}' is currently not supported. Adding global file comment", fileExtension);
        }
        return newComment;
    }
    
    /**
     * Resynchronizes the given {@link IFile} with the file system
     * @param iFile to be refreshed
     * @author Malte Brunnlieb (09.06.2014)
     */
    private static void refreshIFile(IFile iFile) {
        try {
            iFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
        } catch (CoreException e) {
            LOG.warn("Refreshing of IFile {} failed!", iFile.getFullPath().toOSString(), e);
        }
    }
    
    /**
     * Creates a new {@link Reply} with the given id and the given parent
     * @param parent The parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
     * @return the created {@link Reply}
     */
    //TODO (MB) introduce IReplyable interface to restrict the parameter
    public static Reply createReply(Object parent) {
        if (parent == null) { throw new IllegalArgumentException("Parent object could not be null."); }
        return new Reply(sController.getNewReplyId(parent), parent);
    }
    
    /**
     * Deletes the {@link Review} with the given review id. Use this, if you do not have a reference to the real review.
     * @param reviewId identifying the {@link Review} which should be deleted
     * @throws IOException if any commented file could not be read or written
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReview(String reviewId) throws IOException {
        if (reviewId == null) { throw new IllegalArgumentException("Review id could not be null."); }
        deleteReview(getReview(reviewId));
    }
    
    /**
     * Deletes the given {@link Review}
     * @param review {@link Review} which should be deleted
     * @throws IOException if any commented file could not be read or written
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReview(Review review) throws IOException {
        if (review == null) { throw new IllegalArgumentException("Review could not be null."); }
        for (Comment c : review.getComments()) {
            deleteComment(c);
        }
        Object detail = sController.getAllReviews().getValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW);
        if (review.equals(detail)) {
            sController.getAllReviews().storeValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, null);
        }
        sController.getAllReviews().remove(review);
    }
    
    /**
     * Deletes the {@link Comment} with the given comment id. Use this, if you do not have a reference to the real comment.
     * @param commentId identifying the {@link Comment} which should be deleted
     * @throws IOException if the commented file could not be read or written
     * @author Malte Brunnlieb (17.12.2012)
     */
    public static void deleteComment(String commentId) throws IOException {
        if (commentId == null) { throw new IllegalArgumentException("Comment id could not be null."); }
        deleteComment(getComment(commentId));
    }
    
    /**
     * Deletes the given {@link Comment}
     * @param comment {@link Comment} which should be deleted
     * @throws IOException if the commented file could not be read or written
     * @author Thilo Rauch (02.11.2014)
     */
    public static void deleteComment(Comment comment) throws IOException {
        if (comment == null) { throw new IllegalArgumentException("Comment could not be null."); }
        IEditorPart part = PlatformUITools.getActiveWorkbenchPage().getActiveEditor();
        if (part != null) {
            eController.removeTags(part, comment.getId());
        } else {
            Map<String, String[]> fileSupportMap = FileSupportPreferencesFactory.createFileSupportMap();
            String[] multiLineCommentTags = fileSupportMap.get(comment.getCommentedFile().getFileExtension());
            if (multiLineCommentTags != null) {
                FileParser fileParser = new FileParser(comment.getCommentedFile().getRawLocation().toFile(), multiLineCommentTags);
                fileParser.removeTags(comment.getId());
                LOG.info("Comment with id '{}' and its tags removed.", comment.getId());
                refreshIFile(comment.getCommentedFile());
            } else {
                LOG.info(
                        "Comment with id '{}' removed. No tags were removed due to there are no multi line comment tags registered for file extension '.{}'",
                        comment.getId(), comment.getCommentedFile().getFileExtension());
            }
        }
        Object detail = sController.getAllReviews().getValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW);
        if (comment.equals(detail)) {
            sController.getAllReviews().storeValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW, null);
        }
        comment.getReview().deleteComment(comment);
    }
    
    /**
     * Deletes the {@link Reply} with the given reviewId. Use this, if you do not have a reference to the real reply.
     * @param replyId identifying the {@link Reply} which should be deleted
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReply(String replyId) {
        if (replyId == null) { throw new IllegalArgumentException("Reply id could not be null."); }
        deleteReply(getReply(replyId));
    }
    
    /**
     * Deletes the given {@link Reply}
     * @param reply {@link Reply} which should be deleted
     * @author Thilo Rauch (02.11.2013)
     */
    public static void deleteReply(Reply reply) {
        if (reply == null) { throw new IllegalArgumentException("Reply could not be null."); }
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
        if (reviewId == null) { throw new IllegalArgumentException("Review id could not be null."); }
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
    private static Comment getComment(String commentId) {
        Set<Review> reviews = new HashSet<Review>(sController.getAllReviews());
        for (Review r : reviews) {
            for (Comment c : r.getComments()) {
                if (c.getId().equals(commentId)) { return c; }
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
