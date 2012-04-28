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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

/**
 * {@link ViewPart} representing the {@link CommentSummaryView}
 * @author Malte Brunnlieb (08.04.2012)
 */
public class CommentSummaryView extends ViewPart implements Listener, KeyListener {
	
	/**
	 * Current Instance used by the ViewPart
	 */
	private static CommentSummaryView instance;
	
	/**
	 * Provides the current used instance of the CommentTableView
	 * @return instance of CommentTableView
	 */
	public static CommentSummaryView getInstance() {
		return instance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @author Malte Brunnlieb (08.04.2012)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 * @author Malte Brunnlieb (08.04.2012)
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
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
