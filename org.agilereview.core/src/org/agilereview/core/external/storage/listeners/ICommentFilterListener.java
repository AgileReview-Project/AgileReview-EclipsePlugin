/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage.listeners;

import java.util.Set;

import org.agilereview.core.external.storage.Comment;

/**
 * The {@link ICommentFilterListener} will be called with the set of the currently visible comments, whenever any comment filter was added or deleted
 * @author Malte Brunnlieb (01.06.2013)
 */
public interface ICommentFilterListener {
    
    /**
     * Sets the currently filtered (visible) comments
     * @param filteredComments the currently filtered (visible) comments
     * @author Malte Brunnlieb (01.06.2013)
     */
    public void setFilteredComments(Set<Comment> filteredComments);
}
