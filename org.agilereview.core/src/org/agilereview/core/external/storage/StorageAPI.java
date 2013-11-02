/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

import java.util.Calendar;

import org.agilereview.core.external.definition.IStorageClient;
import org.eclipse.core.resources.IFile;

/**
 * The {@link StorageAPI} provides the functionality to create and delete {@link Review}s and {@link Comment}s for Storage Clients. The difference to
 * {@link CommentingAPI} is, that no UI interactions are done here.
 * @author Thilo Rauch (02.11.2013)
 */
public class StorageAPI {
    
    /**
     * Creates a new comment without connections and UI interactions to rebuild a comment structure
     * @param id the ID of the comment retrieved from the current {@link IStorageClient}
     * @param author the author of the comment
     * @param commentedFile the {@link IFile} underlying the editor in which the comment was added
     * @param review the {@link Review} the comment is related to
     * @param creationDate the date when the comment was initially created
     * @param modificationDate the date when the comment was lastly modified
     * @param recipient the person the comment is addressed to
     * @param status the current status of the comment
     * @param priority the priority of the comment
     * @param text the text of the comment
     * @return newly created comment
     * @author Thilo Rauch (02.11.2013)
     */
    public static Comment createComment(String id, String author, IFile commentedFile, Review review, Calendar creationDate,
            Calendar modificationDate, String recipient, int status, int priority, String text) {
        return new Comment(id, author, commentedFile, review, creationDate, modificationDate, recipient, status, priority, text);
    }
    
    /**
     * Creates a new {@link Review} without connections and UI interactions to rebuild a comment structure
     * @param id the id of the {@link Review}
     * @param name the name of the {@link Review}
     * @param status the status of the {@link Review}
     * @param reference e.g. a reference to a bug tracker
     * @param responsibility the person that is in charge for this {@link Review}
     * @param description a text describing the e.g. the content or scope of this {@link Review}
     */
    public static Review createReview(String id, String name, int status, String reference, String responsibility, String description) {
        return new Review(id, name, status, reference, responsibility, description);
    }
    
    /**
     * Creates a new {@link Reply} without connections and UI interactions to rebuild a comment structure
     * @param id the ID of the reply
     * @param author the author of the reply
     * @param creationDate the date when the reply was create initially
     * @param modificationDate date when the reply was create lastly
     * @param text the text of the reply
     * @param parent the parent {@link Object} of this {@link Reply}, either a {@link Comment} or another {@link Reply}
     */
    public static Reply createReply(String id, String author, Calendar creationDate, Calendar modificationDate, String text, Object parent) {
        return new Reply(id, author, creationDate, modificationDate, text, parent);
    }
}
