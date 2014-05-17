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
import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.ICommentFilter;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.agilereview.ui.basic.tools.CommentProperties;

/**
 * Filters comments by a given search keyword (and a given column)
 * @author Malte Brunnlieb (07.06.2012)
 */
public class SearchFilter {
    
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
        this.searchString = s;
    }
    
    /**
     * @return the searchString
     * @author Malte Brunnlieb (16.03.2014)
     */
    public String getSearchString() {
        return searchString;
    }
    
    /**
     * Returns the Filter expression for the current filter
     * @return a list of {@link ICommentFilter}s for the current search filter
     * @author Malte Brunnlieb (01.06.2013)
     */
    public List<ICommentFilter> getFilterExpression() {
        List<ICommentFilter> filter = new LinkedList<ICommentFilter>();
        
        boolean matchAll = false;
        
        switch (restriction) {
        default:
            matchAll = true;
        case AUTHOR:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return comment.getAuthor().contains(searchString);
                }
            });
            if (!matchAll) break;
        case COMMENT_ID:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return comment.getId().contains(searchString);
                }
            });
            if (!matchAll) break;
        case DATE_CREATED:
            final DateFormat df1 = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return df1.format(comment.getCreationDate().getTime()).contains(searchString);
                }
            });
            if (!matchAll) break;
        case DATE_MODIFIED:
            final DateFormat df2 = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return df2.format(comment.getModificationDate().getTime()).contains(searchString);
                }
            });
            if (!matchAll) break;
        case LOCATION:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return comment.getCommentedFile().getFullPath().toOSString().contains(searchString);
                }
            });
            if (!matchAll) break;
        case NO_REPLIES:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return String.valueOf(comment.getReplies().size()).contains(searchString);
                }
            });
            if (!matchAll) break;
        case PRIORITY:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return commentProperties.getPriorityByID(comment.getPriority()).contains(searchString);
                }
            });
            if (!matchAll) break;
        case RECIPIENT:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return comment.getRecipient().contains(searchString);
                }
            });
            if (!matchAll) break;
        case REVIEW_NAME:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return comment.getReview().getName().contains(searchString);
                }
            });
            if (!matchAll) break;
        case STATUS:
            filter.add(new ICommentFilter() {
                @Override
                public boolean accept(Comment comment) {
                    return commentProperties.getStatusByID(comment.getStatus()).contains(searchString);
                }
            });
            if (!matchAll) break;
        }
        return filter;
    }
}