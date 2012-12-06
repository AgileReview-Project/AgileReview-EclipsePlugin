/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ContentProvider for the {@link CommentSummaryView} table
 * @author Malte Brunnlieb (27.05.2012)
 */
public class ContentProvider extends AbstractReviewDataReceiver implements IStructuredContentProvider {
    
    /**
     * Instance created by the {@link IReviewDataReceiver} extension point
     */
    private static ContentProvider instance;
    /**
     * The comments to be displayed (model of TableViewer viewer)
     */
    private final ArrayList<Comment> comments = new ArrayList<Comment>();
    
    /**
     * @return the instance
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static ContentProvider getInstance() {
        return instance;
    }
    
    /**
     * Creates a new {@link ContentProvider} instance and binds it to the {@link CommentSummaryView} if possible
     * @author Malte Brunnlieb (28.05.2012)
     */
    public ContentProvider() {
        instance = this;
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void dispose() {
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#transformData(org.agilereview.core.external.storage.ReviewSet)
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected Object transformData(ReviewSet rawData) {
        comments.clear();
        for (Review r : rawData) {
            comments.addAll(r.getComments());
        }
        return super.transformData(rawData);
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        viewer.refresh();
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
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#getReviewDataViewClass()
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataView> getReviewDataViewClass() {
        return CommentSummaryView.class;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#triggerPropertyChange(java.beans.PropertyChangeEvent, org.agilereview.core.external.storage.ReviewSet)
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected boolean triggerPropertyChange(PropertyChangeEvent evt, ReviewSet data) {
        return true;
    }
}
