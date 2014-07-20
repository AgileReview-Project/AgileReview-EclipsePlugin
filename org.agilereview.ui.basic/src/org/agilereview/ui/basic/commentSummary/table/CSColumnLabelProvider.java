/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.tools.CommentReviewProperties;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * {@link ColumnLabelProvider} which can be adapted to the different column rendering of {@link Comment} information
 * @see ColumnLabelProvider
 * @author Malte Brunnlieb (11.05.2012)
 */
public class CSColumnLabelProvider extends ColumnLabelProvider {
    
    /**
     * Column this instance will render
     */
    private final Column column;
    /**
     * {@link CommentReviewProperties} which will be instantiated if needed in the constructor
     */
    private CommentReviewProperties commentProperties;
    
    /**
     * Creates a new {@link ColumnLabelProvider} for the specified {@link Column}
     * @param column {@link Column} which indicates the format for the correlated table column output
     * @author Malte Brunnlieb (11.05.2012)
     */
    public CSColumnLabelProvider(Column column) {
        this.column = column;
        if (column == Column.STATUS || column == Column.PRIORITY) {
            commentProperties = new CommentReviewProperties();
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
     * @author Malte Brunnlieb (11.05.2012)
     */
    @Override
    public String getText(Object element) {
        switch (column) {
        case REVIEW_NAME:
            Comment c = (Comment) element;
            return c.getReview().getName();
        case COMMENT_ID:
            c = (Comment) element;
            return c.getId();
        case AUTHOR:
            c = (Comment) element;
            return c.getAuthor();
        case RECIPIENT:
            c = (Comment) element;
            return c.getRecipient();
        case STATUS:
            c = (Comment) element;
            String status = commentProperties.getCommentStatusByID(c.getStatus());
            return status;
        case PRIORITY:
            c = (Comment) element;
            String prio = commentProperties.getCommentPriorityByID(c.getPriority());
            return prio;
        case DATE_CREATED:
            c = (Comment) element;
            DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            return df.format(c.getCreationDate().getTime());
        case DATE_MODIFIED:
            c = (Comment) element;
            if (c.getModificationDate() == null) {
                return "";
            }
            df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
            return df.format(c.getModificationDate().getTime());
        case NO_REPLIES:
            c = (Comment) element;
            return String.valueOf(c.getReplies().size());
        case LOCATION:
            c = (Comment) element;
            return c.getCommentedFile().getFullPath().toOSString();
        default:
            return "";
        }
    }
}
