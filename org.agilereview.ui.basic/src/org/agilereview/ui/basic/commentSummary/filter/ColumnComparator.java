/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.filter;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.commentSummary.CSTableViewer.Column;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * The class compares the comment entries of the comment summary view table
 */
public class ColumnComparator extends ViewerComparator {
    
    /**
     * {@link Column} that should be ordered
     */
    private Column sortColumn = Column.REVIEW_ID;
    /**
     * descending is defined as "1"
     */
    private static final int DESCENDING = 1;
    /**
     * standard direction is descending
     */
    private int direction = DESCENDING;
    
    /**
     * Set's the column to be ordered
     * @param column the column's index
     */
    public void setColumn(Column column) {
        if (column == sortColumn) {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        } else {
            // New column; do an ascending sort
            sortColumn = column;
            direction = DESCENDING;
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        Comment c1 = (Comment) e1;
        Comment c2 = (Comment) e2;
        int rc = 0;
        // a value < 0 is returned if c1<c2
        // a value = 0 is returned if c1=c2
        // a value > 0 is returned if c1>c2
        switch (sortColumn) {
        case REVIEW_ID:
            rc = c1.getReview().getId().compareTo(c2.getReview().getId());
            break;
        case COMMENT_ID:
            rc = c1.getId().compareTo(c2.getId());
            break;
        case AUTHOR:
            rc = c1.getAuthor().compareTo(c2.getAuthor());
            break;
        case RECIPIENT:
            rc = c1.getRecipient().compareTo(c2.getRecipient());
            break;
        case STATUS:
            rc = c1.getStatus() - c2.getStatus();
            break;
        case PRIORITY:
            rc = c1.getPriority() - c2.getPriority();
            break;
        case DATE_CREATED:
            rc = c1.getCreationDate().compareTo(c2.getCreationDate());
            break;
        case DATE_MODIFIED:
            rc = c1.getModificationDate().compareTo(c2.getModificationDate());
            break;
        case NO_REPLIES:
            rc = c1.getReplies().size() - c2.getReplies().size();
            break;
        case LOCATION:
            rc = c1.getCommentedFile().getFullPath().toOSString().compareTo(c2.getCommentedFile().getFullPath().toOSString());
            break;
        default:
            rc = 0;
        }
        // If descending order, flip the direction
        if (direction == DESCENDING) {
            rc = -rc;
        }
        return rc;
    }
}