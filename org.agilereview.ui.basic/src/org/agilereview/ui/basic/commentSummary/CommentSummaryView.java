/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.util.LinkedList;
import java.util.List;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.commentSummary.control.ViewController;
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * {@link ViewPart} representing the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CommentSummaryView extends ViewPart implements IReviewDataReceiver {
    
    /**
     * The comments to be displayed (model of TableViewer viewer)
     */
    private List<Comment> comments;
    /**
     * The {@link ToolBar} for this ViewPart
     */
    private CSToolBar toolBar;
    /**
     * The {@link TableViewer} for this ViewPart
     */
    private CSTableViewer viewer;
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        
        CSTableViewer viewer = new CSTableViewer(parent);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(comments);
        getSite().setSelectionProvider(viewer);
        
        ColumnComparator comparator = new ColumnComparator();
        viewer.setComparator(comparator);
        
        SearchFilter commentFilter = new SearchFilter("ALL");
        viewer.addFilter(commentFilter);
        
        toolBar = new CSToolBar(parent, viewer);
        FilterController filterController = new FilterController(toolBar, viewer, commentFilter);
        toolBar.setListeners(filterController);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener("org.agilereview.ui.basic.reviewExplorerView", filterController);
        
        viewer.addDoubleClickListener(new ViewController(viewer));
        
        //add help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".TableView"); //TODO adapt help context
    }
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void setFocus() {
        toolBar.setFocus();
    }
    
    /**
     * {@inheritDoc}
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(java.util.List)
     * @author Malte Brunnlieb (10.05.2012)
     */
    @Override
    public void setReviewData(List<Review> reviews) {
        List<Comment> newComments = new LinkedList<Comment>();
        for (Review r : reviews) {
            newComments.addAll(r.getComments());
        }
        
        comments = newComments;
        if (viewer != null) {
            viewer.setInput(comments);
        }
        
        //TODO inform Parser
    }
}
