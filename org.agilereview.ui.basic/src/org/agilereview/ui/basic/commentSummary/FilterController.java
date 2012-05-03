/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.commentSummary.filter.OpenFilter;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Controller for filter events
 * @author Malte Brunnlieb (03.05.2012)
 */
public class FilterController extends SelectionAdapter implements Listener, KeyListener {
    
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
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
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
        } else if (source.equals("setFilterText")) {
            currentSearchFilter.setSearchText(toolBar.getSearchText());
            tableViewer.refresh();
            //filterComments(); //TODO perhaps get filtered comments of viewer??
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
}
