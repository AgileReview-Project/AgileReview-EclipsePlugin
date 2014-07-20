/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.preferences;

import org.agilereview.core.Activator;
import org.agilereview.core.external.storage.Review;

/**
 * The {@link AgileReviewPreferences} holds preferences IDs for external access
 * @author Malte Brunnlieb (28.04.2012)
 */
public interface AgileReviewPreferences {
    
    /**
     * Plugin ID made available for other plugins to read/write preferences for this plugin
     */
    public static final String CORE_PLUGIN_ID = Activator.PLUGIN_ID;
    /**
     * Comma separated list of comment status
     */
    public static final String COMMENT_STATUS = "org.agilereview.preferences.comment_status";
    /**
     * Comma separated list of comment priorities
     */
    public static final String COMMENT_PRIORITIES = "org.agilereview.preferences.comment_priorities";
    /**
     * Comma separated list of review status
     */
    public static final String REVIEW_STATUS = "org.agilereview.preferences.review_status";
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
     * Author color allocation encoded in JSON syntax as follows:<br>{ 'IDEUser':'hans', 'Author2':'hugo', 'Author3':'erwin', 'Author4':'',
     * 'Author5':'', 'Author6':'', 'Author7':'', 'Author8':'', 'Author9':'', 'Author10':'' }
     */
    public static final String AUTHOR_COLOR_ALLOCATION = "org.agilereview.preferences.author_color_allocation";
    
    /**
     * Default comment color if multi color support is disabled (format: R,G,B)
     */
    public static final String AUTHOR_COLOR_DEFAULT = "org.agilereview.preferences.author_color";
    
    /**
     * Author colors (format: R,G,B)
     */
    public static final String[] AUTHOR_COLORS = { "org.agilereview.preferences.author_color_IDEUser",
            "org.agilereview.preferences.author_color_Author2", "org.agilereview.preferences.author_color_Author3",
            "org.agilereview.preferences.author_color_Author4", "org.agilereview.preferences.author_color_Author5",
            "org.agilereview.preferences.author_color_Author6", "org.agilereview.preferences.author_color_Author7",
            "org.agilereview.preferences.author_color_Author8", "org.agilereview.preferences.author_color_Author9",
            "org.agilereview.preferences.author_color_Author10" };
    
    /**
     * A Boolean value which states whether the author should be distinguishable by their comment color or not
     */
    public static final String AUTHOR_COLORS_ENABLED = "org.agilereview.preferences.author_colors_enabled";
    
}
