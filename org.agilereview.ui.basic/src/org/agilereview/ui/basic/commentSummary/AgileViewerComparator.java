/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.commentSummary;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * The class compares the comment entries of the comment summary view table
 */
public class AgileViewerComparator extends ViewerComparator {
	
	/**
	 * index of the column that is to be ordered
	 */
	private int propertyIndex;
	/**
	 * descending is defined as "1"
	 */
	private static final int DESCENDING = 1;
	/**
	 * standard direction is descending
	 */
	private int direction = DESCENDING;
	
	/**
	 * constructor of this class
	 */
	public AgileViewerComparator() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}
	
	/**
	 * Set's the column to be ordered
	 * @param column the column's index
	 */
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Comment c1 = (Comment) e1;
		Comment c2 = (Comment) e2;
		int rc = 0;
		// a value < 0 is returned if c1<c2
		// a value = 0 is returned if c1=c2
		// a value > 0 is returned if c1>c2
		switch (propertyIndex) {
		case 0:
			rc = c1.getReviewID().compareTo(c2.getReviewID());
			break;
		case 1:
			rc = c1.getId().compareTo(c2.getId());
			break;
		case 2:
			rc = c1.getAuthor().compareTo(c2.getAuthor());
			break;
		case 3:
			rc = c1.getRecipient().compareTo(c2.getRecipient());
			break;
		case 4:
			rc = c1.getStatus() == c2.getStatus() ? 0 : (c1.getStatus() < c2.getStatus() ? -1 : 1);
			break;
		case 5:
			rc = c1.getPriority() == c2.getPriority() ? 0 : (c1.getPriority() < c2.getPriority() ? -1 : 1);
			break;
		case 6:
			rc = c1.getRevision() == c2.getRevision() ? 0 : (c1.getRevision() < c2.getRevision() ? -1 : 1);
			break;
		case 7:
			rc = c1.getCreationDate().compareTo(c2.getCreationDate());
			break;
		case 8:
			rc = c1.getLastModified().compareTo(c2.getLastModified());
			break;
		case 9:
			rc = c1.getReplies().getReplyArray().length == c2.getReplies().getReplyArray().length ? 0 : (c1.getReplies().getReplyArray().length < c2
					.getReplies().getReplyArray().length ? -1 : 1);
			break;
		case 10:
			rc = ReviewAccess.computePath(c1).compareTo(ReviewAccess.computePath(c2));
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
	
}