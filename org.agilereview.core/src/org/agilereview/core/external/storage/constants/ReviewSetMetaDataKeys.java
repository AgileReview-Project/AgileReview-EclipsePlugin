/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.core.external.storage.constants;

import org.agilereview.core.external.storage.ReviewSet;

/**
 * Keys for the {@link ReviewSet#storeValue(String, Object)} method to store any metaData
 * @author Malte Brunnlieb (23.11.2013)
 */
public final class ReviewSetMetaDataKeys {
    
    /**
     * Key to show the attached object in the detail view.
     */
    public final static String SHOW_IN_DETAIL_VIEW = "ReviewSet_metaData_showInDetailView";
    
}
