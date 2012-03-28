/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;

/**
 * Table model for the {@link CommentSummaryView} which contents are {@link Comment} objects
 * @author Malte Brunnlieb (22.03.2012)
 */
public class CommentTableModel implements TableModel, IReviewDataReceiver {
	
	/**
	 * The titles of the table's columns, also used to fill the filter menu
	 */
	private final String[] columnNames = { "ReviewName", "CommentID", "Author", "Recipient", "Status", "Priority", "Date created", "Date modified",
			"Replies", "Location" };
	/**
	 * Class objects representing the column entries
	 */
	private final Class<?>[] columnClasses = { Review.class, String.class, String.class, String.class, Integer.class, Integer.class, Date.class,
			Date.class, Integer.class, String.class };
	/**
	 * The comments to be displayed
	 */
	private List<Comment> comments = new LinkedList<Comment>();
	/**
	 * List of added {@link TableModelListener}
	 */
	private final ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public int getRowCount() {
		return comments.size();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Comment comment = comments.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return comment.getReview();
		case 1:
			return comment.getId();
		case 2:
			return comment.getAuthor();
		case 3:
			return comment.getRecipient();
		case 4:
			return comment.getStatus();
		case 5:
			return comment.getPriority();
		case 6:
			return comment.getCreationDate();
		case 7:
			return comment.getModificationDate();
		case 8:
			return comment.getReplies().size();
		case 9:
			return comment.getCommentedFile().getFullPath();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO extend (new feature for v0.9)
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 * @author Malte Brunnlieb (22.03.2012)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO extend (new feature for v0.10)
	}
	
	/* (non-Javadoc)
	 * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(java.util.List)
	 * @author Malte Brunnlieb (28.03.2012)
	 */
	@Override
	public void setReviewData(List<Review> reviews) {
		comments = new LinkedList<Comment>();
		for (Review r : reviews) {
			comments.addAll(r.getComments());
		}
	}
}
