/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * {@link TableViewer} for the {@link CommentSummaryView}
 * @author Malte Brunnlieb (28.04.2012)
 */
public class CSTableViewer extends TableViewer {
    
    /**
     * The titles of the table's columns, also used to fill the filter menu
     */
    private final String[] columnTitles = { "ReviewName", "CommentID", "Author", "Recipient", "Status", "Priority", "Date created", "Date modified",
            "Replies", "Location" };
    /**
     * The width of the table's columns
     */
    private final int[] columnBounds = { 60, 70, 70, 70, 70, 70, 120, 120, 50, 100 };
    
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
        // create viewer
        createColumns();
        
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
        
        // set properties of columns to titles
        setColumnProperties(columnTitles);
    }
    
    /**
     * Creates the columns of the viewer and adds label providers to fill cells
     * @author Malte Brunnlieb (28.04.2012)
     */
    private void createColumns() {
        final CommentProperties commentProperties = new CommentProperties();
        
        // ReviewID
        TableViewerColumn col = createColumn(columnTitles[0], columnBounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return c.getReview().getId();
            }
        });
        
        // CommentID
        col = createColumn(columnTitles[1], columnBounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return c.getId();
            }
        });
        
        // Author
        col = createColumn(columnTitles[2], columnBounds[2], 2);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return c.getAuthor();
            }
        });
        
        // Recipient
        col = createColumn(columnTitles[3], columnBounds[3], 3);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return c.getRecipient();
            }
        });
        
        // Status
        col = createColumn(columnTitles[4], columnBounds[4], 4);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                String status = commentProperties.getStatusByID(c.getStatus());
                return status;
            }
        });
        
        // Priority
        col = createColumn(columnTitles[5], columnBounds[5], 5);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                String prio = commentProperties.getPriorityByID(c.getPriority());
                return prio;
            }
        });
        
        // Date created
        col = createColumn(columnTitles[6], columnBounds[6], 6);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
                return df.format(c.getCreationDate().getTime());
            }
        });
        
        // Date modified
        col = createColumn(columnTitles[7], columnBounds[7], 7);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                if (c.getModificationDate() == null) return "";
                DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
                return df.format(c.getModificationDate().getTime());
            }
        });
        
        // Number of relplies
        col = createColumn(columnTitles[8], columnBounds[8], 8);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return String.valueOf(c.getReplies().size());
            }
        });
        
        // Location
        col = createColumn(columnTitles[9], columnBounds[9], 9);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Comment c = (Comment) element;
                return c.getCommentedFile().getFullPath().toOSString();
            }
        });
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
}
