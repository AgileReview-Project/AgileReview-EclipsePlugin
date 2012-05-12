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
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
     * Columns of the comment table
     * @author Malte Brunnlieb (11.05.2012)
     */
    public static enum Column {
        REVIEW_ID, COMMENT_ID, AUTHOR, RECIPIENT, STATUS, PRIORITY, DATE_CREATED, DATE_MODIFIED, NO_REPLIES, LOCATION
    }
    
    /**
     * The titles of the table's columns, also used to fill the filter menu
     */
    private final String[] columnTitles = { "ReviewName", "CommentID", "Author", "Recipient", "Status", "Priority", "Date created", "Date modified",
            "Replies", "Location" };
    /**
     * The width of the table's columns
     */
    private final int[] columnBounds = { 60, 70, 70, 70, 70, 70, 120, 120, 50, 200 };
    
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
            col = createColumn(columnTitles[i], columnBounds[i], i);
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
     * @param title The title to be set
     * @param bound The width of the column
     * @param colNumber The columns number
     * @return The column with given parameters
     * @author Malte Brunnlieb (28.04.2012)
     */
    private TableViewerColumn createColumn(String title, int bound, int colNumber) {
        TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        column.addSelectionListener(createSelectionAdapter(column, colNumber));
        return viewerColumn;
    }
    
    /**
     * Get the selection adapter of a given column
     * @param column the column
     * @param index the column's index
     * @return the columns selection adapter
     * @author Malte Brunnlieb (28.04.2012)
     */
    private SelectionAdapter createSelectionAdapter(final TableColumn column, final int index) {
        SelectionAdapter selectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ((ColumnComparator) getComparator()).setColumn(index);
                int dir = getTable().getSortDirection();
                if (getTable().getSortColumn() == column) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                } else {
                    dir = SWT.DOWN;
                }
                getTable().setSortDirection(dir);
                getTable().setSortColumn(column);
                refresh();
            }
        };
        return selectionAdapter;
    }
    
    /**
     * Returns the column titles of the table
     * @return an Array containing all column titles of the table
     * @author Malte Brunnlieb (29.04.2012)
     */
    public String[] getTitles() {
        return columnTitles.clone();
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
