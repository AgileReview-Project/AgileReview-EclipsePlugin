/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.export.xls.extension;

import org.agilereview.core.external.definition.IReviewDataReceiver;
import org.agilereview.core.external.storage.ReviewSet;

/**
 * Review Data Receiver for the current {@link ReviewSet}
 * @author Malte Brunnlieb (10.06.2014)
 */
public class ReviewDataReceiver implements IReviewDataReceiver {
    
    /**
     * Last received review set
     */
    private ReviewSet reviewSet;
    
    /**
     * Instance
     */
    private ReviewDataReceiver instance;
    
    /**
     * Returns the instance created by the eclipse framework
     * @return the instance created by the eclipse framework
     * @author Malte Brunnlieb (10.06.2014)
     */
    public ReviewDataReceiver getInstance() {
        return instance;
    }
    
    /**
     * @return the reviewSet
     * @author Malte Brunnlieb (10.06.2014)
     */
    public ReviewSet getReviewSet() {
        return reviewSet;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.core.external.definition.IReviewDataReceiver#setReviewData(org.agilereview.core.external.storage.ReviewSet)
     * @author Malte Brunnlieb (10.06.2014)
     */
    @Override
    public void setReviewData(ReviewSet reviews) {
        reviewSet = reviews;
    }
    
}
