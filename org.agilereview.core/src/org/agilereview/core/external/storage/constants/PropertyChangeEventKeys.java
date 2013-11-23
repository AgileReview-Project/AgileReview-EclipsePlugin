/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage.constants;

import java.beans.PropertyChangeEvent;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Reply;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * {@link PropertyChangeEvent} keys for the ReviewSet
 * @author Malte Brunnlieb (23.11.2013)
 */
public final class PropertyChangeEventKeys {
    
    /**
     * {@link PropertyChangeEvent} key for changes in the set of reviews hold by the {@link ReviewSet}
     */
    public final static String REVIEWSET_REVIEWS = "ReviewSet_reviews";
    
    /**
     * {@link PropertyChangeEvent} key for changes in the metaData of the {@link ReviewSet}
     */
    public final static String REVIEWSET_METADATA = "ReviewSet_metaData";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the name of a {@link Review}
     */
    public final static String REVIEW_NAME = "Review_name";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the status of a {@link Review}
     */
    public final static String REVIEW_STATUS = "Review_status";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the reference of a {@link Review}
     */
    public final static String REVIEW_REFERENCE = "Review_reference";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the responsibility of a {@link Review}
     */
    public final static String REVIEW_RESPONSIBILITY = "Review_responsibility";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the comments of a {@link Review}
     */
    public final static String REVIEW_COMMENTS = "Review_comments";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the isOpen status of a {@link Review}
     */
    public final static String REVIEW_ISOPEN_STATUS = "Review_isOpenStatus";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the description of a {@link Review}
     */
    public final static String REVIEW_DESCRIPTION = "Review_description";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the isActive status of a {@link Review}
     */
    public final static String REVIEW_ISACTIVE_STATUS = "Review_isActiveStatus";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the commented file reference of a {@link Comment}
     */
    public final static String COMMENT_COMMENTED_FILE_REFERENCE = "Comment_commentedFile";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the modification date of a {@link Comment}
     */
    public final static String COMMENT_MODIFICATION_DATE = "Comment_modificationDate";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the recipient of a {@link Comment}
     */
    public final static String COMMENT_RECIPIENT = "Comment_recipient";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the status of a {@link Comment}
     */
    public final static String COMMENT_STATUS = "Comment_status";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the priority of a {@link Comment}
     */
    public final static String COMMENT_PRIORITY = "Comment_priority";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the text of a {@link Comment}
     */
    public final static String COMMENT_TEXT = "Comment_text";
    
    /**
     * {@link PropertyChangeEvent} key for changes in the set of replies of a {@link Comment}
     */
    public final static String COMMENT_REPLIES = "Comment_replies";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the modification date of a {@link Reply}
     */
    public final static String REPLY_MODIFICATION_DATE = "Reply_modificationDate";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the text of a {@link Reply}
     */
    public final static String REPLY_TEXT = "Reply_text";
    
    /**
     * {@link PropertyChangeEvent} key for changes of the set of replies of a {@link Reply}
     */
    public final static String REPLY_REPLIES = "Reply_replies";
    
}
