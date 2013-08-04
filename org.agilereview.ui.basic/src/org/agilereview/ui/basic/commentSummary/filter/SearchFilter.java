/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.filter;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.containsString;

import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.hamcrest.Matcher;
import org.hamcrest.core.AnyOf;

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
     * Returns the Filter expression for the current filter
     * @return {@link Matcher} for the current search filter
     * @author Malte Brunnlieb (01.06.2013)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Matcher<Object> getFilterExpression() {
        List<Matcher<Object>> matchers = new LinkedList<Matcher<Object>>();
        boolean matchAll = false;
        
        switch (restriction) {
        default:
            matchAll = true;
        case AUTHOR:
            matchers.add(having(on(Comment.class).getAuthor(), containsString(searchString)));
            if (!matchAll) break;
        case COMMENT_ID:
            matchers.add(having(on(Comment.class).getId(), containsString(searchString)));
            if (!matchAll) break;
        case DATE_CREATED:
            //TODO lambdaj problem
            //                        DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            //                        matchers.add(having(df.format(on(Comment.class).getCreationDate().getTime()), containsString(searchString)));
            if (!matchAll) break;
        case DATE_MODIFIED:
            //TODO lambdaj problem
            //                        df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            //                        matchers.add(having(df.format(on(Comment.class).getModificationDate().getTime()), containsString(searchString)));
            if (!matchAll) break;
        case LOCATION:
            matchers.add(having(on(Comment.class).getCommentedFile().getFullPath().toOSString(), containsString(searchString)));
            if (!matchAll) break;
        case NO_REPLIES:
            //TODO lambdaj problem
            //            matchers.add(having(String.valueOf(on(Comment.class).getReplies().size()), containsString(searchString)));
            if (!matchAll) break;
        case PRIORITY:
            //TODO lambdaj problem
            //            matchers.add(having(commentProperties.getPriorityByID(on(Comment.class).getPriority()), containsString(searchString)));
            if (!matchAll) break;
        case RECIPIENT:
            matchers.add(having(on(Comment.class).getRecipient(), containsString(searchString)));
            if (!matchAll) break;
        case REVIEW_ID:
            matchers.add(having(on(Comment.class).getReview().getId(), containsString(searchString)));
            if (!matchAll) break;
        case STATUS:
            //TODO lambdaj problem
            //            matchers.add(having(commentProperties.getStatusByID(on(Comment.class).getStatus()), containsString(searchString)));
            if (!matchAll) break;
        }
        
        return new AnyOf(matchers);
    }
}