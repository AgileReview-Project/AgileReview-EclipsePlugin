/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * ContentProvider for the {@link CommentSummaryView} table
 * @author Malte Brunnlieb (27.05.2012)
 */
public class ContentProvider implements IStructuredContentProvider, IReviewDataReceiver, PropertyChangeListener {

    /**
     * Instance created by the {@link IReviewDataReceiver} extension point
     */
    private static ContentProvider instance;
    /**
     * ReviewList provided by the currently active StorageClient
     */
    private static ReviewSet reviewList;
    /**
     * The comments to be displayed (model of TableViewer viewer)
     */
    private final ArrayList<Comment> comments = new ArrayList<Comment>();
    /**
     * {@link TableViewer} this model has been bound to
     */
    private static TableViewer viewer;
    /**
     * Current {@link CommentSummaryView} instance
     */
    private static CommentSummaryView commentSummaryView;
    /**
     * Object in order to synchronize model <-> table binding
     */
    private static Object syncObj = new Object();

    /**
     * Creates a new {@link ContentProvider} instance and binds it to the {@link CommentSummaryView} if possible
     * @author Malte Brunnlieb (28.05.2012)
     */
    public ContentProvider() {
        synchronized (syncObj) {
            instance = this;
            if (commentSummaryView != null) {
                viewer = commentSummaryView.bindTableModel(this);
            }
        }
    }

    /**
     * Binds the {@link CommentSummaryView} as the content receiver for persistence data
     * @param view {@link CommentSummaryView} which contains the table viewer
     * @author Malte Brunnlieb (27.05.2012)
     */
    public static void bind(CommentSummaryView view) {
        synchronized (syncObj) {
            commentSummaryView = view;
            if (instance != null) {
                if (reviewList != null) {
                    viewer = commentSummaryView.bindTableModel(instance);
                } else {
                    viewer = commentSummaryView.bindTableModel(null);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(ReviewSet)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void setReviewData(ReviewSet reviews) {
        reviewList = reviews;
        if (reviews == null) {
            if (commentSummaryView != null) {
                viewer = commentSummaryView.bindTableModel(null);
            }
        } else {
            reviews.addPropertyChangeListener(this);
            if (commentSummaryView != null) {
                viewer = commentSummaryView.bindTableModel(this);
            }
            refreshCommentList();
        }
        //TODO inform Parser
    }

    /**
     * Extracts all {@link Comment}s out of the {@link ReviewSet} and refreshes the viewer
     * @author Malte Brunnlieb (03.06.2012)
     */
    private void refreshCommentList() {
        if (reviewList != null) {
            comments.clear();
            for (Review r : reviewList) {
                comments.addAll(r.getComments());
            }
            refreshViewer();
        }
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
        //        viewer.setInput(newInput);
        //        viewer.refresh();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @author Malte Brunnlieb (28.05.2012)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return comments.toArray(new Comment[0]);
    }

    /**
     * {@inheritDoc}
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     * @author Malte Brunnlieb (03.06.2012)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        refreshCommentList();
    }

    /**
     * Refreshes the registered viewer. Does not check whether viewer is != null
     * @author Malte Brunnlieb (03.06.2012)
     */
    private void refreshViewer() {
        if (viewer != null) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    viewer.setInput(comments);
                }
            });
        }
    }
}
