/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.core.external.definition.IStorageClient;
import org.agilereview.ui.basic.Activator;
import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.commentSummary.control.ViewController;
import org.agilereview.ui.basic.commentSummary.filter.ColumnComparator;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.agilereview.ui.basic.commentSummary.table.ContentProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * {@link ViewPart} representing the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CommentSummaryView extends ViewPart {
    
    /**
     * The values describe the currently shown content of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (30.05.2012)
     */
    private enum ViewContent {
        /**
         * Stays for the state "No StorageClient connected"
         */
        DISCONNECTED,
        /**
         * Stays for the state where a StorageClient is available and review content will be shown
         */
        CONNECTED
    };
    
    /**
     * The {@link ToolBar} for this ViewPart
     */
    private CSToolBar toolBar;
    /**
     * The {@link TableViewer} for this ViewPart
     */
    private CSTableViewer viewer;
    /**
     * The content provider for the {@link TableViewer}
     */
    private ContentProvider contentProvider;
    /**
     * The {@link FilterController} which manages all filter actions
     */
    private FilterController filterController;
    /**
     * Parent of this {@link ViewPart}
     */
    private Composite parent;
    /**
     * Identifies the current state of the UI
     */
    private ViewContent content;
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        parent.setLayout(new GridLayout());
        ContentProvider.bind(this);
        
        synchronized (this.parent) {
            if (contentProvider == null) {
                displayStorageDisconnected();
            } else {
                buildWorkingUI();
            }
        }
        
        //add help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".TableView"); //TODO adapt help context
    }
    
    /**
     * Binds the given {@link ContentProvider} to the {@link CSTableViewer} instance of this view. If the parameter is net to null, the
     * {@link CommentSummaryView} will display a no {@link IStorageClient} registered message instead of a table
     * @param tableModel model for the {@link CSTableViewer}
     * @return The TableViewer the model was bound to. Will be null if the parameter has been set to null.
     * @author Malte Brunnlieb (27.05.2012)
     */
    public TableViewer bindTableModel(ContentProvider tableModel) {
        synchronized (parent) {
            contentProvider = tableModel;
            if (contentProvider != null) {
                if (viewer == null) {
                    buildWorkingUI();
                }
                viewer.setContentProvider(contentProvider);
            } else {
                displayStorageDisconnected();
            }
            return viewer;
        }
    }
    
    /**
     * Creates the full functional view using the {@link CSToolBar} and {@link CSTableViewer}
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void buildWorkingUI() {
        if (content == ViewContent.CONNECTED) return;
        clearParent();
        content = ViewContent.CONNECTED;
        
        toolBar = new CSToolBar(parent);
        
        viewer = new CSTableViewer(parent);
        viewer.setContentProvider(new ArrayContentProvider());
        ColumnComparator comparator = new ColumnComparator();
        viewer.setComparator(comparator);
        SearchFilter commentFilter = new SearchFilter("ALL");
        viewer.addFilter(commentFilter);
        
        viewer.addDoubleClickListener(new ViewController(viewer));
        getSite().setSelectionProvider(viewer);
        
        filterController = new FilterController(toolBar, viewer, commentFilter);
        toolBar.setListeners(filterController);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener("org.agilereview.ui.basic.reviewExplorerView", filterController);
    }
    
    /**
     * Displays a message as no {@link IStorageClient} provides data
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void displayStorageDisconnected() {
        if (content == ViewContent.DISCONNECTED) return;
        clearParent();
        content = ViewContent.CONNECTED;
        
        Label label = new Label(parent, SWT.CENTER);
        label.setText("No data available as currently no StorageClient is connected.");
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.CENTER;
        gd.verticalAlignment = GridData.CENTER;
        label.setLayoutData(gd);
        parent.pack();
    }
    
    /**
     * Disposes all children of the current parent.
     * @author Malte Brunnlieb (27.05.2012)
     */
    private void clearParent() {
        if (filterController != null) {
            getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener("org.agilereview.ui.basic.reviewExplorerView",
                    filterController);
        }
        toolBar = null;
        viewer = null;
        for (Control child : parent.getChildren()) {
            child.dispose();
        }
        parent.layout();
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
}
