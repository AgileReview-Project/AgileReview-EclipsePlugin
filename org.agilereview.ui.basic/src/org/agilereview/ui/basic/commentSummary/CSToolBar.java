/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.commentSummary.filter.OpenFilter;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * ToolBar for the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CSToolBar extends ToolBar {
    
    /**
     * Filter text field
     */
    private Text filterText;
    
    /**
     * Creates a new instance of the {@link CSToolBar}
     * @param parent on which this {@link CSToolBar} should be added
     * @param viewer {@link CSTableViewer} of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (08.04.2012)
     */
    public CSToolBar(Composite parent, CSTableViewer viewer) {
        super(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        createToolBar(viewer);
    }
    
    /**
     * Creates the ToolBar elements
     * @param viewController which handles all UI events
     * @param viewer {@link CSTableViewer} of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (08.04.2012)
     */
    private void createToolBar(final CSTableViewer viewer) {
        // add dropdown box to toolbar to select category to filter
        final ToolItem itemDropDown = new ToolItem(this, SWT.DROP_DOWN);
        itemDropDown.setText("Search for ALL");
        itemDropDown.setToolTipText("Click here to select the filter option");
        
        // create menu for dropdown box
        final Menu menu = new Menu(getShell(), SWT.POP_UP);
        
        // add menu items
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("ALL");
        item.addListener(SWT.Selection, viewController);
        item = new MenuItem(menu, SWT.SEPARATOR);
        String[] titles = viewer.getTitles();
        for (int i = 0; i < titles.length; i++) {
            item = new MenuItem(menu, SWT.PUSH);
            item.setText(titles[i]);
            item.addListener(SWT.Selection, viewController);
        }
        
        // add text field for filter to toolbar
        this.filterText = new Text(this, SWT.BORDER | SWT.SINGLE);
        filterText.addKeyListener(viewController);
        filterText.pack();
        
        // add seperator to toolbar
        ToolItem itemSeparator = new ToolItem(this, SWT.SEPARATOR);
        itemSeparator.setWidth(filterText.getBounds().width);
        itemSeparator.setControl(filterText);
        
        // add show open comments only checkbox
        final int filterStatusNumber = 0;
        final Button onlyOpenCommentsCheckbox = new Button(this, SWT.CHECK);
        String statusStr = new CommentProperties().getStatusByID(filterStatusNumber);
        onlyOpenCommentsCheckbox.setText("Only show " + statusStr + " comments");
        onlyOpenCommentsCheckbox.setToolTipText("Show only " + statusStr + " comments");
        onlyOpenCommentsCheckbox.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                ViewerFilter openFilter = new OpenFilter();
                if (onlyOpenCommentsCheckbox.getSelection()) {
                    viewer.addFilter(openFilter);
                } else {
                    viewer.removeFilter(openFilter);
                }
                //filterComments(); //TODO remove?! wird glaub ich nicht mehr gebraucht
            }
        });
        
        // add listener to dropdown box to show menu
        itemDropDown.addListener(SWT.Selection, viewController);
        
        pack();
    }
    
}
