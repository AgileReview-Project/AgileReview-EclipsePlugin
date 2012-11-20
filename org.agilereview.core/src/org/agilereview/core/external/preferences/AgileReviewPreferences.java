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
    public static final String COMMENT_STATUS = "org.agilereview.preferences.comment_status";
    /**
     * Comma separated list of comment priorities
     */
    public static final String COMMENT_PRIORITIES = "org.agilereview.preferences.comment_priorities";
    /**
     * id of the currently active review
     */
    public static final String ACTIVE_REVIEW_ID = "org.agilereview.preferences.active_review_id";
    /**
     * Name of the current user
     */
    public static final String AUTHOR = "org.agilereview.preferences.author";
    /**
     * Comma separated list of open reviews, do not modify by hand, handled by {@link Review#setIsOpen(boolean)}
     */
    public static final String OPEN_REVIEWS = "org.agilereview.preferences.open_reviews";
    // TODO add possibility for storing open reviews of multiple storageclients using e.g. JSON syntax
    
    /**
     * Supported files an its comment tags encoded in JSON syntax as follows:<br> [{'commentTags':['/*','*\/'],'fileendings':['java']},
     * {'commentTags':['<!--','-->'],'fileendings':['xml','xaml']}]
     */
    public static final String SUPPORTED_FILES = "org.agilereview.preferences.supported_files";
    
    /**
     * Author colors encoded in JSON syntax as follows:<br>{ 'IDEUser':'R,G,B', 'Author2':'R,G,B', 'Author3':'R,G,B', 'Author4':'R,G,B',
     * 'Author5':'R,G,B', 'Author6':'R,G,B', 'Author7':'R,G,B', 'Author8':'R,G,B', 'Author9':'R,G,B', 'Author10':'R,G,B' }
     */
    public static final String AUTHOR_COLORS = "org.agilereview.preferences.author_colors";
    
    /**
     * Author color allocation encoded in JSON syntax as follows:<br>{ 'IDEUser':'hans', 'Author2':'hugo', 'Author3':'erwin', 'Author4':'',
     * 'Author5':'', 'Author6':'', 'Author7':'', 'Author8':'', 'Author9':'', 'Author10':'' }
     */
    public static final String AUTHOR_COLOR_ALLOCATION = "org.agilereview.preferences.author_color_allocation";
    
}
