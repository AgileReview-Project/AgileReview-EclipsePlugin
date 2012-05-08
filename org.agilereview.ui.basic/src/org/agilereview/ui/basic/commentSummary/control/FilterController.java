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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agilereview.core.external.storage.Review;
import org.agilereview.ui.basic.commentSummary.CSTableViewer;
import org.agilereview.ui.basic.commentSummary.CSToolBar;
import org.agilereview.ui.basic.commentSummary.filter.ExplorerSelectionFilter;
import org.agilereview.ui.basic.commentSummary.filter.OpenFilter;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.agilereview.ui.basic.tools.ExceptionHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
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
     * The current {@link SearchFilter} set on the {@link CSTableViewer}
     */
    private SearchFilter currentSearchFilter;
    /**
     * The current {@link ExplorerSelectionFilter} set on the {@link CSTableViewer}
     */
    private ExplorerSelectionFilter currentExplorerSelectionFilter;
    
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
     * @param event provided by the SelectionProvider
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        if (event.getSelection() instanceof ITreeSelection && !event.getSelection().isEmpty()) { //TODO check for source??
        
            ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
            if (cmdService == null) {
                ExceptionHandler.notifyUser("The Service ICommandService could not be determined.");
                return;
            }
            
            Command linkExplorerCommand = cmdService.getCommand("org.eclipse.ui.navigate.linkWithEditor"); // TODO check command ID
            Object state = linkExplorerCommand.getState("org.eclipse.ui.commands.toggleState").getValue();
            
            if ((Boolean) state) {
                ITreeSelection sel = (ITreeSelection) event.getSelection();
                List<String> reviewIDs = getReviewIDs(sel);
                Map<String, Set<String>> paths = getPaths(sel);
                
                currentExplorerSelectionFilter = new ExplorerSelectionFilter(reviewIDs, paths);
                tableViewer.addFilter(currentExplorerSelectionFilter);
                //filterComments(); //TODO check this (Parser filtering)
            }
        } else {
            if (currentExplorerSelectionFilter != null) {
                tableViewer.removeFilter(currentExplorerSelectionFilter);
            }
        }
    }
    
    /**
     * Picks all {@link Review}s contained in the given {@link ITreeSelection} and returns all review IDs
     * @param sel {@link ITreeSelection} in which will be searched for {@link Review}s
     * @return A {@link List} of contained the found review IDs
     * @author Malte Brunnlieb (08.05.2012)
     */
    private List<String> getReviewIDs(ITreeSelection sel) {
        ArrayList<String> reviewIDs = new ArrayList<String>();
        Iterator<?> it = sel.iterator();
        
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof Review) {
                String reviewID = ((Review) next).getId();
                if (!reviewIDs.contains(reviewID)) {
                    reviewIDs.add(reviewID);
                }
            }
        }
        
        return reviewIDs;
    }
    
    /**
     * Searches for {@link IResource}s in the given {@link ITreeSelection}. All {@link IResource}s will be mapped by their origin review ID.
     * @param sel {@link ITreeSelection} which will be the bases of the search
     * @return A {@link Map} of review IDs to a {@link Set} of paths of the underlying {@link IResource}s
     * @author Malte Brunnlieb (08.05.2012)
     */
    private Map<String, Set<String>> getPaths(ITreeSelection sel) {
        HashMap<String, Set<String>> paths = new HashMap<String, Set<String>>();
        
        for (TreePath tp : sel.getPaths()) {
            if (tp.getLastSegment() instanceof IResource) {
                String path = ((IResource) tp.getLastSegment()).getLocation().toOSString();
                String reviewID = ((Review) tp.getFirstSegment()).getId();
                if (paths.containsKey(reviewID)) {
                    paths.get(reviewID).add(path);
                } else {
                    paths.put(reviewID, new HashSet<String>());
                    paths.get(reviewID).add(path);
                }
            }
        }
        
        return paths;
    }
}
