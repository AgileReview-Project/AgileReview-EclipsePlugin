/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.detail;

import java.beans.PropertyChangeEvent;

import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.core.external.storage.constants.PropertyChangeEventKeys;
import org.agilereview.core.external.storage.constants.ReviewSetMetaDataKeys;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;

/**
 * 
 * @author Thilo Rauch (15.10.2012)
 */
public class DetailDataReceiver extends AbstractReviewDataReceiver {
    
    private static DetailDataReceiver instance;
    
    private Object currentlyShownObject;
    
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
        if (rawData != null) {
            currentlyShownObject = rawData.getValue(ReviewSetMetaDataKeys.SHOW_IN_DETAIL_VIEW);
            if (currentlyShownObject != null) {
                return currentlyShownObject;
            } else {
                return new Object();
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#triggerPropertyChange(java.beans.PropertyChangeEvent, org.agilereview.core.external.storage.ReviewSet)
     * @author Thilo Rauch (26.11.2012)
     */
    @Override
    protected boolean triggerPropertyChange(PropertyChangeEvent evt, ReviewSet data) {
        return evt.getPropertyName().equals(PropertyChangeEventKeys.REVIEWSET_METADATA) && !evt.getSource().equals(currentlyShownObject);
    }
}
