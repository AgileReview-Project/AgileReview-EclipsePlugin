/**
 * Copyright (c) 2011, 2012 AgileReview Development Team and others.
 * All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License - v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Malte Brunnlieb, Philipp Diebold, Peter Reuter, Thilo Rauch
 */
package org.agilereview.ui.basic.reviewExplorer;

import java.beans.PropertyChangeEvent;

import org.agilereview.core.external.storage.ReviewSet;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver;
import org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataView;

/**
 * 
 * @author Thilo Rauch (07.07.2012)
 */
public class REDataReceiver extends AbstractReviewDataReceiver {
    
    /**
     * Current Instance used by the ViewPart. Part of the ReviewDataView pattern.
     */
    private static REDataReceiver instance;
    
    /**
     * Constructor used to capture the instance created by eclipse. Part of the ReviewDataView pattern.
     * @author Thilo Rauch (13.07.2012)
     */
    public REDataReceiver() {
        super();
        instance = this;
    }
    
    /**
     * GetInstance() method to provide access to the instance created by Eclipse. Part of the ReviewDataView pattern.
     * @return instance of this class created by eclipse
     * @author Thilo Rauch (13.07.2012)
     */
    public static AbstractReviewDataReceiver getInstance() {
        return instance;
    }
    
    @Override
    protected Object transformData(ReviewSet rawData) {
        return super.transformData(rawData);
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#getReviewDataViewClass()
     * @author Thilo Rauch (08.07.2012)
     */
    @Override
    protected Class<? extends AbstractReviewDataView> getReviewDataViewClass() {
        return ReviewExplorerView.class;
    }
    
    /* (non-Javadoc)
     * @see org.agilereview.ui.basic.external.reviewDataReceiver.AbstractReviewDataReceiver#triggerPropertyChange(java.beans.PropertyChangeEvent, org.agilereview.core.external.storage.ReviewSet)
     * @author Thilo Rauch (26.11.2012)
     */
    @Override
    protected boolean triggerPropertyChange(PropertyChangeEvent evt, ReviewSet data) {
        return false;
    }
    
}
