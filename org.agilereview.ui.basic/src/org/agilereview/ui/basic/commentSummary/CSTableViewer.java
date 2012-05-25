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

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.commentSummary.control.ViewController;
import org.agilereview.ui.basic.commentSummary.table.CSColumnLabelProvider;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * {@link TableViewer} for the {@link CommentSummaryView}
 * @author Malte Brunnlieb (28.04.2012)
 */
public class CSTableViewer extends TableViewer {
    
    /**
     * The width of the table's columns
     */
    private final int[] columnBounds = { 90, 80, 70, 70, 70, 70, 120, 120, 50, 500 };
    
    /**
     * Creates a new {@link CSTableViewer} for the {@link CommentSummaryView}
     * @param parent {@link Composite} as parent for this instance
     * @author Malte Brunnlieb (28.04.2012)
     */
    public CSTableViewer(Composite parent) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createUI(parent);
    }
    
    /**
     * Creates the TableViewer component, sets it's model and layout
     * @param parent The parent of the TableViewer
     * @author Malte Brunnlieb (28.04.2012)
     */
    private void createUI(Composite parent) {
        
        Column[] columns = Column.values();
        TableViewerColumn col;
        for (int i = 0; i < columns.length; i++) {
            col = createColumn(columns[i], i);
            col.setLabelProvider(new CSColumnLabelProvider(columns[i]));
        }
        
        // set attributes of viewer's table
        Table table = getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        // set layout of the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        getControl().setLayoutData(gridData);
    }
    
    /**
     * Creates a single column of the viewer with given parameters
     * @param column {@link Column} type
     * @param colNumber The columns number
     * @return The column with given parameters
     * @author Malte Brunnlieb (28.04.2012)
     */
    private TableViewerColumn createColumn(Column column, int colNumber) {
        TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        TableColumn tableColumn = viewerColumn.getColumn();
        tableColumn.setText(column.toString());
        tableColumn.setWidth(columnBounds[colNumber]);
        tableColumn.setResizable(true);
        tableColumn.setMoveable(true);
        tableColumn.addSelectionListener(new ViewController(this));
        tableColumn.setData(column);
        return viewerColumn;
    }
    
    /**
     * Lists all currently visible {@link Comment}s after applying filters
     * @return the currently visible {@link List} of {@link Comment}s
     * @author Malte Brunnlieb (10.05.2012)
     */
    public List<Comment> getVisibleComments() {
        List<Comment> comments = new LinkedList<Comment>();
        for (TableItem item : getTable().getItems()) {
            comments.add((Comment) item.getData());
        }
        return comments;
    }
}
