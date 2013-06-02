/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary.control;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.commentSummary.CSTableViewer;
import org.agilereview.ui.basic.commentSummary.CSToolBar;
import org.agilereview.ui.basic.commentSummary.filter.SearchFilter;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.hamcrest.Matcher;

/**
 * Controller for filter events
 * @author Malte Brunnlieb (03.05.2012)
 */
public class FilterController extends SelectionAdapter implements Listener, KeyListener {
    
    /**
     * Filter applied on the {@link ReviewSet}
     * @author Malte Brunnlieb (01.06.2013)
     */
    private enum Filter {
        /**
         * Search Filter
         */
        SEARCH_FILTER,
        /**
         * Open Comment Filter
         */
        OPEN_FILTER
    }
    
    /**
     * ToolBar which sets the filters
     */
    private final CSToolBar toolBar;
    /**
     * The current {@link SearchFilter} set on the {@link CSTableViewer}
     */
    private SearchFilter currentSearchFilter;
    /**
     * Currently provided {@link ReviewSet}
     */
    private ReviewSet reviewSet;
    /**
     * Currently applied filters
     */
    private Map<Object, Matcher<Object>> currentFilters = new HashMap<Object, Matcher<Object>>();
    
    /**
     * Creates a new instance of the {@link FilterController} controlling the given {@link CSToolBar}
     * @param toolBar {@link CSToolBar} which sets the filter mechanisms
     * @author Malte Brunnlieb (03.05.2012)
     */
    public FilterController(CSToolBar toolBar) {
        this.toolBar = toolBar;
        this.currentSearchFilter = new SearchFilter(Column.NULL);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        processSearchFiltering();
    }
    
    /**
     * Adds or removes the current search filter dependent on the current specified search text in the {@link CSToolBar}
     * @author Malte Brunnlieb (01.06.2013)
     */
    private void processSearchFiltering() {
        String searchText = toolBar.getSearchText();
        currentSearchFilter.setSearchText(searchText);
        if (searchText.trim().isEmpty()) {
            removeFilter(this);
        } else {
            applyFilter(Filter.SEARCH_FILTER, currentSearchFilter.getFilterExpression());
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void handleEvent(Event event) {
        if (event.widget == null || event.widget.getData("actionCommand") == null) return;
        
        Object command = event.widget.getData("actionCommand");
        if (command.equals("setSearchFilter") && event.widget instanceof Combo) {
            Combo comboBox = (Combo) event.widget;
            currentSearchFilter = new SearchFilter(Column.get(comboBox.getText()));
            processSearchFiltering();
            toolBar.setFilterText("Search for " + comboBox.getText());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @author Malte Brunnlieb (03.05.2012)
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        if (toolBar.showOnlyOpenComments()) {
            applyFilter(Filter.OPEN_FILTER, having(on(Comment.class).getStatus(), not(equals(0))));
        } else {
            removeFilter(Filter.OPEN_FILTER);
        }
    }
    
    /**
     * Applies the given filter
     * @param owner Owner {@link Object} of the given filter
     * @param filter {@link Matcher} which represents the filtermechanism
     * @author Malte Brunnlieb (01.06.2013)
     */
    private void applyFilter(Object owner, Matcher<Object> filter) {
        currentFilters.put(owner, filter);
        reviewSet.setCommentFilter(owner, filter);
    }
    
    /**
     * Removes the given filter
     * @param owner Owner {@link Object} of the filter to be removed
     * @author Malte Brunnlieb (01.06.2013)
     */
    private void removeFilter(Object owner) {
        currentFilters.remove(owner);
        reviewSet.removeCommentFilter(owner);
    }
    
    /**
     * Sets the current {@link ReviewSet} reference
     * @param reviewSet
     * @author Malte Brunnlieb (01.06.2013)
     */
    public void setReviewSet(ReviewSet reviewSet) {
        this.reviewSet = reviewSet;
        for (Entry<Object, Matcher<Object>> entry : currentFilters.entrySet()) {
            reviewSet.setCommentFilter(entry.getKey(), entry.getValue());
        }
    }
    
}
