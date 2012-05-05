/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.agilereview.ui.basic.commentSummary.CSTableViewer;
import org.agilereview.ui.basic.commentSummary.CSToolBar;
import org.agilereview.ui.basic.commentSummary.filter.ExplorerSelectionFilter;
import org.agilereview.ui.basic.commentSummary.filter.OpenFilter;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.commands.ICommandService;

/**
 * Controller for filter events
 * @author Malte Brunnlieb (03.05.2012)
 */
public class FilterController extends SelectionAdapter implements Listener, KeyListener, ISelectionChangedListener {
    
    /**
     * ToolBar which sets the filters
     */
    private CSToolBar toolBar;
    /**
     * TableViewer of the comment table
     */
    private CSTableViewer tableViewer;
    /**
     * The current
     */
    private SearchFilter currentSearchFilter;
    
    /**
     * Creates a new instance of the {@link FilterController} controlling the given {@link CSToolBar}
     * @param toolBar {@link CSToolBar} which sets the filter mechanisms
     * @param viewer {@link CSTableViewer} of the comment table
     * @param searchFilter initiating {@link SearchFilter}
     * @author Malte Brunnlieb (03.05.2012)
     */
    public FilterController(CSToolBar toolBar, CSTableViewer viewer, SearchFilter searchFilter) {
        this.toolBar = toolBar;
        this.tableViewer = viewer;
        this.currentSearchFilter = searchFilter;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // not needed
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        currentSearchFilter.setSearchText(toolBar.getSearchText());
        tableViewer.refresh();
        //filterComments(); //TODO perhaps get filtered comments of viewer??
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void handleEvent(Event event) {
        Object source = event.item.getData();
        if (source.equals("chooseFilterType")) {
            // filter selection (DropDown box ToolBar)
            MenuItem item = (MenuItem) event.widget;
            tableViewer.removeFilter(currentSearchFilter);
            currentSearchFilter = new SearchFilter(item.getText());
            tableViewer.addFilter(currentSearchFilter);
            toolBar.setFilterText("Search for " + item.getText());
        } else if (source.equals("openMenu")) {
            // filter menu (DropDownBox ToolBar)
            if (event.detail == SWT.ARROW || event.detail == 0) {
                toolBar.showDropDownMenu();
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        OpenFilter openFilter = new OpenFilter();
        if (toolBar.showOnlyOpenComments()) {
            tableViewer.addFilter(openFilter);
        } else {
            tableViewer.removeFilter(openFilter);
        }
        //filterComments(); //TODO remove?! wird glaub ich nicht mehr gebraucht
    }
    
    /**
     * Selection of ReviewExplorer changed, filter comments
     * @param event will be forwarded from the {@link ViewControl}
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        if (event.getSelection() instanceof IStructuredSelection && !event.getSelection().isEmpty()) {
            IStructuredSelection sel = (IStructuredSelection) event.getSelection();
            if (sel.getFirstElement() instanceof AbstractMultipleWrapper) {
                // get selection, selection's iterator, initialize reviewIDs and paths
                Iterator<?> it = sel.iterator();
                ArrayList<String> reviewIDs = new ArrayList<String>();
                HashMap<String, HashSet<String>> paths = new HashMap<String, HashSet<String>>();
                
                // get all selected reviews and paths
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next instanceof MultipleReviewWrapper) {
                        String reviewID = ((MultipleReviewWrapper) next).getWrappedReview().getId();
                        if (!reviewIDs.contains(reviewID)) {
                            reviewIDs.add(reviewID);
                        }
                    } else if (next instanceof AbstractMultipleWrapper) {
                        String path = ((AbstractMultipleWrapper) next).getPath();
                        String reviewID = ((AbstractMultipleWrapper) next).getReviewId();
                        if (paths.containsKey(reviewID)) {
                            paths.get(reviewID).add(path);
                        } else {
                            paths.put(reviewID, new HashSet<String>());
                            paths.get(reviewID).add(path);
                        }
                    }
                }
                
                // Remove the old filter, then create the new filter, 
                // so it can be applied directly when needed
                viewer.removeFilter(this.selectionFilter);
                this.selectionFilter = new ExplorerSelectionFilter(reviewIDs, paths);
                
                ICommandService cmdService = (ICommandService) getSite().getService(ICommandService.class);
                Command linkExplorerCommand = cmdService.getCommand("de.tukl.cs.softech.agilereview.views.reviewexplorer.linkexplorer");
                Object state = linkExplorerCommand.getState("org.eclipse.ui.commands.toggleState").getValue();
                // If "Link Editor" is enabled, then filter also
                if ((Boolean) state) {
                    PluginLogger.log(this.getClass().toString(), "selectionChanged", "Adding new filter regarding selection of ReviewExplorer");
                    // refresh annotations, update list of filtered comments
                    viewer.addFilter(this.selectionFilter);
                    filterComments();
                }
            }
        }
    }
}
