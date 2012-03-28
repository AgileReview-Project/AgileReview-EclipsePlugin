/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Apache License v2.0 which accompanies this distribution, and is available
 * at http://www.apache.org/licenses/LICENSE-2.0.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * ToolBar for {@link CommentSummaryView}
 * @author Malte Brunnlieb (28.03.2012)
 */
public class CSToolBar extends ToolBar {
	
	/**
	 * Creates a new {@link CSToolBar} instance
	 * @param parent parent {@link Composite} of the ToolBar
	 * @param style style for the ToolBar
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	public CSToolBar(Composite parent, int style) {
		super(parent, style);
		// add dropdown box to toolbar to select category to filter
		final ToolItem itemDropDown = new ToolItem(this, SWT.DROP_DOWN);
		itemDropDown.setText("Search for ALL");
		itemDropDown.setToolTipText("Click here to select the filter option");
		
		// create listener to submit category changes to dropdown box and filter
		Listener selectionListener = new Listener() {
			public void handleEvent(Event event) {
				MenuItem item = (MenuItem) event.widget;
				viewer.removeFilter(commentFilter);
				commentFilter = new AgileCommentFilter(item.getText());
				viewer.addFilter(commentFilter);
				itemDropDown.setText("Search for " + item.getText());
				toolBar.pack();
				parent.layout();
			}
		};
		
		// create menu for dropdown box
		final Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		
		// add menu items
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("ALL");
		item.addListener(SWT.Selection, selectionListener);
		item = new MenuItem(menu, SWT.SEPARATOR);
		for (int i = 0; i < titles.length; i++) {
			item = new MenuItem(menu, SWT.PUSH);
			item.setText(titles[i]);
			item.addListener(SWT.Selection, selectionListener);
		}
		
		// add text field for filter to toolbar
		this.filterText = new Text(this, SWT.BORDER | SWT.SINGLE);
		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				commentFilter.setSearchText(filterText.getText());
				viewer.refresh();
				filterComments();
			}
			
		});
		filterText.pack();
		
		// add seperator to toolbar
		ToolItem itemSeparator = new ToolItem(toolBar, SWT.SEPARATOR);
		itemSeparator.setWidth(filterText.getBounds().width);
		itemSeparator.setControl(filterText);
		
		// add show open comments only checkbox
		final int filterStatusNumber = 0;
		final Button onlyOpenCommentsCheckbox = new Button(parent, SWT.CHECK);
		String statusStr = pm.getCommentStatusByID(filterStatusNumber);
		onlyOpenCommentsCheckbox.setText("Only show " + statusStr + " comments");
		onlyOpenCommentsCheckbox.setToolTipText("Show only " + statusStr + " comments");
		onlyOpenCommentsCheckbox.addSelectionListener(new SelectionAdapter() {
			
			private final ViewerFilter openFilter = new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					return ((Comment) element).getStatus() == filterStatusNumber; // XXX Hack
				}
			};
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (onlyOpenCommentsCheckbox.getSelection()) {
					viewer.addFilter(openFilter);
				} else {
					viewer.removeFilter(openFilter);
				}
				filterComments();
			}
		});
		
		// add listener to dropdown box to show menu
		itemDropDown.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW || event.detail == 0) {
					Rectangle bounds = itemDropDown.getBounds();
					Point point = toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
					filterText.setFocus();
				}
			}
		});
		
		pack();
	}
	
}
