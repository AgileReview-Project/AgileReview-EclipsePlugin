/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.Activator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.xml.sax.helpers.ParserFactory;

/**
 * 
 * @author Malte Brunnlieb (22.03.2012)
 */
public class CommentSummaryView extends ViewPart {
	
	/**
	 * Current instance used by the ViewPart
	 */
	private static CommentSummaryView instance = new CommentSummaryView();
	
	/**
	 * Provides the current used instance of the CommentSummaryView
	 * @return the instance of the CommentSummaryView
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	public static CommentSummaryView getInstance() {
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public void createPartControl(Composite parent) {
		instance = this;
		
		int layoutCols = 6;
		
		// set layout of parent
		GridLayout layout = new GridLayout(layoutCols, false);
		parent.setLayout(layout);
		
		// create UI elements (filter, add-/delete-button)
		createToolBar(parent);
		createViewer(parent);
		
		// set comparator (sorting order of columns) and filter
		comparator = new AgileViewerComparator();
		viewer.setComparator(comparator);
		commentFilter = new AgileCommentFilter("ALL");
		viewer.addFilter(commentFilter);
		
		//add help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".TableView");
		
		// register view
		ViewControl.registerView(this.getClass());
		
		// get editor that is active when opening eclipse
		if (getActiveEditor() instanceof IEditorPart) {
			this.parserMap.put(getActiveEditor(), ParserFactory.createParser(getActiveEditor()));
			this.parserMap.get(getActiveEditor()).filter(getFilteredComments());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
}
