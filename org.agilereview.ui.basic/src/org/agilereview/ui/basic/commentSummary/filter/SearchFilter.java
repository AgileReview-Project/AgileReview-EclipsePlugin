/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters comments by a given search keyword (and a given column)
 * @author Malte Brunnlieb (07.06.2012)
 */
public class SearchFilter extends ViewerFilter {
    
    /**
     * search word by which comments should be filtered
     */
    private String searchString;
    /**
     * Category to be searched in. For no restriction (search in every column) pass null
     */
    private final Column restriction;
    /**
     * Property access point for comments
     */
    private final CommentProperties commentProperties;
    
    /**
     * Constructor of the filter, used to set initial restrictions on category
     * @param restriction a {@link Column} or null for search in all
     * @author Malte Brunnlieb (07.06.2012)
     */
    public SearchFilter(Column restriction) {
        if (restriction == null) {
            this.restriction = Column.NULL;
        } else {
            this.restriction = restriction;
        }
        commentProperties = new CommentProperties();
    }
    
    /**
     * Sets the search text for this filter
     * @param s text to be searched
     * @author Malte Brunnlieb (07.06.2012)
     */
    public void setSearchText(String s) {
        this.searchString = ".*" + Pattern.quote(s) + ".*";
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        Comment c = (Comment) element;
        if (searchString == null || searchString.length() == 0) { return true; }
        
        switch (restriction) {
        default:
        case AUTHOR:
            if (c.getAuthor().matches(searchString)) { return true; }
        case COMMENT_ID:
            if (c.getId().matches(searchString)) { return true; }
        case DATE_CREATED:
            DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            if (df.format(c.getCreationDate().getTime()).matches(searchString)) { return true; }
        case DATE_MODIFIED:
            df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            if (df.format(c.getModificationDate().getTime()).matches(searchString)) { return true; }
        case LOCATION:
            if (c.getCommentedFile().getFullPath().toOSString().matches(searchString)) { return true; }
        case NO_REPLIES:
            if (String.valueOf(c.getReplies().size()).matches(searchString)) { return true; }
        case PRIORITY:
            if (commentProperties.getPriorityByID(c.getPriority()).matches(searchString)) { return true; }
        case RECIPIENT:
            if (c.getRecipient().matches(searchString)) { return true; }
        case REVIEW_ID:
            if (c.getReview().getId().matches(searchString)) { return true; }
        case STATUS:
            if (commentProperties.getStatusByID(c.getStatus()).matches(searchString)) { return true; }
        }
        
        return false;
    }
}