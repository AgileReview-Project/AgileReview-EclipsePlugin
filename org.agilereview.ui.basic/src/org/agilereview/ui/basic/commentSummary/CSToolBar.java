/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
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
     * Menu items for the filter menu
     */
    private MenuItem[] menuItems;
    /**
     * Drop down box item where the current filter value is displayed
     */
    private ToolItem dropDownBox;
    /**
     * CheckBox for the "show only open comments" filter functionality
     */
    private Button onlyOpenCommentsCheckbox;
    /**
     * Parent {@link Composite} of this instance
     */
    private Composite parent;
    /**
     * PopUp menu for the filter DropDownBox
     */
    private Menu menu;
    
    /**
     * Creates a new instance of the {@link CSToolBar}
     * @param parent on which this {@link CSToolBar} should be added
     * @param viewer {@link CSTableViewer} of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (08.04.2012)
     */
    CSToolBar(Composite parent, CSTableViewer viewer) {
        super(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        createToolBar(viewer);
    }
    
    /**
     * Creates the ToolBar elements
     * @param viewer {@link CSTableViewer} of the {@link CommentSummaryView}
     * @author Malte Brunnlieb (08.04.2012)
     */
    private void createToolBar(final CSTableViewer viewer) {
        // add dropdown box to toolbar to select category to filter
        dropDownBox = new ToolItem(this, SWT.DROP_DOWN);
        dropDownBox.setText("Search for ALL");
        dropDownBox.setToolTipText("Click here to select the filter option");
        
        // create menu for dropdown box
        menu = new Menu(getShell(), SWT.POP_UP);
        
        String[] tableColums = viewer.getTitles();
        menuItems = new MenuItem[tableColums.length + 1];
        
        // add menu items
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("ALL");
        menuItems[tableColums.length] = item;
        item = new MenuItem(menu, SWT.SEPARATOR);
        for (int i = 0; i < tableColums.length; i++) {
            item = new MenuItem(menu, SWT.PUSH);
            item.setText(tableColums[i]);
            menuItems[i] = item;
        }
        
        // add text field for filter to toolbar
        filterText = new Text(this, SWT.BORDER | SWT.SINGLE);
        filterText.pack();
        
        // add seperator to toolbar
        ToolItem itemSeparator = new ToolItem(this, SWT.SEPARATOR);
        itemSeparator.setWidth(filterText.getBounds().width);
        itemSeparator.setControl(filterText);
        
        // add show open comments only checkbox
        int filterStatusNumber = 0;
        onlyOpenCommentsCheckbox = new Button(this, SWT.CHECK);
        String statusStr = new CommentProperties().getStatusByID(filterStatusNumber);
        onlyOpenCommentsCheckbox.setText("Only show " + statusStr + " comments");
        onlyOpenCommentsCheckbox.setToolTipText("Show only " + statusStr + " comments");
        
        pack();
    }
    
    /**
     * Sets the given controller instances for all components of the ToolBar which should be handled
     * @param toolBarController the {@link FilterController} which has to extend {@link SelectionAdapter} and implement the {@link KeyListener} and
     *            {@link Listener} interface
     * @author Malte Brunnlieb (03.05.2012)
     */
    void setListeners(FilterController toolBarController) {
        dropDownBox.setData("chooseFilterType");
        dropDownBox.addListener(SWT.Selection, toolBarController);
        for (MenuItem item : menuItems) {
            item.setData("chooseFilter");
            item.addListener(SWT.Selection, toolBarController);
        }
        filterText.addKeyListener(toolBarController);
        onlyOpenCommentsCheckbox.setData("openMenu");
        onlyOpenCommentsCheckbox.addSelectionListener(toolBarController);
    }
    
    /**
     * @return true, if the "show only open comments" filter is set<br>false, otherwise
     * @author Malte Brunnlieb (03.05.2012)
     */
    public boolean showOnlyOpenComments() {
        return onlyOpenCommentsCheckbox.getSelection();
    }
    
    /**
     * Sets the text shown on the filter DropDownBox
     * @param text which should be displayed
     * @author Malte Brunnlieb (03.05.2012)
     */
    public void setFilterText(String text) {
        dropDownBox.setText(text);
        pack();
        parent.layout(); //TODO check if necessary!
    }
    
    /**
     * Shows the DropDown menu for filter selection
     * @author Malte Brunnlieb (03.05.2012)
     */
    public void showDropDownMenu() {
        Rectangle bounds = dropDownBox.getBounds();
        Point point = toDisplay(bounds.x, bounds.y + bounds.height);
        menu.setLocation(point);
        menu.setVisible(true);
        filterText.setFocus(); //TODO check whether this is necessary and the intended behavior... why is this needed???
    }
    
    /**
     * @return Search text entered in the text filter search box
     * @author Malte Brunnlieb (03.05.2012)
     */
    public String getSearchText() {
        return filterText.getText();
    }
}
