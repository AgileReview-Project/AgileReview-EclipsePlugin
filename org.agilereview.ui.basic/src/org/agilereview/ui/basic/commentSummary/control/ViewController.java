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
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This {@link ViewController} handles elementary events of the {@link CommentSummaryView}
 * @author Malte Brunnlieb (28.04.2012)
 */
public class ViewController extends SelectionAdapter implements IDoubleClickListener {
    
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
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     * @author Malte Brunnlieb (28.04.2012)
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        // TODO Open Editor and Jump to Position
    }
    
    /**
     * This implementation assures the sorting functionality of columns
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @author Malte Brunnlieb (11.05.2012)
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        TableColumn tableColumn = (TableColumn) e.getSource();
        Column column = (Column) tableColumn.getData();
        ((ColumnComparator) tableViewer.getComparator()).setColumn(column);
        int sortDirection = tableViewer.getTable().getSortDirection();
        if (tableViewer.getTable().getSortColumn() == tableColumn) {
            sortDirection = (sortDirection == SWT.UP ? SWT.DOWN : SWT.UP);
        } else {
            sortDirection = SWT.DOWN;
        }
        tableViewer.getTable().setSortDirection(sortDirection);
        tableViewer.getTable().setSortColumn(tableColumn);
        tableViewer.refresh();
    }
}