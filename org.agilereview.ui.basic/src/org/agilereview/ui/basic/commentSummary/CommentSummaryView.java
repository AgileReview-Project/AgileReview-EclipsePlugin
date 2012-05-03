/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.util.ArrayList;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.commentSummary.control.ViewController;
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * {@link ViewPart} representing the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CommentSummaryView extends ViewPart {
    
    /**
     * The comments to be displayed (model of TableViewer viewer)
     */
    private ArrayList<Comment> comments;
    
    /*
    * (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    * @author Malte Brunnlieb (08.04.2012)
    */
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        
        CSTableViewer viewer = new CSTableViewer(parent);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(comments); //TODO set in model
        getSite().setSelectionProvider(viewer);
        ColumnComparator comparator = new ColumnComparator();
        viewer.setComparator(comparator);
        SearchFilter commentFilter = new SearchFilter("ALL");
        viewer.addFilter(commentFilter);
        
        CSToolBar toolBar = new CSToolBar(parent, viewer);
        FilterController filterController = new FilterController(toolBar, viewer, commentFilter);
        toolBar.setListeners(filterController);
        
        ViewController viewController = new ViewController(viewer);
        viewer.addSelectionChangedListener(viewController);
        viewer.addDoubleClickListener(viewController);
        
        //add help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".TableView"); //TODO adapt help context
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }
}
