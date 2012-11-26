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
import org.agilereview.core.external.exception.EditorCurrentlyNotOpenException;
import org.agilereview.core.external.exception.FileNotSupportedException;
import org.agilereview.core.external.exception.UnknownException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Malte Brunnlieb (26.11.2012)
 */
public class CommentFactory {
    
    public static Review createReview() {
        StorageController sController = (StorageController) ExtensionControllerFactory.getExtensionController(ExtensionPoint.StorageClient);
        String reviewId = sController.getNewReviewId();
        return new Review(reviewId);
    }
    
    public static Comment createComment(String author, Review review) throws FileNotSupportedException, EditorCurrentlyNotOpenException,
            UnknownException {
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
    
}
