/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.control;

import org.agilereview.ui.basic.commentSummary.CSTableViewer;
import org.agilereview.ui.basic.commentSummary.CommentSummaryView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * This {@link ViewController} handles all events related to the CommentSummaryView
 * @author Malte Brunnlieb (28.04.2012)
 */
public class ViewController implements ISelectionChangedListener, IDoubleClickListener {
    
    /**
     * TableViewer which events should be handled
     */
    private final CSTableViewer tableViewer;
    
    /**
     * Creates a new instance of {@link ViewController} for a given {@link CommentSummaryView}
     * @param tableViewer {@link CSTableViewer} which should be controlled by this instance
     * @author Malte Brunnlieb (28.04.2012)
     */
    public ViewController(CSTableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        // TODO Auto-generated method stub
        // for TableViewer
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        // TODO Auto-generated method stub
        // for TableViewer
    }
}
