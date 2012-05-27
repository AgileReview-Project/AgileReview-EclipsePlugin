/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.table;

import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ContentProvider for the {@link CommentSummaryView} table
 * @author Malte Brunnlieb (27.05.2012)
 */
public class TableContentProvider implements IContentProvider, IReviewDataReceiver {
    
    private static TableContentProvider instance;
    /**
     * The comments to be displayed (model of TableViewer viewer)
     */
    private List<Comment> comments;
    /**
     * Current {@link CommentSummaryView} instance
     */
    private static CommentSummaryView commentSummaryView;
    
    public TableContentProvider() {
        instance = this;
        if (commentSummaryView != null) {
            commentSummaryView.bindTableModel(this);
        }
    }
    
    /**
     * Binds the {@link CommentSummaryView} as the content receiver for persistence data
     * @param view {@link CommentSummaryView} which contains the table viewer
     * @author Malte Brunnlieb (27.05.2012)
     */
    public static void bind(CommentSummaryView view) {
        commentSummaryView = view;
        if (instance != null) {
            commentSummaryView.bindTableModel(instance);
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(java.util.List)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void setReviewData(List<Review> reviews) {
        List<Comment> newComments = new LinkedList<Comment>();
        for (Review r : reviews) {
            newComments.addAll(r.getComments());
        }
        comments = newComments;
        
        //TODO inform Parser
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void dispose() {
        if (commentSummaryView != null) {
            commentSummaryView.bindTableModel(null);
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO check if implementation is necessary
    }
}
