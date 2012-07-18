/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.preferences;

import org.agilereview.core.external.storage.Review;

/**
 * The {@link AgileReviewPreferences} holds preferences IDs for external access
 * @author Malte Brunnlieb (28.04.2012)
 */
public interface AgileReviewPreferences {
    
    /**
     * Comma separated list of comment status
     */
    public static final String COMMENT_STATUS = "org.agilereview.comment_status";
    /**
     * Comma separated list of comment priorities
     */
    public static final String COMMENT_PRIORITIES = "org.agilereview.comment_priorities";
    /**
     * id of the currently active review
     */
    public static final String ACTIVE_REVIEW_ID = "org.agilereview.active_review_id";
    /**
     * Name of the current user 
     */
    public static final String AUTHOR = "org.agilereview.author";
    /**
     * Comma separated list of open reviews, do not modify by hand, handled by {@link Review#setIsOpen(boolean)}
     */
    public static final String OPEN_REVIEWS = "org.agilereview.open_reviews";
    // TODO add possibility for storing open reviews of multiple storageclients using e.g. JSON syntax
    
}
