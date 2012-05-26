/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.agilereview.ui.basic.commentSummary.control.FilterController;
import org.agilereview.ui.basic.commentSummary.table.Column;
import org.agilereview.ui.basic.tools.CommentProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;

/**
 * ToolBar for the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CSToolBar extends Composite {
    
    /**
     * Filter text field
     */
    private Text filterText;
    /**
     * Drop down box item where the current filter value is displayed
     */
    private Combo dropDownBox;
    /**
     * CheckBox for the "show only open comments" filter functionality
     */
    private Button onlyOpenCommentsCheckbox;
    /**
     * PopUp menu for the filter DropDownBox
     */
    private Menu menu;
    
    /**
     * Creates a new instance of the {@link CSToolBar}
     * @param parent on which this {@link CSToolBar} should be added
     * @author Malte Brunnlieb (08.04.2012)
     */
    CSToolBar(Composite parent) {
        super(parent, SWT.NONE);
        createToolBar();
    }
    
    /**
     * Creates the ToolBar elements
     * @author Malte Brunnlieb (08.04.2012)
     */
    private void createToolBar() {
        setLayout(new GridLayout(10, false));
        
        Label label = new Label(this, SWT.NONE);
        label.setText("Search in");
        
        // add dropdown box to toolbar to select category to filter
        dropDownBox = new Combo(this, SWT.READ_ONLY);
        dropDownBox.setText("Search for ALL");
        dropDownBox.setToolTipText("Click here to select the filter option");
        List<String> filterList = new LinkedList<String>(Arrays.asList(Column.valuesToString()));
        String all = "All Columns";
        filterList.add(0, all);
        dropDownBox.setItems(filterList.toArray(new String[0]));
        dropDownBox.setText(all);
        
        filterText = new Text(this, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData();
        gd.widthHint = 100;
        filterText.setLayoutData(gd);
        filterText.setData("testKey", "filterText");
        
        Sash sash = new Sash(this, SWT.NONE);
        gd = new GridData();
        gd.widthHint = 20;
        sash.setLayoutData(gd);
        
        onlyOpenCommentsCheckbox = new Button(this, SWT.CHECK);
        String statusStr = new CommentProperties().getStatusByID(0);
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
        dropDownBox.setData("setSearchFilter");
        dropDownBox.addListener(SWT.Selection, toolBarController);
        filterText.addKeyListener(toolBarController);
        onlyOpenCommentsCheckbox.setData("setOnlyOpenFilter");
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
        //        layout(); //TODO check if necessary! else delete parent attribute!
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
    
    /**
     * {@inheritDoc}
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     * @author Malte Brunnlieb (10.05.2012)
     */
    @Override
    public boolean setFocus() {
        return filterText.setFocus();
    }
}
