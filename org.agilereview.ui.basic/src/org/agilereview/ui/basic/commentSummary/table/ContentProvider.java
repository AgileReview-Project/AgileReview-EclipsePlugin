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
import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewList;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

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
     * The comments to be displayed (model of TableViewer viewer)
     */
    private final List<Comment> comments = new LinkedList<Comment>();
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
                viewer = commentSummaryView.bindTableModel(instance);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(ReviewList)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void setReviewData(ReviewList reviews) {
        if (reviews == null) {
            viewer = commentSummaryView.bindTableModel(null);
        } else {
            reviews.addPropertyChangeListener(this);
            viewer = commentSummaryView.bindTableModel(this);
            comments.clear();
            for (Review r : reviews) {
                comments.addAll(r.getComments());
            }
            viewer.refresh();
        }
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
        if (viewer != null) {
            viewer.refresh();
        }
    }
}
