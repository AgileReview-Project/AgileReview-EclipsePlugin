/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.agilereview.ui.basic.commentSummary.filter.ColumnSorter;
import org.agilereview.ui.basic.commentSummary.filter.ExplorerSelectionFilter;
import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.xml.sax.helpers.ParserFactory;

/**
 * Used to provide an overview for review comments using a table
 */
public class CommentTableView extends ViewPart implements IDoubleClickListener {
	
	/**
	 * Current Instance used by the ViewPart
	 */
	private static CommentTableView instance;
	/**
	 * Instance of ReviewAccess
	 */
	private final ReviewAccess ra = ReviewAccess.getInstance();
	/**
	 * Instance of PropertiesManager
	 */
	private final PropertiesManager pm = PropertiesManager.getInstance();
	/**
	 * The comments to be displayed (model of TableViewer viewer)
	 */
	private ArrayList<Comment> comments;
	/**
	 * The view that displays the comments
	 */
	private TableViewer viewer;
	/**
	 * Filter text field
	 */
	private Text filterText;
	/**
	 * Comparator of the view, used to sort columns ascending/descending
	 */
	private ColumnSorter comparator;
	/**
	 * Filter of the view, used to filter by a given search string
	 */
	private AgileCommentFilter commentFilter;
	/**
	 * Filter of the view, used to filter by selected entries of the explorer
	 */
	private ExplorerSelectionFilter selectionFilter = new ExplorerSelectionFilter(new ArrayList<String>(), new HashMap<String, HashSet<String>>());
	/**
	 * The number of columns of the parent's GridLayout
	 */
	private static final int layoutCols = 6;
	/**
	 * The titles of the table's columns, also used to fill the filter menu
	 */
	private final String[] titles = { "ReviewName", "CommentID", "Author", "Recipient", "Status", "Priority", "Date created", "Date modified",
			"Replies", "Location" };
	/**
	 * The width of the table's columns
	 */
	private final int[] bounds = { 60, 70, 70, 70, 70, 70, 120, 120, 50, 100 };
	/**
	 * map of currently opened editors and their annotation parsers
	 */
	private final HashMap<IEditorPart, IAnnotationParser> parserMap = new HashMap<IEditorPart, IAnnotationParser>();
	
	/**
	 * Provides the current used instance of the CommentTableView
	 * @return instance of CommentTableView
	 */
	public static CommentTableView getInstance() {
		return instance;
	}
	
	/**
	 * Used to set a new model
	 * @param comments
	 */
	protected void setTableContent(ArrayList<Comment> comments) {
		this.comments = comments;
		viewer.setInput(comments);
		viewer.refresh();
		PluginLogger.log(this.getClass().toString(), "setTableContent", "Setting table content");
	}
	
	/**
	 * Add a comment to an existing model
	 * @param comment the comment
	 */
	public void addComment(Comment comment) {
		// add comment to (un)filtered model
		PluginLogger.log(this.getClass().toString(), "addComment", "Adding comment to table content");
		this.comments.add(comment);
		
		viewer.setInput(this.comments);
		
		// TODO: Das hier vlt auslagern -> macht CTV dümmer, außerdem liegen z.B. Editor und Selection im Handler vor 
		try {
			IEditorPart editor;
			if ((editor = getActiveEditor()) != null) {
				PluginLogger.log(this.getClass().toString(), "addComment", "Add tags for currently added comment");
				parserMap.get(editor).addTagsInDocument(comment, getFilteredComments().contains(comment));
			}
		} catch (BadLocationException e) {
			PluginLogger.logError(this.getClass().toString(), "addComment", "BadLocationException when trying to add tags", e);
		}
		
		// set selection (to display comment in detail view)
		// getSite().getSelectionProvider().setSelection(new StructuredSelection(comment));
		this.selectComment(comment);
	}
	
	/**
	 * Delete a comment from the model
	 * @param comment the comment
	 */
	public void deleteComment(Comment comment) {
		
		PluginLogger.log(this.getClass().toString(), "deleteComment", "Deleting a comment from table content");
		// add comment to (un)filtered model
		this.comments.remove(comment);
		viewer.setInput(this.comments);
		
		// remove annotation and tags
		try {
			PluginLogger.log(this.getClass().toString(), "deleteComment", "Starting to remove tags for currently deleted comment");
			if (openEditorContains(comment)) {
				parserMap.get(getActiveEditor()).removeCommentTags(comment);
			} else {
				TagCleaner.removeTag(new Path(ReviewAccess.computePath(comment)), generateCommentKey(comment), false);
				reparseAllEditors();
			}
		} catch (BadLocationException e) {
			PluginLogger.logError(this.getClass().toString(), "deleteComment", "BadLocationException when trying to delete comment", e);
		}
	}
	
	/**
	 * Reload current table input
	 */
	public void refreshTable() {
		PluginLogger.log(this.getClass().toString(), "refreshTable", "Reloading current table input");
		viewer.refresh();
		filterComments();
	}
	
	/**
	 * Resets the comments (reloading from model)
	 */
	public void resetComments() {
		PluginLogger.log(this.getClass().toString(), "resetComments", "Reloading comments from model");
		this.comments = ra.getAllComments();
		this.viewer.setInput(this.comments);
		this.refreshTable();
	}
	
	/**
	 * Filter comments based on the viewers filter
	 */
	private void filterComments() {
		PluginLogger.log(this.getClass().toString(), "filterComments", "Starting to filter comments");
		Object[] filteredCommentObjects = this.comments.toArray();
		for (ViewerFilter filter : viewer.getFilters()) {
			filteredCommentObjects = filter.filter(viewer, this, filteredCommentObjects);
		}
		
		IEditorPart editor;
		if ((editor = this.getActiveEditor()) != null) {
			if (this.parserMap.get(editor) != null) {
				this.parserMap.get(editor).filter(getFilteredComments());
			}
		}
	}
	
	//###############################################################################
	//################ functions which creates the view parts #######################
	//###############################################################################
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		PluginLogger.log(this.getClass().toString(), "createPartControl", "CommentTableView will be created");
		instance = this;
		// get comments from CommentController
		this.comments = ra.getAllComments();
		
		// set layout of parent
		GridLayout layout = new GridLayout(layoutCols, false);
		parent.setLayout(layout);
		
		// create UI elements (filter, add-/delete-button)
		createToolBar(parent);
		createViewer(parent);
		
		// set comparator (sorting order of columns) and filter
		comparator = new ColumnSorter();
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
	 * @return viewer The TableView component of this view
	 */
	private TableViewer createViewer(Composite parent) {
		
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
	 * create the toolbar containing filter
	 * @param parent the toolsbar's parent
	 * @return the toolbar
	 */
	private ToolBar createToolBar(final Composite parent) {
		// create toolbar
		final ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		
		// add dropdown box to toolbar to select category to filter
		final ToolItem itemDropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
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
		this.filterText = new Text(toolBar, SWT.BORDER | SWT.SINGLE);
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
					Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
					filterText.setFocus();
				}
			}
		});
		
		toolBar.pack();
		
		return toolBar;
	}
	
	/**
	 * Creates the columns of the viewer and adds label providers to fill cells
	 */
	private void createColumns() {
		// ReviewID
		TableViewerColumn col = createColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return c.getReviewID();
			}
		});
		
		// CommentID
		col = createColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return c.getId();
			}
		});
		
		// Author
		col = createColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return c.getAuthor();
			}
		});
		
		// Recipient
		col = createColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return c.getRecipient();
			}
		});
		
		// Status
		col = createColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				String status = pm.getCommentStatusByID(c.getStatus());
				return status;
			}
		});
		
		// Priority
		col = createColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				String prio = pm.getCommentPriorityByID(c.getPriority());
				return prio;
			}
		});
		
		// Date created
		col = createColumn(titles[6], bounds[6], 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
				return df.format(c.getCreationDate().getTime());
			}
		});
		
		// Date modified
		col = createColumn(titles[7], bounds[7], 7);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				if (c.getLastModified() == null) return "";
				DateFormat df = new SimpleDateFormat("dd.M.yyyy', 'HH:mm:ss");
				return df.format(c.getLastModified().getTime());
			}
		});
		
		// Number of relplies
		col = createColumn(titles[8], bounds[8], 8);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return String.valueOf(c.getReplies().getReplyArray().length);
			}
		});
		
		// Location
		col = createColumn(titles[9], bounds[9], 9);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Comment c = (Comment) element;
				return ReviewAccess.computePath(c);
			}
		});
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
	
	//###############################################################################
	//############################## some helper functions ##########################
	//###############################################################################
	
	/**
	 * Get the selection adapter of a given column
	 * @param column the column
	 * @param index the column's index
	 * @return the columns selection adapter
	 */
	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = viewer.getTable().getSortDirection();
				if (viewer.getTable().getSortColumn() == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					
					dir = SWT.DOWN;
				}
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}
	
	/**
	 * @return the currently active editor or null if no editor is active
	 */
	private IEditorPart getActiveEditor() {
		return getSite().getPage().getActiveEditor();
	}
	
	/**
	 * Proves whether the current open editor contains the tags for the given comment
	 * @param comment the comment
	 * @return true, if the current open editor contains the given comment<br> false, otherwise
	 */
	public boolean openEditorContains(Comment comment) {
		boolean result = false;
		IPath path = new Path(ReviewAccess.computePath(comment));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (getActiveEditor() != null) {
			IFile editorFile = (IFile) getActiveEditor().getEditorInput().getAdapter(IFile.class);
			PluginLogger.log(this.getClass().toString(), "openEditorContains", "File for comment: " + file.getFullPath() + " | Current Editor File: "
					+ editorFile.getFullPath());
			if (file.getFullPath().equals(editorFile.getFullPath())) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Opens an editor for a given comment
	 * @param comment the comment
	 * @return boolean flag, indicating if the editor could be opened
	 */
	private boolean openEditor(Comment comment) {
		PluginLogger.log(this.getClass().toString(), "openEditor", "Opening editor for the given comment");
		IPath path = new Path(ReviewAccess.computePath(comment));
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		
		if (!file.exists()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error while opening file", "Could not open file '" + file.getFullPath()
					+ "'!\nFile not existent in workspace or respective project may be closed!");
			return false;
		}
		
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		try {
			if (desc == null) {
				getSite().getPage().openEditor(new FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor");
			} else {
				getSite().getPage().openEditor(new FileEditorInput(file), desc.getId());
			}
		} catch (PartInitException e) {
			PluginLogger.logError(this.getClass().toString(), "openEditor", "PartInitException occured when opening editor", e);
		}
		return true;
	}
	
	/**
	 * Generates the comment key for the given comment in the following scheme: reviewID|author|commendID
	 * @param comment which comment key should be generated
	 * @return comment key
	 */
	private String generateCommentKey(Comment comment) {
		String keySeparator = pm.getInternalProperty(PropertiesManager.INTERNAL_KEYS.KEY_SEPARATOR);
		String commentTag = comment.getReviewID() + keySeparator + comment.getAuthor() + keySeparator + comment.getId();
		return commentTag;
	}
	
	/**
	 * Returns all currently shown comments of the table
	 * @return comments of all currently displayed comments
	 */
	private HashSet<Comment> getFilteredComments() {
		HashSet<Comment> comments = new HashSet<Comment>();
		for (TableItem i : viewer.getTable().getItems()) {
			if (i.getData() instanceof Comment) {
				comments.add((Comment) i.getData());
			}
		}
		return comments;
	}
	
	//###############################################################################
	//######### functions which provide functionality for AnnotationParser ##########
	//###################################################################O############
	
	/**
	 * Relocates the comment passed to the current selection within the same file
	 * @param comment comment which should be relocated
	 */
	public void relocateComment(Comment comment) {
		IEditorPart editor;
		if ((editor = getActiveEditor()) != null) {
			try {
				parserMap.get(editor).relocateComment(comment, getFilteredComments().contains(comment));
			} catch (BadLocationException e) {
				PluginLogger.logError(this.getClass().toString(), "relocateComment", "BadLocationException when trying to add/remove tags", e);
			}
		}
	}
	
	/**
	 * Clears the current parserMap and deletes all done Annotations.<br> This should be done before refactoring was initiated!
	 */
	public void cleanEditorReferences() {
		PluginLogger.log(this.getClass().toString(), "cleanEditorReferences", "Droping old editors, reparsing active editor");
		//delete all annotations
		for (IAnnotationParser p : this.parserMap.values()) {
			p.clearAnnotations();
		}
		this.parserMap.clear();
		System.gc();
		PluginLogger.log(this.getClass().toString(), "cleanEditorReferences", "Clear parser map and run garbage collector");
	}
	
	/**
	 * Adds the active editor if some is active to the parserMap an creates the respective AnnotationParser for the normal annotation behavior.
	 * <br>This should be done after refactoring.
	 */
	public void resetEditorReferences() {
		PluginLogger.log(this.getClass().toString(), "resetEditorReferences", "Adding and reparsing active editor");
		IEditorPart editor;
		if ((editor = this.getActiveEditor()) != null) {
			if (editor instanceof IEditorPart) {
				this.parserMap.put(editor, ParserFactory.createParser(editor));
				this.parserMap.get(editor).filter(getFilteredComments());
			}
		}
	}
	
	/**
	 * Triggers the {@link AnnotationParser} of the currently active editor to reparse its file
	 */
	public void reparseActiveEditor() {
		IEditorPart editor;
		if ((editor = this.getActiveEditor()) != null && editor instanceof IEditorPart && this.parserMap.get(editor) != null) {
			this.parserMap.get(editor).reload();
			this.parserMap.get(editor).filter(getFilteredComments());
		}
	}
	
	/**
	 * Triggers all {@link AnnotationParser} in the parser map to reparse its file
	 */
	public void reparseAllEditors() {
		for (IAnnotationParser p : this.parserMap.values()) {
			p.reload();
			p.filter(getFilteredComments());
		}
	}
	
	/**
	 * Returns all comments which are overlapping with the given {@link Position}
	 * @param p position
	 * @return all comments which are overlapping with the given {@link Position}
	 */
	public String[] getCommentsByPositionOfActiveEditor(Position p) {
		if (getActiveEditor() != null) {
			IAnnotationParser parser;
			if ((parser = this.parserMap.get(getActiveEditor())) != null) { return parser.getCommentsByPosition(p); }
		}
		return new String[] {};
	}
	
	/**
	 * Computes the next position from the given one on where a comment is located.
	 * @param current The current position
	 * @return The next position or<br>the current position if there is no such position.
	 */
	public Position getNextCommentPosition(Position current) {
		Position next = null;
		if (getActiveEditor() != null) {
			IAnnotationParser parser;
			if ((parser = this.parserMap.get(getActiveEditor())) != null) {
				next = parser.getNextCommentsPosition(current);
			}
		}
		return next == null ? current : next;
	}
	
	//###############################################################################
	//############ implemented and listener triggered functions #####################
	//###############################################################################
	
	/**
	 * Selection of ReviewExplorer changed, filter comments
	 * @param event will be forwarded from the {@link ViewControl}
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection() instanceof IStructuredSelection && !event.getSelection().isEmpty()) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			if (sel.getFirstElement() instanceof AbstractMultipleWrapper) {
				PluginLogger.log(this.getClass().toString(), "selectionChanged", "Selection of ReviewExplorer changed");
				// get selection, selection's iterator, initialize reviewIDs and paths
				Iterator<?> it = sel.iterator();
				ArrayList<String> reviewIDs = new ArrayList<String>();
				HashMap<String, HashSet<String>> paths = new HashMap<String, HashSet<String>>();
				
				// get all selected reviews and paths
				while (it.hasNext()) {
					Object next = it.next();
					if (next instanceof MultipleReviewWrapper) {
						String reviewID = ((MultipleReviewWrapper) next).getWrappedReview().getId();
						if (!reviewIDs.contains(reviewID)) {
							reviewIDs.add(reviewID);
						}
					} else if (next instanceof AbstractMultipleWrapper) {
						String path = ((AbstractMultipleWrapper) next).getPath();
						String reviewID = ((AbstractMultipleWrapper) next).getReviewId();
						if (paths.containsKey(reviewID)) {
							paths.get(reviewID).add(path);
						} else {
							paths.put(reviewID, new HashSet<String>());
							paths.get(reviewID).add(path);
						}
					}
				}
				
				// Remove the old filter, then create the new filter, 
				// so it can be applied directly when needed
				viewer.removeFilter(this.selectionFilter);
				this.selectionFilter = new ExplorerSelectionFilter(reviewIDs, paths);
				
				ICommandService cmdService = (ICommandService) getSite().getService(ICommandService.class);
				Command linkExplorerCommand = cmdService.getCommand("de.tukl.cs.softech.agilereview.views.reviewexplorer.linkexplorer");
				Object state = linkExplorerCommand.getState("org.eclipse.ui.commands.toggleState").getValue();
				// If "Link Editor" is enabled, then filter also
				if ((Boolean) state) {
					PluginLogger.log(this.getClass().toString(), "selectionChanged", "Adding new filter regarding selection of ReviewExplorer");
					// refresh annotations, update list of filtered comments
					viewer.addFilter(this.selectionFilter);
					filterComments();
				}
			}
		}
	}
	
	/**
	 * Editor has been closed, remove from parserMap
	 * @param partRef will be forwarded from the {@link ViewControl}
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) partRef.getPart(false);
			if (editor == null) { return; }
			if (this.parserMap.containsKey(editor)) {
				this.parserMap.remove(editor);
			}
		}
	}
	
	/**
	 * Editor has been brought to top, add annotations
	 * @param partRef will be forwarded from the {@link ViewControl}
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) partRef.getPart(false);
			if (editor == null) { return; }
			if (!this.parserMap.containsKey(editor) && ViewControl.isPerspectiveOpen()) {
				this.parserMap.put(editor, ParserFactory.createParser(editor));
			}
			if (parserMap.containsKey(editor)) {
				parserMap.get(editor).filter(getFilteredComments());
			}
		}
	}
	
	/**
	 * Reset the parser of the editor given by partRef
	 * @param partRef will be forwarded from the {@link ViewControl}
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partInputChanged(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) partRef.getPart(false);
			if (editor == null) { return; }
			if (this.parserMap.containsKey(editor) && ViewControl.isPerspectiveOpen()) {
				parserMap.get(editor).clearAnnotations();
				parserMap.put(editor, ParserFactory.createParser(editor));
				parserMap.get(editor).filter(getFilteredComments());
			}
		}
	}
	
	/**
	 * Removes all annotations if the AgileReview perspective is closed. The method is invoke by {@link:ViewControl}
	 * @param page the workbench page
	 * @param perspective the activated perspective
	 */
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// XXX: The whole method should be treated somewhere else in my opinion
		if (perspective.getId().equals("de.tukl.cs.softech.agilereview.view.AgileReviewPerspective")) {
			PluginLogger.log(this.getClass().toString(), "perspectiveActivated",
					"Adding annotations since AgileReview perspective has been activated");
			if (getActiveEditor() instanceof IEditorPart) {
				this.parserMap.put(getActiveEditor(), ParserFactory.createParser(getActiveEditor()));
				this.parserMap.get(getActiveEditor()).filter(getFilteredComments());
			}
		} else {
			PluginLogger.log(this.getClass().toString(), "perspectiveActivated", "Hiding annotations since current perspective is not 'AgileReview'");
			for (IAnnotationParser parser : this.parserMap.values()) {
				parser.clearAnnotations();
			}
			this.parserMap.clear();
			
			System.gc();
			PluginLogger.log(this.getClass().toString(), "perspectiveActivatedperspectiveActivated", "Clear parser map and run garbage collector");
		}
	}
	
	/**
	 * Open correspondent editor of the comment and jump to beginning of that comment on doubleclick
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		revealComment((Comment) ((IStructuredSelection) event.getSelection()).getFirstElement());
	}
	
	/**
	 * not yet used
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() { /* Do nothing */
	}
	
	/**
	 * Adds the selection filter of the viewer
	 */
	public void addSelectionFilter() {
		viewer.addFilter(this.selectionFilter);
		filterComments();
		viewer.refresh();
	}
	
	/**
	 * Removes the selection filter from the viewer
	 */
	public void removeSelectionFilter() {
		viewer.removeFilter(this.selectionFilter);
		filterComments();
		viewer.refresh();
	}
	
	/**
	 * Sets the focus to the filter text field
	 */
	public void focusFilterField() {
		this.filterText.setFocus();
	}
	
	/**
	 * Selects the given comment in the table
	 * @param c comment to select
	 */
	public void selectComment(Comment c) {
		if (getFilteredComments().contains(c)) {
			viewer.setSelection(new StructuredSelection(c), true);
		} else {
			if (ViewControl.isOpen(DetailView.class)) {
				DetailView.getInstance().selectionChanged(new SelectionChangedEvent(viewer, new StructuredSelection(c)));
			}
		}
	}
	
	/**
	 * Sets the selection to the comment following the last selected one.
	 */
	public void selectNextComment() {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			int index = 0;
			Object comment;
			if (viewer.getSelection().isEmpty()) {
				if (comments.isEmpty()) { return; }
			} else {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				comment = sel.toList().get(sel.size() - 1);
				if (comment instanceof Comment) {
					index = comments.indexOf(comment) + 1;
				}
			}
			if (index < comments.size()) {
				comment = comments.get(index);
			} else {
				comment = comments.get(0);
			}
			viewer.setSelection(new StructuredSelection(comment));
			revealComment((Comment) comment);
		}
	}
	
	/**
	 * Reveal the comment.
	 * @param comment The comment to reveal
	 */
	private void revealComment(Comment comment) {
		if (openEditor(comment)) {
			//jump to comment in opened editor
			try {
				PluginLogger.log(this.getClass().toString(), "doubleClick", "Revealing comment in it's editor");
				this.parserMap.get(getActiveEditor()).revealCommentLocation(generateCommentKey(comment));
			} catch (BadLocationException e) {
				PluginLogger.logError(this.getClass().toString(), "openEditor", "BadLocationException when revealing comment in it's editor", e);
			}
			
			//open Detail View and set Focus
			ViewControl.openView(ViewControl.DETAIL_VIEW);
			if (ViewControl.isOpen(DetailView.class)) {
				selectComment(comment); //select comment another time to show the comment if the view was closed before
			}
		}
	}
	
}