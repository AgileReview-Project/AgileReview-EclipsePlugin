/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.agilereview.ui.basic.Activator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
	 * The view that displays the comments
	 */
	private TableViewer viewer;
	
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
		new CommentSummaryToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		createViewer(parent, layoutCols);
		
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
	
	/**
	 * Creates the TableViewer component, sets it's model and layout
	 * @param parent The parent of the TableViewer
	 * @param layoutCols number of cols of the layout
	 * @return viewer The TableView component of this view
	 */
	private TableViewer createViewer(Composite parent, int layoutCols) {
		
		// create viewer
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		
		// set attributes of viewer's table
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// set input for viewer
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(this.comments);
		
		// provide access to selections of table rows
		getSite().setSelectionProvider(viewer);
		viewer.addSelectionChangedListener(ViewControl.getInstance());
		
		viewer.addDoubleClickListener(this);
		
		// set layout of the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = layoutCols;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		
		// set properties of columns to titles
		viewer.setColumnProperties(this.titles);
		
		return viewer;
	}
	
	/**
	 * Creates the columns of the viewer and adds label providers to fill cells
	 */
	private void createColumns() {
		for (int i = 0; i < 10; i++) {
			TableViewerColumn col = createColumn(titles[0], bounds[0], 0);
		}
	}
	
	/**
	 * Creates a single column of the viewer with given parameters
	 * @param title The title to be set
	 * @param bound The width of the column
	 * @param colNumber The columns number
	 * @return The column with given parameters
	 */
	private TableViewerColumn createColumn(String title, int bound, int colNumber) {
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
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
