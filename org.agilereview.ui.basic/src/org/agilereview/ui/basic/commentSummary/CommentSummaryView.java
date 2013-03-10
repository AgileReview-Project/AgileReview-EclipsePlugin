/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.commentSummary.control.ViewController;
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.agilereview.ui.basic.commentSummary.table.ContentProvider;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.part.ViewPart;

/**
 * {@link ViewPart} representing the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CommentSummaryView extends AbstractReviewDataView {
    
    /**
     * Unique instance of the {@link CommentSummaryView}
     */
    private static CommentSummaryView instance = null;
    /**
     * The {@link ToolBar} for this ViewPart
     */
    private CSToolBar toolBar;
    /**
     * The {@link TableViewer} for this ViewPart
     */
    private CSTableViewer viewer;
    /**
     * The {@link FilterController} which manages all filter actions
     */
    private FilterController filterController;
    
    /**
     * Creates a new {@link CommentSummaryView}
     * @author Malte Brunnlieb (06.12.2012)
     */
    public CommentSummaryView() {
        instance = this;
    }
    
    /**
     * Returns the unique instance created by the framework
     * @return the instance created by the framework
     * @author Malte Brunnlieb (06.12.2012)
     */
    public static CommentSummaryView getInstance() {
        return instance;
    }
    
    /**
     * {@inheritDoc} Also removes selection listeners
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     * @author Malte Brunnlieb (27.05.2012)
     */
    @Override
    public void dispose() {
        if (filterController != null) {
            getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener("org.agilereview.ui.basic.reviewExplorerView",
                    filterController);
        }
        super.dispose();
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void setFocus() {
        if (toolBar != null) {
            toolBar.setFocus();
        }
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#getReviewDataReceiverClass()
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataReceiver> getReviewDataReceiverClass() {
        return ContentProvider.class;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#buildUI(org.eclipse.swt.widgets.Composite, java.lang.Object)
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected void buildUI(Composite parent, Object initalInput) {
        parent.setLayout(new GridLayout());
        toolBar = new CSToolBar(parent);
        
        viewer = new CSTableViewer(parent);
        ContentProvider cp = ContentProvider.getInstance();
        viewer.setContentProvider(cp);
        ColumnComparator comparator = new ColumnComparator();
        viewer.setComparator(comparator);
        SearchFilter commentFilter = new SearchFilter(null);
        viewer.addFilter(commentFilter);
        
        viewer.addDoubleClickListener(new ViewController(viewer));
        getSite().setSelectionProvider(viewer);
        
        filterController = new FilterController(toolBar, viewer, commentFilter, cp.getReviewSet());
        toolBar.setListeners(filterController);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener("org.agilereview.ui.basic.reviewExplorerView", filterController);
        parent.layout();
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView#refreshInput(java.lang.Object)
     * @author Malte Brunnlieb (06.12.2012)
     */
    @Override
    protected void refreshInput(Object reviewData) {
        viewer.setInput(reviewData);
    }
    
}
