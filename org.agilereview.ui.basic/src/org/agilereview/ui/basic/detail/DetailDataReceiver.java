/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.detail;

import java.util.List;

import org.agilereview.core.external.storage.Comment;
import org.agilereview.core.external.storage.Review;
import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;

/**
 * 
 * @author Thilo Rauch (15.10.2012)
 */
public class DetailDataReceiver extends AbstractReviewDataReceiver {
    
    private static DetailDataReceiver instance;
    
    public DetailDataReceiver() {
        super();
        instance = this;
    }
    
    public static DetailDataReceiver getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#getReviewDataViewClass()
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataView> getReviewDataViewClass() {
        return DetailView.class;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#transformData()
     * @author Thilo Rauch (15.10.2012)
     */
    @Override
    protected Object transformData(ReviewSet rawData) {
        // TODO get the right object
        if (rawData != null) {
            Review[] reviews = rawData.toArray(new Review[0]);
            if (reviews.length > 0) {
                List<Comment> comments = reviews[0].getComments();
                return comments.get(0);
            }
            
            //            Iterator<Review> it = rawData.iterator();
            //            if (it.hasNext()) {
            //                return it.next().getComments().get(0);
            //            }
        }
        return null;
    }
}
