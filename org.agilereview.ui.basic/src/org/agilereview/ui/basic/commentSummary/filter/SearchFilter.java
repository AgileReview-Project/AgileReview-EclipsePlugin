/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.filter;

import java.util.regex.Pattern;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters comments by a given searchword (and a given category)
 */
public class SearchFilter extends ViewerFilter {
    
    /**
     * searchword by which comments should be filtered
     */
    private String searchString;
    /**
     * Category to be searched ('ALL' or the category's name)
     */
    private final String restriction;
    /**
     * Property access point for comments
     */
    private final CommentProperties commentProperties;
    
    /**
     * Constructor of the filter, used to set initial restrictions on category
     * @param restriction
     */
    public SearchFilter(String restriction) {
        this.restriction = restriction;
        commentProperties = new CommentProperties();
    }
    
    /**
     * Append wildcards to search string
     * @param s
     */
    public void setSearchText(String s) {
        // Search must be a substring of the existing value
        this.searchString = ".*" + Pattern.quote(s) + ".*";
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean matches = false;
        if (searchString == null || searchString.length() == 0) {
            matches = true;
        } else {
            Comment c = (Comment) element;
            // match in ReviewID and category ALL or ReviewID
            if (c.getReview().getId().matches(searchString) && (restriction.equals("ALL") || restriction.equals("ReviewID"))) {
                matches = true;
            }
            // match in CommentID and category ALL or CommentID
            if (c.getId().matches(searchString) && (restriction.equals("ALL") || restriction.equals("CommentID"))) {
                matches = true;
            }
            // match in Author and category ALL or Author
            if (c.getAuthor().matches(searchString) && (restriction.equals("ALL") || restriction.equals("Author"))) {
                matches = true;
            }
            // match in Recipient and category ALL or Recipient
            if (c.getRecipient().matches(searchString) && (restriction.equals("ALL") || restriction.equals("Recipient"))) {
                matches = true;
            }
            // match in Status and category ALL or Status
            if (commentProperties.getStatusByID(c.getStatus()).matches(searchString) && (restriction.equals("ALL") || restriction.equals("Status"))) {
                matches = true;
            }
            // match in Priority and category ALL or Priority
            if (commentProperties.getPriorityByID(c.getPriority()).matches(searchString) && (restriction.equals("ALL") || restriction.equals("Priority"))) {
                matches = true;
            }
            // match in CreationDate and category ALL or 'Date created'
            if (c.getCreationDate().toString().matches(searchString) && (restriction.equals("ALL") || restriction.equals("Date created"))) {
                matches = true;
            }
            // match in LastModified and category ALL or 'Date modified'
            if (c.getModificationDate().toString().matches(searchString) && (restriction.equals("ALL") || restriction.equals("Date modified"))) {
                matches = true;
            }
            // match in # of replies and category ALL or Replies
            if (String.valueOf(c.getReplies().size()).matches(searchString) && (restriction.equals("ALL") || restriction.equals("Replies"))) {
                matches = true;
            }
            // match in Path and category ALL or Location
            if (c.getCommentedFile().getFullPath().toOSString().matches(searchString)
                    && (restriction.equals("ALL") || restriction.equals("Location"))) {
                matches = true;
            }
        }
        
        return matches;
    }
}