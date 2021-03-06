/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage;

/**
 * This interface can be used to specify filter logic for filtering the displayed comments
 * @author Malte Brunnlieb (02.06.2013)
 */
public interface ICommentFilter {
    
    /**
     * Determines whether the given comment should be accepted by the filter or not
     * @param comment current {@link Comment}
     * @return <code>true</code> if the {@link Comment} should be displayed<br><code>false</code> , otherwise
     * @author Malte Brunnlieb (02.06.2013)
     */
    public boolean accept(Comment comment);
    
}
