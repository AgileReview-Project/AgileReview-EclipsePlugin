/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.commentSummary.filter.CommentFilter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * This {@link ViewController} handles all events related to the CommentSummaryView
 * @author Malte Brunnlieb (28.04.2012)
 */
public class ViewController implements Listener, KeyListener {
    
    /**
     * View which should be controlled (normally event source)
     */
    private final CommentSummaryView view;
    
    /**
     * Creates a new instance of {@link ViewController} for a given {@link CommentSummaryView}
     * @param view {@link CommentSummaryView} which should be controlled
     * @author Malte Brunnlieb (28.04.2012)
     */
    ViewController(CommentSummaryView view) {
        this.view = view;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void handleEvent(Event event) {
        MenuItem item = (MenuItem) event.widget;
        viewer.removeFilter(commentFilter);
        commentFilter = new CommentFilter(item.getText());
        viewer.addFilter(commentFilter);
        itemDropDown.setText("Search for " + item.getText());
        toolBar.pack();
        parent.layout();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // not needed
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @author Malte Brunnlieb (08.04.2012)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        commentFilter.setSearchText(filterText.getText());
        viewer.refresh();
        filterComments();
    }
}
